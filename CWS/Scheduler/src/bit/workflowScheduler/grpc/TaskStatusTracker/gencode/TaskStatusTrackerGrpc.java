package bit.workflowScheduler.grpc.TaskStatusTracker.gencode;

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
    comments = "Source: TaskStatusTracker.proto")
public final class TaskStatusTrackerGrpc {

  private TaskStatusTrackerGrpc() {}

  public static final String SERVICE_NAME = "TaskStatusTracker.TaskStatusTracker";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest,
      bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply> getInputTaskStateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InputTaskState",
      requestType = bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest.class,
      responseType = bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest,
      bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply> getInputTaskStateMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest, bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply> getInputTaskStateMethod;
    if ((getInputTaskStateMethod = TaskStatusTrackerGrpc.getInputTaskStateMethod) == null) {
      synchronized (TaskStatusTrackerGrpc.class) {
        if ((getInputTaskStateMethod = TaskStatusTrackerGrpc.getInputTaskStateMethod) == null) {
          TaskStatusTrackerGrpc.getInputTaskStateMethod = getInputTaskStateMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest, bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "TaskStatusTracker.TaskStatusTracker", "InputTaskState"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply.getDefaultInstance()))
                  .setSchemaDescriptor(new TaskStatusTrackerMethodDescriptorSupplier("InputTaskState"))
                  .build();
          }
        }
     }
     return getInputTaskStateMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TaskStatusTrackerStub newStub(io.grpc.Channel channel) {
    return new TaskStatusTrackerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TaskStatusTrackerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TaskStatusTrackerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TaskStatusTrackerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TaskStatusTrackerFutureStub(channel);
  }

  /**
   */
  public static abstract class TaskStatusTrackerImplBase implements io.grpc.BindableService {

    /**
     */
    public void inputTaskState(bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply> responseObserver) {
      asyncUnimplementedUnaryCall(getInputTaskStateMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getInputTaskStateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest,
                bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply>(
                  this, METHODID_INPUT_TASK_STATE)))
          .build();
    }
  }

  /**
   */
  public static final class TaskStatusTrackerStub extends io.grpc.stub.AbstractStub<TaskStatusTrackerStub> {
    private TaskStatusTrackerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TaskStatusTrackerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TaskStatusTrackerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TaskStatusTrackerStub(channel, callOptions);
    }

    /**
     */
    public void inputTaskState(bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInputTaskStateMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TaskStatusTrackerBlockingStub extends io.grpc.stub.AbstractStub<TaskStatusTrackerBlockingStub> {
    private TaskStatusTrackerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TaskStatusTrackerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TaskStatusTrackerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TaskStatusTrackerBlockingStub(channel, callOptions);
    }

    /**
     */
    public bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply inputTaskState(bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest request) {
      return blockingUnaryCall(
          getChannel(), getInputTaskStateMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TaskStatusTrackerFutureStub extends io.grpc.stub.AbstractStub<TaskStatusTrackerFutureStub> {
    private TaskStatusTrackerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TaskStatusTrackerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TaskStatusTrackerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TaskStatusTrackerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply> inputTaskState(
        bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInputTaskStateMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INPUT_TASK_STATE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TaskStatusTrackerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TaskStatusTrackerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INPUT_TASK_STATE:
          serviceImpl.inputTaskState((bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStateReply>) responseObserver);
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

  private static abstract class TaskStatusTrackerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TaskStatusTrackerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return bit.workflowScheduler.grpc.TaskStatusTracker.gencode.TaskStatusTrackerOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TaskStatusTracker");
    }
  }

  private static final class TaskStatusTrackerFileDescriptorSupplier
      extends TaskStatusTrackerBaseDescriptorSupplier {
    TaskStatusTrackerFileDescriptorSupplier() {}
  }

  private static final class TaskStatusTrackerMethodDescriptorSupplier
      extends TaskStatusTrackerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TaskStatusTrackerMethodDescriptorSupplier(String methodName) {
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
      synchronized (TaskStatusTrackerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TaskStatusTrackerFileDescriptorSupplier())
              .addMethod(getInputTaskStateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
