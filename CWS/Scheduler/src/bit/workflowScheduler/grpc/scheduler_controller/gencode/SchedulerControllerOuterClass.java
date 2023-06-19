// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scheduler_controller.proto

package bit.workflowScheduler.grpc.scheduler_controller.gencode;

public final class SchedulerControllerOuterClass {
  private SchedulerControllerOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveReply_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowReply_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\032scheduler_controller.proto\0227bit.workfl" +
      "owScheduler.grpc.scheduler_controller.ge" +
      "ncode\"|\n\020KeepAliveRequest\022\013\n\003sid\030\001 \001(\t\022\020" +
      "\n\010pressure\030\002 \001(\007\022\020\n\010capacity\030\003 \001(\007\022\025\n\rse" +
      "rial_number\030\004 \001(\004\022\014\n\004ipv4\030\005 \001(\t\022\022\n\nclust" +
      "er_id\030\006 \001(\t\"#\n\016KeepAliveReply\022\021\n\twait_se" +
      "cs\030\001 \001(\005\"(\n\024InputWorkflowRequest\022\020\n\010work" +
      "flow\030\001 \003(\014\"$\n\022InputWorkflowReply\022\016\n\006acce" +
      "pt\030\001 \001(\r2\345\002\n\023SchedulerController\022\237\001\n\tKee" +
      "pAlive\022I.bit.workflowScheduler.grpc.sche" +
      "duler_controller.gencode.KeepAliveReques" +
      "t\032G.bit.workflowScheduler.grpc.scheduler" +
      "_controller.gencode.KeepAliveReply\022\253\001\n\rI" +
      "nputWorkflow\022M.bit.workflowScheduler.grp" +
      "c.scheduler_controller.gencode.InputWork" +
      "flowRequest\032K.bit.workflowScheduler.grpc" +
      ".scheduler_controller.gencode.InputWorkf" +
      "lowReplyB\002P\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveRequest_descriptor,
        new java.lang.String[] { "Sid", "Pressure", "Capacity", "SerialNumber", "Ipv4", "ClusterId", });
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveReply_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_KeepAliveReply_descriptor,
        new java.lang.String[] { "WaitSecs", });
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowRequest_descriptor,
        new java.lang.String[] { "Workflow", });
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowReply_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_bit_workflowScheduler_grpc_scheduler_controller_gencode_InputWorkflowReply_descriptor,
        new java.lang.String[] { "Accept", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
