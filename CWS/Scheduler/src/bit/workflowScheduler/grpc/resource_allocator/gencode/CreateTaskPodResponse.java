// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: resource_allocator.proto

package bit.workflowScheduler.grpc.resource_allocator.gencode;

/**
 * Protobuf type {@code bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse}
 */
public  final class CreateTaskPodResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse)
    CreateTaskPodResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use CreateTaskPodResponse.newBuilder() to construct.
  private CreateTaskPodResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private CreateTaskPodResponse() {
    result_ = 0;
    volumePath_ = "";
    errNo_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private CreateTaskPodResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 8: {

            result_ = input.readInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            volumePath_ = s;
            break;
          }
          case 24: {

            errNo_ = input.readInt32();
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateTaskPodResponse_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateTaskPodResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.class, bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.Builder.class);
  }

  public static final int RESULT_FIELD_NUMBER = 1;
  private int result_;
  /**
   * <pre>
   *成功创建 pod 的状态码（成功&gt;=1，失败置为0）
   * </pre>
   *
   * <code>int32 result = 1;</code>
   */
  public int getResult() {
    return result_;
  }

  public static final int VOLUMEPATH_FIELD_NUMBER = 2;
  private volatile java.lang.Object volumePath_;
  /**
   * <pre>
   *pod共享存储路径
   * </pre>
   *
   * <code>string volumePath = 2;</code>
   */
  public java.lang.String getVolumePath() {
    java.lang.Object ref = volumePath_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      volumePath_ = s;
      return s;
    }
  }
  /**
   * <pre>
   *pod共享存储路径
   * </pre>
   *
   * <code>string volumePath = 2;</code>
   */
  public com.google.protobuf.ByteString
      getVolumePathBytes() {
    java.lang.Object ref = volumePath_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      volumePath_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ERR_NO_FIELD_NUMBER = 3;
  private int errNo_;
  /**
   * <pre>
   *在失败状态下，可以设置状态码
   *成功状态（result&gt;=1），客户端不关系此字段，置为 0 即可
   * </pre>
   *
   * <code>int32 err_no = 3;</code>
   */
  public int getErrNo() {
    return errNo_;
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (result_ != 0) {
      output.writeInt32(1, result_);
    }
    if (!getVolumePathBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, volumePath_);
    }
    if (errNo_ != 0) {
      output.writeInt32(3, errNo_);
    }
    unknownFields.writeTo(output);
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (result_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, result_);
    }
    if (!getVolumePathBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, volumePath_);
    }
    if (errNo_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, errNo_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse)) {
      return super.equals(obj);
    }
    bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse other = (bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse) obj;

    boolean result = true;
    result = result && (getResult()
        == other.getResult());
    result = result && getVolumePath()
        .equals(other.getVolumePath());
    result = result && (getErrNo()
        == other.getErrNo());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + RESULT_FIELD_NUMBER;
    hash = (53 * hash) + getResult();
    hash = (37 * hash) + VOLUMEPATH_FIELD_NUMBER;
    hash = (53 * hash) + getVolumePath().hashCode();
    hash = (37 * hash) + ERR_NO_FIELD_NUMBER;
    hash = (53 * hash) + getErrNo();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse)
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateTaskPodResponse_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateTaskPodResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.class, bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.Builder.class);
    }

    // Construct using bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      result_ = 0;

      volumePath_ = "";

      errNo_ = 0;

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateTaskPodResponse_descriptor;
    }

    public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse getDefaultInstanceForType() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.getDefaultInstance();
    }

    public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse build() {
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse buildPartial() {
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse result = new bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse(this);
      result.result_ = result_;
      result.volumePath_ = volumePath_;
      result.errNo_ = errNo_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse) {
        return mergeFrom((bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse other) {
      if (other == bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse.getDefaultInstance()) return this;
      if (other.getResult() != 0) {
        setResult(other.getResult());
      }
      if (!other.getVolumePath().isEmpty()) {
        volumePath_ = other.volumePath_;
        onChanged();
      }
      if (other.getErrNo() != 0) {
        setErrNo(other.getErrNo());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int result_ ;
    /**
     * <pre>
     *成功创建 pod 的状态码（成功&gt;=1，失败置为0）
     * </pre>
     *
     * <code>int32 result = 1;</code>
     */
    public int getResult() {
      return result_;
    }
    /**
     * <pre>
     *成功创建 pod 的状态码（成功&gt;=1，失败置为0）
     * </pre>
     *
     * <code>int32 result = 1;</code>
     */
    public Builder setResult(int value) {
      
      result_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *成功创建 pod 的状态码（成功&gt;=1，失败置为0）
     * </pre>
     *
     * <code>int32 result = 1;</code>
     */
    public Builder clearResult() {
      
      result_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object volumePath_ = "";
    /**
     * <pre>
     *pod共享存储路径
     * </pre>
     *
     * <code>string volumePath = 2;</code>
     */
    public java.lang.String getVolumePath() {
      java.lang.Object ref = volumePath_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        volumePath_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     *pod共享存储路径
     * </pre>
     *
     * <code>string volumePath = 2;</code>
     */
    public com.google.protobuf.ByteString
        getVolumePathBytes() {
      java.lang.Object ref = volumePath_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        volumePath_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     *pod共享存储路径
     * </pre>
     *
     * <code>string volumePath = 2;</code>
     */
    public Builder setVolumePath(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      volumePath_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *pod共享存储路径
     * </pre>
     *
     * <code>string volumePath = 2;</code>
     */
    public Builder clearVolumePath() {
      
      volumePath_ = getDefaultInstance().getVolumePath();
      onChanged();
      return this;
    }
    /**
     * <pre>
     *pod共享存储路径
     * </pre>
     *
     * <code>string volumePath = 2;</code>
     */
    public Builder setVolumePathBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      volumePath_ = value;
      onChanged();
      return this;
    }

    private int errNo_ ;
    /**
     * <pre>
     *在失败状态下，可以设置状态码
     *成功状态（result&gt;=1），客户端不关系此字段，置为 0 即可
     * </pre>
     *
     * <code>int32 err_no = 3;</code>
     */
    public int getErrNo() {
      return errNo_;
    }
    /**
     * <pre>
     *在失败状态下，可以设置状态码
     *成功状态（result&gt;=1），客户端不关系此字段，置为 0 即可
     * </pre>
     *
     * <code>int32 err_no = 3;</code>
     */
    public Builder setErrNo(int value) {
      
      errNo_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *在失败状态下，可以设置状态码
     *成功状态（result&gt;=1），客户端不关系此字段，置为 0 即可
     * </pre>
     *
     * <code>int32 err_no = 3;</code>
     */
    public Builder clearErrNo() {
      
      errNo_ = 0;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse)
  }

  // @@protoc_insertion_point(class_scope:bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse)
  private static final bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse();
  }

  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CreateTaskPodResponse>
      PARSER = new com.google.protobuf.AbstractParser<CreateTaskPodResponse>() {
    public CreateTaskPodResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new CreateTaskPodResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<CreateTaskPodResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CreateTaskPodResponse> getParserForType() {
    return PARSER;
  }

  public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateTaskPodResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
