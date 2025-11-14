package org.example.memorialbooklet.facade;

import org.example.memorialbooklet.dto.UploadFileResponse;
import org.example.memorialbooklet.ipfs.IpfsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

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
}