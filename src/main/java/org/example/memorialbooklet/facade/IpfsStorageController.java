package org.example.memorialbooklet.facade;

import org.example.memorialbooklet.dto.UploadFileResponse;
import org.example.memorialbooklet.ipfs.IpfsService;
import org.example.memorialbooklet.pedigree.mybatis.type.DigitalLegacyAsset;
import org.example.memorialbooklet.response.FileIpfsDetailResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
// Sets the base path for all methods in this controller
@RequestMapping("/api/ipfs")
@CrossOrigin(origins = "*") // 允许前端跨域调试
public class IpfsStorageController {

    private final IpfsService ipfsService;

    // Standard Spring constructor injection
    public IpfsStorageController(IpfsService ipfsService) {
        this.ipfsService = ipfsService;
    }

    /**
     * Handles file uploads via HTTP POST request.
     *
     * The client should send the raw file content in the request body.
     * We accept ALL media types because the browser might automatically set
     * Content-Type to image/jpeg, application/pdf, etc., and we want to handle them all
     * as a raw stream.
     *
     * @param inputStream The raw data stream of the file content from the HTTP request body.
     * @return ResponseEntity containing the CID and a status message.
     */
    @PostMapping(value = "/upload",
            // Allow any content type (e.g., image/jpeg, application/pdf, application/octet-stream)
            consumes = MediaType.ALL_VALUE,
            // Specifies the output type
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadFileResponse> uploadFile(InputStream inputStream, Long personId) throws IOException {

        // Basic check for an empty request body
        if (inputStream == null) {
            return new ResponseEntity<>(
                    new UploadFileResponse(null, "Request body is empty or not provided."),
                    HttpStatus.BAD_REQUEST
            );
        }

        try {
            // 1. Core Logic: Directly pass the incoming HTTP stream to the IPFS service.
            String cid = ipfsService.uploadFile(inputStream, personId);

            UploadFileResponse responseBody = new UploadFileResponse(
                    cid,
                    "File uploaded and pinned successfully"
            );

            // 2. Return success response with HTTP 200 OK
            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (EOFException e) {
            // Specific handling for empty body or interrupted stream
            System.err.println("IPFS upload failed: Stream ended unexpectedly (EOF). Body might be empty.");
            return new ResponseEntity<>(
                    new UploadFileResponse(null, "Upload failed: Request body is empty or stream interrupted."),
                    HttpStatus.BAD_REQUEST
            );
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

    @GetMapping(value = "/search-files/{personId}")
    public ResponseEntity<List<FileIpfsDetailResponse>> searchFiles(@PathVariable long personId) {
        try {
            List<FileIpfsDetailResponse> assets = ipfsService.findByPersonId(personId);
            return new ResponseEntity<>(assets, HttpStatus.OK);
        } catch (IOException e) {
            System.err.println("IPFS search error for personId " + personId + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}