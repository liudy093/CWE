package bit.workflowScheduler.grpc.Scheduler.gencode;

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
    comments = "Source: scheduler.proto")
public final class SchedulerGrpc {

  private SchedulerGrpc() {}

  public static final String SERVICE_NAME = "scheduler.Scheduler";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest,
      bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply> getWorkflowTransferMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "WorkflowTransfer",
      requestType = bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest.class,
      responseType = bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest,
      bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply> getWorkflowTransferMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest, bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply> getWorkflowTransferMethod;
    if ((getWorkflowTransferMethod = SchedulerGrpc.getWorkflowTransferMethod) == null) {
      synchronized (SchedulerGrpc.class) {
        if ((getWorkflowTransferMethod = SchedulerGrpc.getWorkflowTransferMethod) == null) {
          SchedulerGrpc.getWorkflowTransferMethod = getWorkflowTransferMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest, bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "scheduler.Scheduler", "WorkflowTransfer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply.getDefaultInstance()))
                  .setSchemaDescriptor(new SchedulerMethodDescriptorSupplier("WorkflowTransfer"))
                  .build();
          }
        }
     }
     return getWorkflowTransferMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest,
      bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply> getInputTaskStateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InputTaskState",
      requestType = bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest.class,
      responseType = bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest,
      bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply> getInputTaskStateMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest, bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply> getInputTaskStateMethod;
    if ((getInputTaskStateMethod = SchedulerGrpc.getInputTaskStateMethod) == null) {
      synchronized (SchedulerGrpc.class) {
        if ((getInputTaskStateMethod = SchedulerGrpc.getInputTaskStateMethod) == null) {
          SchedulerGrpc.getInputTaskStateMethod = getInputTaskStateMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest, bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "scheduler.Scheduler", "InputTaskState"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply.getDefaultInstance()))
                  .setSchemaDescriptor(new SchedulerMethodDescriptorSupplier("InputTaskState"))
                  .build();
          }
        }
     }
     return getInputTaskStateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest,
      bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply> getDeleteWorkflowMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteWorkflow",
      requestType = bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.class,
      responseType = bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest,
      bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply> getDeleteWorkflowMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest, bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply> getDeleteWorkflowMethod;
    if ((getDeleteWorkflowMethod = SchedulerGrpc.getDeleteWorkflowMethod) == null) {
      synchronized (SchedulerGrpc.class) {
        if ((getDeleteWorkflowMethod = SchedulerGrpc.getDeleteWorkflowMethod) == null) {
          SchedulerGrpc.getDeleteWorkflowMethod = getDeleteWorkflowMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest, bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "scheduler.Scheduler", "DeleteWorkflow"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply.getDefaultInstance()))
                  .setSchemaDescriptor(new SchedulerMethodDescriptorSupplier("DeleteWorkflow"))
                  .build();
          }
        }
     }
     return getDeleteWorkflowMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SchedulerStub newStub(io.grpc.Channel channel) {
    return new SchedulerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SchedulerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SchedulerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SchedulerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SchedulerFutureStub(channel);
  }

  /**
   */
  public static abstract class SchedulerImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * 调度器控制器向调度器发送工作流DAG文件
     * </pre>
     */
    public void workflowTransfer(bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply> responseObserver) {
      asyncUnimplementedUnaryCall(getWorkflowTransferMethod(), responseObserver);
    }

    /**
     */
    public void inputTaskState(bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply> responseObserver) {
      asyncUnimplementedUnaryCall(getInputTaskStateMethod(), responseObserver);
    }

    /**
     * <pre>
     * 删除工作流
     * </pre>
     */
    public void deleteWorkflow(bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteWorkflowMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getWorkflowTransferMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest,
                bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply>(
                  this, METHODID_WORKFLOW_TRANSFER)))
          .addMethod(
            getInputTaskStateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest,
                bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply>(
                  this, METHODID_INPUT_TASK_STATE)))
          .addMethod(
            getDeleteWorkflowMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest,
                bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply>(
                  this, METHODID_DELETE_WORKFLOW)))
          .build();
    }
  }

  /**
   */
  public static final class SchedulerStub extends io.grpc.stub.AbstractStub<SchedulerStub> {
    private SchedulerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerStub(channel, callOptions);
    }

    /**
     * <pre>
     * 调度器控制器向调度器发送工作流DAG文件
     * </pre>
     */
    public void workflowTransfer(bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getWorkflowTransferMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void inputTaskState(bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInputTaskStateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 删除工作流
     * </pre>
     */
    public void deleteWorkflow(bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteWorkflowMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SchedulerBlockingStub extends io.grpc.stub.AbstractStub<SchedulerBlockingStub> {
    private SchedulerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 调度器控制器向调度器发送工作流DAG文件
     * </pre>
     */
    public bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply workflowTransfer(bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest request) {
      return blockingUnaryCall(
          getChannel(), getWorkflowTransferMethod(), getCallOptions(), request);
    }

    /**
     */
    public bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply inputTaskState(bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest request) {
      return blockingUnaryCall(
          getChannel(), getInputTaskStateMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 删除工作流
     * </pre>
     */
    public bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply deleteWorkflow(bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteWorkflowMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SchedulerFutureStub extends io.grpc.stub.AbstractStub<SchedulerFutureStub> {
    private SchedulerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SchedulerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SchedulerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SchedulerFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 调度器控制器向调度器发送工作流DAG文件
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply> workflowTransfer(
        bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getWorkflowTransferMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply> inputTaskState(
        bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInputTaskStateMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 删除工作流
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply> deleteWorkflow(
        bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteWorkflowMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_WORKFLOW_TRANSFER = 0;
  private static final int METHODID_INPUT_TASK_STATE = 1;
  private static final int METHODID_DELETE_WORKFLOW = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SchedulerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SchedulerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_WORKFLOW_TRANSFER:
          serviceImpl.workflowTransfer((bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.WorkflowTransferReply>) responseObserver);
          break;
        case METHODID_INPUT_TASK_STATE:
          serviceImpl.inputTaskState((bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.TaskStateReply>) responseObserver);
          break;
        case METHODID_DELETE_WORKFLOW:
          serviceImpl.deleteWorkflow((bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowReply>) responseObserver);
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

  private static abstract class SchedulerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SchedulerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return bit.workflowScheduler.grpc.Scheduler.gencode.SchedulerOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Scheduler");
    }
  }

  private static final class SchedulerFileDescriptorSupplier
      extends SchedulerBaseDescriptorSupplier {
    SchedulerFileDescriptorSupplier() {}
  }

  private static final class SchedulerMethodDescriptorSupplier
      extends SchedulerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SchedulerMethodDescriptorSupplier(String methodName) {
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
      synchronized (SchedulerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SchedulerFileDescriptorSupplier())
              .addMethod(getWorkflowTransferMethod())
              .addMethod(getInputTaskStateMethod())
              .addMethod(getDeleteWorkflowMethod())
              .build();
        }
      }
    }
    return result;
  }
}
