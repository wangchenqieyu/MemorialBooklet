package org.example.memorialbooklet.facade;

import org.example.memorialbooklet.dto.UploadFileResponse;
import org.example.memorialbooklet.ipfs.IpfsService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
// Sets the base path for all methods in this controller
@RequestMapping("/api/ipfs")
public class IpfsStorageController {

    private final IpfsService ipfsService;

    // Standard Spring constructor injection
    public IpfsStorageController(IpfsService ipfsService) {
        this.ipfsService = ipfsService;
    }

    /**
     * Handles file uploads via HTTP POST request.
     *
     * The client should send the raw file content in the request body
     * with the header 'Content-Type: application/octet-stream'.
     *
     * Spring automatically binds the raw request body stream to the InputStream parameter,
     * which enables efficient, stream-based processing without buffering the entire file
     * in the controller's memory. This replaces the PipedStream logic used in gRPC.
     *
     * @param inputStream The raw data stream of the file content from the HTTP request body.
     * @return ResponseEntity containing the CID and a status message.
     */
    @PostMapping(value = "/upload",
            // Specifies the expected input type for streaming binary data
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,
            // Specifies the output type
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadFileResponse> uploadFile(InputStream inputStream) {

        // Basic check for an empty request body
        if (inputStream == null) {
            return new ResponseEntity<>(
                    new UploadFileResponse(null, "Request body is empty or not provided."),
                    HttpStatus.BAD_REQUEST
            );
        }

        try {
            // 1. Core Logic: Directly pass the incoming HTTP stream to the IPFS service.
            // This is the equivalent of the separate executor thread in the gRPC version,
            // but managed synchronously by the Spring request handling thread.
            String cid = ipfsService.uploadFile(inputStream);

            UploadFileResponse responseBody = new UploadFileResponse(
                    cid,
                    "File uploaded and pinned successfully"
            );

            // 2. Return success response with HTTP 200 OK
            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (IOException e) {
            // Handle IO errors (e.g., issues communicating with IPFS or reading the stream)
            System.err.println("IPFS upload IO error: " + e.getMessage());
            return new ResponseEntity<>(
                    new UploadFileResponse(null, "IPFS upload error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.err.println("Unexpected error during IPFS upload: " + e.getMessage());
            return new ResponseEntity<>(
                    new UploadFileResponse(null, "An unexpected error occurred: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @GetMapping(value = "/download/{cid}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String cid) {
        InputStream fileStream;
        try {
            // 1. 核心逻辑：从 IPFS 服务获取文件流
            fileStream = ipfsService.downloadFile(cid);

            // 2. 构造响应头
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器这是一个附件下载，可以指定文件名 (这里使用 CID 作为文件名)
            // 实际应用中，您可能需要存储原始文件名
            headers.setContentDispositionFormData("attachment",
                    URLEncoder.encode(cid, StandardCharsets.UTF_8));
            // 设置内容类型为二进制流
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 3. 将 InputStream 包装成 Spring 的 InputStreamResource
            InputStreamResource resource = new InputStreamResource(fileStream);

            // 4. 返回 ResponseEntity
            // 注意：我们不设置 Content-Length，让 Spring 自动处理流的长度。
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (IOException e) {
            // 处理文件不存在、连接错误等问题
            System.err.println("IPFS download error for CID " + cid + ": " + e.getMessage());

            // 检查常见的“文件未找到”错误
            if (e.getMessage() != null && e.getMessage().contains("object not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // HTTP 404
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // HTTP 500
        }
    }
}