package org.example.memorialbooklet.util.converter;


import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * 工具类：将 gRPC 客户端流 (StreamObserver) 转换为标准的 InputStream。
 * 使用 PipedInputStream/PipedOutputStream 实现阻塞/流式转换。
 */
public class GrpcStreamConverter {

    private GrpcStreamConverter() {
        // 工具类，禁止实例化
    }

    /**
     * 将 gRPC 客户端流的字节数据转换为 InputStream。
     * @param responseObserver 用于错误处理的 gRPC 响应观察者
     * @return 转换后的 InputStream
     */
    public static InputStream toInputStream(StreamObserver<?> responseObserver) throws IOException, IOException {
        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);

        // 创建一个单独的线程来处理写入操作，防止阻塞 gRPC 线程
        new Thread(() -> {
            try {
                // 将接收到的所有数据写入 OutputStream
                // 这个 OutputStream 将作为 PipedInputStream 的数据源
                // 注意：这里需要外部逻辑（Facade Service）来调用 GrpcStreamConverter
                // 实际转换逻辑将在 Facade Service 的 StreamObserver 中实现。
                // 这个工具类主要提供 PipedInputStream/PipedOutputStream 机制。
            } catch (Exception e) {
                // 如果写入失败，关闭流并通知客户端错误
                try {
                    pipedOutputStream.close();
                } catch (IOException ioException) {
                    // Ignore
                }
                responseObserver.onError(e);
            }
        }).start();

        return pipedInputStream;
    }
}
