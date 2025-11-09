package org.example.memorialbooklet.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.60.0)",
    comments = "Source: ipfs_storage.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class IpfsStorageServiceGrpc {

  private IpfsStorageServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "IpfsStorageService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.example.memorialbooklet.rpc.FileChunk,
      org.example.memorialbooklet.rpc.UploadFileResponse> getUploadFileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UploadFile",
      requestType = org.example.memorialbooklet.rpc.FileChunk.class,
      responseType = org.example.memorialbooklet.rpc.UploadFileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<org.example.memorialbooklet.rpc.FileChunk,
      org.example.memorialbooklet.rpc.UploadFileResponse> getUploadFileMethod() {
    io.grpc.MethodDescriptor<org.example.memorialbooklet.rpc.FileChunk, org.example.memorialbooklet.rpc.UploadFileResponse> getUploadFileMethod;
    if ((getUploadFileMethod = IpfsStorageServiceGrpc.getUploadFileMethod) == null) {
      synchronized (IpfsStorageServiceGrpc.class) {
        if ((getUploadFileMethod = IpfsStorageServiceGrpc.getUploadFileMethod) == null) {
          IpfsStorageServiceGrpc.getUploadFileMethod = getUploadFileMethod =
              io.grpc.MethodDescriptor.<org.example.memorialbooklet.rpc.FileChunk, org.example.memorialbooklet.rpc.UploadFileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UploadFile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.example.memorialbooklet.rpc.FileChunk.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.example.memorialbooklet.rpc.UploadFileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new IpfsStorageServiceMethodDescriptorSupplier("UploadFile"))
              .build();
        }
      }
    }
    return getUploadFileMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static IpfsStorageServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IpfsStorageServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IpfsStorageServiceStub>() {
        @java.lang.Override
        public IpfsStorageServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IpfsStorageServiceStub(channel, callOptions);
        }
      };
    return IpfsStorageServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static IpfsStorageServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IpfsStorageServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IpfsStorageServiceBlockingStub>() {
        @java.lang.Override
        public IpfsStorageServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IpfsStorageServiceBlockingStub(channel, callOptions);
        }
      };
    return IpfsStorageServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static IpfsStorageServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IpfsStorageServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IpfsStorageServiceFutureStub>() {
        @java.lang.Override
        public IpfsStorageServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IpfsStorageServiceFutureStub(channel, callOptions);
        }
      };
    return IpfsStorageServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<org.example.memorialbooklet.rpc.FileChunk> uploadFile(
        io.grpc.stub.StreamObserver<org.example.memorialbooklet.rpc.UploadFileResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getUploadFileMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service IpfsStorageService.
   */
  public static abstract class IpfsStorageServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return IpfsStorageServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service IpfsStorageService.
   */
  public static final class IpfsStorageServiceStub
      extends io.grpc.stub.AbstractAsyncStub<IpfsStorageServiceStub> {
    private IpfsStorageServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IpfsStorageServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IpfsStorageServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<org.example.memorialbooklet.rpc.FileChunk> uploadFile(
        io.grpc.stub.StreamObserver<org.example.memorialbooklet.rpc.UploadFileResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getUploadFileMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service IpfsStorageService.
   */
  public static final class IpfsStorageServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<IpfsStorageServiceBlockingStub> {
    private IpfsStorageServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IpfsStorageServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IpfsStorageServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service IpfsStorageService.
   */
  public static final class IpfsStorageServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<IpfsStorageServiceFutureStub> {
    private IpfsStorageServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IpfsStorageServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IpfsStorageServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_UPLOAD_FILE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_UPLOAD_FILE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.uploadFile(
              (io.grpc.stub.StreamObserver<org.example.memorialbooklet.rpc.UploadFileResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getUploadFileMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              org.example.memorialbooklet.rpc.FileChunk,
              org.example.memorialbooklet.rpc.UploadFileResponse>(
                service, METHODID_UPLOAD_FILE)))
        .build();
  }

  private static abstract class IpfsStorageServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    IpfsStorageServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.example.memorialbooklet.rpc.IpfsStorage.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("IpfsStorageService");
    }
  }

  private static final class IpfsStorageServiceFileDescriptorSupplier
      extends IpfsStorageServiceBaseDescriptorSupplier {
    IpfsStorageServiceFileDescriptorSupplier() {}
  }

  private static final class IpfsStorageServiceMethodDescriptorSupplier
      extends IpfsStorageServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    IpfsStorageServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (IpfsStorageServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new IpfsStorageServiceFileDescriptorSupplier())
              .addMethod(getUploadFileMethod())
              .build();
        }
      }
    }
    return result;
  }
}
