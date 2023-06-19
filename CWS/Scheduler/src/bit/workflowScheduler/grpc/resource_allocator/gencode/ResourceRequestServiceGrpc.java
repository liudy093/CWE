package bit.workflowScheduler.grpc.resource_allocator.gencode;

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
 * <pre>
 *资源请求服务service
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: resource_allocator.proto")
public final class ResourceRequestServiceGrpc {

  private ResourceRequestServiceGrpc() {}

  public static final String SERVICE_NAME = "resource_allocator.ResourceRequestService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo> getGetResourceAllocateInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetResourceAllocateInfo",
      requestType = bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest.class,
      responseType = bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo> getGetResourceAllocateInfoMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo> getGetResourceAllocateInfoMethod;
    if ((getGetResourceAllocateInfoMethod = ResourceRequestServiceGrpc.getGetResourceAllocateInfoMethod) == null) {
      synchronized (ResourceRequestServiceGrpc.class) {
        if ((getGetResourceAllocateInfoMethod = ResourceRequestServiceGrpc.getGetResourceAllocateInfoMethod) == null) {
          ResourceRequestServiceGrpc.getGetResourceAllocateInfoMethod = getGetResourceAllocateInfoMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resource_allocator.ResourceRequestService", "GetResourceAllocateInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceRequestServiceMethodDescriptorSupplier("GetResourceAllocateInfo"))
                  .build();
          }
        }
     }
     return getGetResourceAllocateInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse> getCreateSchedulerPodMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateSchedulerPod",
      requestType = bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.class,
      responseType = bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse> getCreateSchedulerPodMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse> getCreateSchedulerPodMethod;
    if ((getCreateSchedulerPodMethod = ResourceRequestServiceGrpc.getCreateSchedulerPodMethod) == null) {
      synchronized (ResourceRequestServiceGrpc.class) {
        if ((getCreateSchedulerPodMethod = ResourceRequestServiceGrpc.getCreateSchedulerPodMethod) == null) {
          ResourceRequestServiceGrpc.getCreateSchedulerPodMethod = getCreateSchedulerPodMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resource_allocator.ResourceRequestService", "CreateSchedulerPod"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceRequestServiceMethodDescriptorSupplier("CreateSchedulerPod"))
                  .build();
          }
        }
     }
     return getCreateSchedulerPodMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse> getCreateTaskPodMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateTaskPod",
      requestType = bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest.class,
      responseType = bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse> getCreateTaskPodMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse> getCreateTaskPodMethod;
    if ((getCreateTaskPodMethod = ResourceRequestServiceGrpc.getCreateTaskPodMethod) == null) {
      synchronized (ResourceRequestServiceGrpc.class) {
        if ((getCreateTaskPodMethod = ResourceRequestServiceGrpc.getCreateTaskPodMethod) == null) {
          ResourceRequestServiceGrpc.getCreateTaskPodMethod = getCreateTaskPodMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resource_allocator.ResourceRequestService", "CreateTaskPod"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceRequestServiceMethodDescriptorSupplier("CreateTaskPod"))
                  .build();
          }
        }
     }
     return getCreateTaskPodMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse> getDeleteWorkflowNamespaceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteWorkflowNamespace",
      requestType = bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest.class,
      responseType = bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse> getDeleteWorkflowNamespaceMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse> getDeleteWorkflowNamespaceMethod;
    if ((getDeleteWorkflowNamespaceMethod = ResourceRequestServiceGrpc.getDeleteWorkflowNamespaceMethod) == null) {
      synchronized (ResourceRequestServiceGrpc.class) {
        if ((getDeleteWorkflowNamespaceMethod = ResourceRequestServiceGrpc.getDeleteWorkflowNamespaceMethod) == null) {
          ResourceRequestServiceGrpc.getDeleteWorkflowNamespaceMethod = getDeleteWorkflowNamespaceMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resource_allocator.ResourceRequestService", "DeleteWorkflowNamespace"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceRequestServiceMethodDescriptorSupplier("DeleteWorkflowNamespace"))
                  .build();
          }
        }
     }
     return getDeleteWorkflowNamespaceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse> getUpdateSchedulerAliveStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateSchedulerAliveStatus",
      requestType = bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest.class,
      responseType = bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse> getUpdateSchedulerAliveStatusMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse> getUpdateSchedulerAliveStatusMethod;
    if ((getUpdateSchedulerAliveStatusMethod = ResourceRequestServiceGrpc.getUpdateSchedulerAliveStatusMethod) == null) {
      synchronized (ResourceRequestServiceGrpc.class) {
        if ((getUpdateSchedulerAliveStatusMethod = ResourceRequestServiceGrpc.getUpdateSchedulerAliveStatusMethod) == null) {
          ResourceRequestServiceGrpc.getUpdateSchedulerAliveStatusMethod = getUpdateSchedulerAliveStatusMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resource_allocator.ResourceRequestService", "UpdateSchedulerAliveStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceRequestServiceMethodDescriptorSupplier("UpdateSchedulerAliveStatus"))
                  .build();
          }
        }
     }
     return getUpdateSchedulerAliveStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse> getGetAllocatableSchedulerNumMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetAllocatableSchedulerNum",
      requestType = bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest.class,
      responseType = bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest,
      bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse> getGetAllocatableSchedulerNumMethod() {
    io.grpc.MethodDescriptor<bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse> getGetAllocatableSchedulerNumMethod;
    if ((getGetAllocatableSchedulerNumMethod = ResourceRequestServiceGrpc.getGetAllocatableSchedulerNumMethod) == null) {
      synchronized (ResourceRequestServiceGrpc.class) {
        if ((getGetAllocatableSchedulerNumMethod = ResourceRequestServiceGrpc.getGetAllocatableSchedulerNumMethod) == null) {
          ResourceRequestServiceGrpc.getGetAllocatableSchedulerNumMethod = getGetAllocatableSchedulerNumMethod = 
              io.grpc.MethodDescriptor.<bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest, bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "resource_allocator.ResourceRequestService", "GetAllocatableSchedulerNum"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ResourceRequestServiceMethodDescriptorSupplier("GetAllocatableSchedulerNum"))
                  .build();
          }
        }
     }
     return getGetAllocatableSchedulerNumMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ResourceRequestServiceStub newStub(io.grpc.Channel channel) {
    return new ResourceRequestServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ResourceRequestServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ResourceRequestServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ResourceRequestServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ResourceRequestServiceFutureStub(channel);
  }

  /**
   * <pre>
   *资源请求服务service
   * </pre>
   */
  public static abstract class ResourceRequestServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getResourceAllocateInfo(bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getGetResourceAllocateInfoMethod(), responseObserver);
    }

    /**
     */
    public void createSchedulerPod(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateSchedulerPodMethod(), responseObserver);
    }

    /**
     */
    public void createTaskPod(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateTaskPodMethod(), responseObserver);
    }

    /**
     */
    public void deleteWorkflowNamespace(bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteWorkflowNamespaceMethod(), responseObserver);
    }

    /**
     */
    public void updateSchedulerAliveStatus(bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateSchedulerAliveStatusMethod(), responseObserver);
    }

    /**
     */
    public void getAllocatableSchedulerNum(bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetAllocatableSchedulerNumMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetResourceAllocateInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest,
                bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo>(
                  this, METHODID_GET_RESOURCE_ALLOCATE_INFO)))
          .addMethod(
            getCreateSchedulerPodMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest,
                bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse>(
                  this, METHODID_CREATE_SCHEDULER_POD)))
          .addMethod(
            getCreateTaskPodMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest,
                bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse>(
                  this, METHODID_CREATE_TASK_POD)))
          .addMethod(
            getDeleteWorkflowNamespaceMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest,
                bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse>(
                  this, METHODID_DELETE_WORKFLOW_NAMESPACE)))
          .addMethod(
            getUpdateSchedulerAliveStatusMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest,
                bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse>(
                  this, METHODID_UPDATE_SCHEDULER_ALIVE_STATUS)))
          .addMethod(
            getGetAllocatableSchedulerNumMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest,
                bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse>(
                  this, METHODID_GET_ALLOCATABLE_SCHEDULER_NUM)))
          .build();
    }
  }

  /**
   * <pre>
   *资源请求服务service
   * </pre>
   */
  public static final class ResourceRequestServiceStub extends io.grpc.stub.AbstractStub<ResourceRequestServiceStub> {
    private ResourceRequestServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ResourceRequestServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ResourceRequestServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ResourceRequestServiceStub(channel, callOptions);
    }

    /**
     */
    public void getResourceAllocateInfo(bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetResourceAllocateInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createSchedulerPod(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateSchedulerPodMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createTaskPod(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateTaskPodMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteWorkflowNamespace(bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteWorkflowNamespaceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateSchedulerAliveStatus(bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateSchedulerAliveStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getAllocatableSchedulerNum(bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest request,
        io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetAllocatableSchedulerNumMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   *资源请求服务service
   * </pre>
   */
  public static final class ResourceRequestServiceBlockingStub extends io.grpc.stub.AbstractStub<ResourceRequestServiceBlockingStub> {
    private ResourceRequestServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ResourceRequestServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ResourceRequestServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ResourceRequestServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo getResourceAllocateInfo(bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetResourceAllocateInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse createSchedulerPod(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateSchedulerPodMethod(), getCallOptions(), request);
    }

    /**
     */
    public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse createTaskPod(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateTaskPodMethod(), getCallOptions(), request);
    }

    /**
     */
    public bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse deleteWorkflowNamespace(bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteWorkflowNamespaceMethod(), getCallOptions(), request);
    }

    /**
     */
    public bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse updateSchedulerAliveStatus(bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateSchedulerAliveStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse getAllocatableSchedulerNum(bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetAllocatableSchedulerNumMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   *资源请求服务service
   * </pre>
   */
  public static final class ResourceRequestServiceFutureStub extends io.grpc.stub.AbstractStub<ResourceRequestServiceFutureStub> {
    private ResourceRequestServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ResourceRequestServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ResourceRequestServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ResourceRequestServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo> getResourceAllocateInfo(
        bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetResourceAllocateInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse> createSchedulerPod(
        bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateSchedulerPodMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse> createTaskPod(
        bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateTaskPodMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse> deleteWorkflowNamespace(
        bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteWorkflowNamespaceMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse> updateSchedulerAliveStatus(
        bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateSchedulerAliveStatusMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse> getAllocatableSchedulerNum(
        bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetAllocatableSchedulerNumMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_RESOURCE_ALLOCATE_INFO = 0;
  private static final int METHODID_CREATE_SCHEDULER_POD = 1;
  private static final int METHODID_CREATE_TASK_POD = 2;
  private static final int METHODID_DELETE_WORKFLOW_NAMESPACE = 3;
  private static final int METHODID_UPDATE_SCHEDULER_ALIVE_STATUS = 4;
  private static final int METHODID_GET_ALLOCATABLE_SCHEDULER_NUM = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ResourceRequestServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ResourceRequestServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_RESOURCE_ALLOCATE_INFO:
          serviceImpl.getResourceAllocateInfo((bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocateInfo>) responseObserver);
          break;
        case METHODID_CREATE_SCHEDULER_POD:
          serviceImpl.createSchedulerPod((bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodResponse>) responseObserver);
          break;
        case METHODID_CREATE_TASK_POD:
          serviceImpl.createTaskPod((bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse>) responseObserver);
          break;
        case METHODID_DELETE_WORKFLOW_NAMESPACE:
          serviceImpl.deleteWorkflowNamespace((bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.DeleteWorkflowNamespaceResponse>) responseObserver);
          break;
        case METHODID_UPDATE_SCHEDULER_ALIVE_STATUS:
          serviceImpl.updateSchedulerAliveStatus((bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse>) responseObserver);
          break;
        case METHODID_GET_ALLOCATABLE_SCHEDULER_NUM:
          serviceImpl.getAllocatableSchedulerNum((bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumRequest) request,
              (io.grpc.stub.StreamObserver<bit.workflowScheduler.grpc.resource_allocator.gencode.GetSchedulerNumResponse>) responseObserver);
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

  private static abstract class ResourceRequestServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ResourceRequestServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ResourceRequestService");
    }
  }

  private static final class ResourceRequestServiceFileDescriptorSupplier
      extends ResourceRequestServiceBaseDescriptorSupplier {
    ResourceRequestServiceFileDescriptorSupplier() {}
  }

  private static final class ResourceRequestServiceMethodDescriptorSupplier
      extends ResourceRequestServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ResourceRequestServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ResourceRequestServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ResourceRequestServiceFileDescriptorSupplier())
              .addMethod(getGetResourceAllocateInfoMethod())
              .addMethod(getCreateSchedulerPodMethod())
              .addMethod(getCreateTaskPodMethod())
              .addMethod(getDeleteWorkflowNamespaceMethod())
              .addMethod(getUpdateSchedulerAliveStatusMethod())
              .addMethod(getGetAllocatableSchedulerNumMethod())
              .build();
        }
      }
    }
    return result;
  }
}
