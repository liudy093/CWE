package bit.workflowScheduler.grpc.scheduler_controller.gencode;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: scheduler_controller.proto")
public final class SchedulerControllerGrpc {

  private SchedulerControllerGrpc() {}

  public static final String SERVICE_NAME = "scheduler_controller.SchedulerController";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest,
      bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply> getKeepAliveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "KeepAlive",
      requestType = bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest.class,
      responseType = bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest,
      bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply> getKeepAliveMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest, bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply> getKeepAliveMethod;
    if ((getKeepAliveMethod = SchedulerControllerGrpc.getKeepAliveMethod) == null) {
      synchronized (SchedulerControllerGrpc.class) {
        if ((getKeepAliveMethod = SchedulerControllerGrpc.getKeepAliveMethod) == null) {
          SchedulerControllerGrpc.getKeepAliveMethod = getKeepAliveMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest, bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "scheduler_controller.SchedulerController", "KeepAlive"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply.getDefaultInstance()))
                  .setSchemaDescriptor(new SchedulerControllerMethodDescriptorSupplier("KeepAlive"))
                  .build();
          }
        }
     }
     return getKeepAliveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest,
      bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply> getInputWorkflowMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InputWorkflow",
      requestType = bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest.class,
      responseType = bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest,
      bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply> getInputWorkflowMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest, bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply> getInputWorkflowMethod;
    if ((getInputWorkflowMethod = SchedulerControllerGrpc.getInputWorkflowMethod) == null) {
      synchronized (SchedulerControllerGrpc.class) {
        if ((getInputWorkflowMethod = SchedulerControllerGrpc.getInputWorkflowMethod) == null) {
          SchedulerControllerGrpc.getInputWorkflowMethod = getInputWorkflowMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest, bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
            		  "scheduler_controller.SchedulerController", "InputWorkflow"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply.getDefaultInstance()))
                  .setSchemaDescriptor(new SchedulerControllerMethodDescriptorSupplier("InputWorkflow"))
                  .build();
          }
        }
     }
     return getInputWorkflowMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SchedulerControllerStub newStub(io.grpc.Channel channel) {
    return new SchedulerControllerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SchedulerControllerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SchedulerControllerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SchedulerControllerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SchedulerControllerFutureStub(channel);
  }

  /**
   */
  public static abstract class SchedulerControllerImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * 调度器内保活器向调度器控制器发送保活信号
     * </pre>
     */
    public void keepAlive(bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply> responseObserver) {
      asyncUnimplementedUnaryCall(getKeepAliveMethod(), responseObserver);
    }

    /**
     * <pre>
     * 前端输入工作流
     * </pre>
     */
    public void inputWorkflow(bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply> responseObserver) {
      asyncUnimplementedUnaryCall(getInputWorkflowMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getKeepAliveMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest,
                bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply>(
                  this, METHODID_KEEP_ALIVE)))
          .addMethod(
            getInputWorkflowMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest,
                bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply>(
                  this, METHODID_INPUT_WORKFLOW)))
          .build();
    }
  }

  /**
   */
  public static final class SchedulerControllerStub extends io.grpc.stub.AbstractStub<SchedulerControllerStub> {
    private SchedulerControllerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerControllerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerControllerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerControllerStub(channel, callOptions);
    }

    /**
     * <pre>
     * 调度器内保活器向调度器控制器发送保活信号
     * </pre>
     */
    public void keepAlive(bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getKeepAliveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 前端输入工作流
     * </pre>
     */
    public void inputWorkflow(bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInputWorkflowMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SchedulerControllerBlockingStub extends io.grpc.stub.AbstractStub<SchedulerControllerBlockingStub> {
    private SchedulerControllerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerControllerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerControllerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerControllerBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 调度器内保活器向调度器控制器发送保活信号
     * </pre>
     */
    public bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply keepAlive(bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest request) {
      return blockingUnaryCall(
          getChannel(), getKeepAliveMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 前端输入工作流
     * </pre>
     */
    public bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply inputWorkflow(bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest request) {
      return blockingUnaryCall(
          getChannel(), getInputWorkflowMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SchedulerControllerFutureStub extends io.grpc.stub.AbstractStub<SchedulerControllerFutureStub> {
    private SchedulerControllerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerControllerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerControllerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerControllerFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 调度器内保活器向调度器控制器发送保活信号
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply> keepAlive(
        bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getKeepAliveMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 前端输入工作流
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply> inputWorkflow(
        bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInputWorkflowMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_KEEP_ALIVE = 0;
  private static final int METHODID_INPUT_WORKFLOW = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SchedulerControllerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SchedulerControllerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_KEEP_ALIVE:
          serviceImpl.keepAlive((bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.scheduler_controller.gencode.KeepAliveReply>) responseObserver);
          break;
        case METHODID_INPUT_WORKFLOW:
          serviceImpl.inputWorkflow((bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.scheduler_controller.gencode.InputWorkflowReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class SchedulerControllerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SchedulerControllerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return bit.workflowScheduler.grpc.scheduler_controller.gencode.SchedulerControllerOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SchedulerController");
    }
  }

  private static final class SchedulerControllerFileDescriptorSupplier
      extends SchedulerControllerBaseDescriptorSupplier {
    SchedulerControllerFileDescriptorSupplier() {}
  }

  private static final class SchedulerControllerMethodDescriptorSupplier
      extends SchedulerControllerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SchedulerControllerMethodDescriptorSupplier(String methodName) {
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
      synchronized (SchedulerControllerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SchedulerControllerFileDescriptorSupplier())
              .addMethod(getKeepAliveMethod())
              .addMethod(getInputWorkflowMethod())
              .build();
        }
      }
    }
    return result;
  }
}
