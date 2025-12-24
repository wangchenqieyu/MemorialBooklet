package org.example.memorialbooklet.facade;


import com.couchbase.client.core.deps.io.grpc.stub.StreamObserver;
import org.example.memorialbooklet.ipfs.IpfsService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@GrpcService
public class IpfsStorageServiceFacadeImpl extends org.example.memorialbooklet.rpc.IpfsStorageServiceGrpc.IpfsStorageServiceImplBase {

    private final IpfsService ipfsService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public IpfsStorageServiceFacadeImpl(IpfsService ipfsService) {
        this.ipfsService = ipfsService;
    }

    public StreamObserver<org.example.memorialbooklet.rpc.FileChunk> uploadFile(StreamObserver<org.example.memorialbooklet.rpc.UploadFileResponse> responseObserver) {

        PipedOutputStream pipedOutputStream;
        PipedInputStream pipedInputStream;

        try {
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream = new PipedInputStream(pipedOutputStream);
        } catch (IOException e) {
            responseObserver.onError(e);
            return null;
        }

        executorService.execute(() -> {
            try (PipedInputStream is = pipedInputStream; PipedOutputStream os = pipedOutputStream) {

                String cid = ipfsService.uploadFile(is, 1L);

                org.example.memorialbooklet.rpc.UploadFileResponse response = org.example.memorialbooklet.rpc.UploadFileResponse.newBuilder()
                        .setCid(cid)
                        .setMessage("File uploaded and pinned successfully")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();

            } catch (IOException e) {
                responseObserver.onError(new RuntimeException("IPFS upload error: " + e.getMessage(), e));
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        });

        // 2. gRPC Stream Observer: Receives chunks from the client
        return new StreamObserver<org.example.memorialbooklet.rpc.FileChunk>() {

            // This is the observer that receives the chunks from the client
            @Override
            public void onNext(org.example.memorialbooklet.rpc.FileChunk chunk) {
                try {
                    // We only care about the data chunks for writing to the output stream
                    if (chunk.getPayloadCase() == org.example.memorialbooklet.rpc.FileChunk.PayloadCase.DATA) {
                        // Write data directly to PipedOutputStream
                        pipedOutputStream.write(chunk.getData().toByteArray());
                    }
                    // Optionally handle metadata here if needed (e.g., logging file name)
                } catch (Exception e) {
                    onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
                try {
                    pipedOutputStream.close();
                } catch (IOException ignored) {}
            }

            @Override
            public void onCompleted() {
                try {
                    pipedOutputStream.close();
                } catch (IOException e) {
                    onError(e);
                }
            }
        };
    }

}
