// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: resource_allocator.proto

package bit.workflowScheduler.grpc.resource_allocator.gencode;

public interface UpdateSchedulerAliveStatusResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:bit.workflowScheduler.grpc.resource_allocator.gencode.UpdateSchedulerAliveStatusResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   *成功更新调度器保活状态的状态码（成功&gt;=1，失败置为0）
   * </pre>
   *
   * <code>int32 result = 1;</code>
   */
  int getResult();

  /**
   * <pre>
   *在失败状态下，可以设置状态码
   *客户端不关系此字段，置为 0 即可
   * </pre>
   *
   * <code>int32 err_no = 2;</code>
   */
  int getErrNo();
}
