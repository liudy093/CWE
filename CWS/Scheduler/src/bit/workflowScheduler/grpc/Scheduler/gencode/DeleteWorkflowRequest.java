// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scheduler.proto

package bit.workflowScheduler.grpc.Scheduler.gencode;

/**
 * Protobuf type {@code bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest}
 */
public  final class DeleteWorkflowRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest)
    DeleteWorkflowRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use DeleteWorkflowRequest.newBuilder() to construct.
  private DeleteWorkflowRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private DeleteWorkflowRequest() {
    workflowId_ = "";
    customId_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private DeleteWorkflowRequest(
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
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            workflowId_ = s;
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            customId_ = s;
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
    return bit.workflowScheduler.grpc.Scheduler.gencode.SchedulerOuterClass.internal_static_bit_workflowScheduler_grpc_Scheduler_gencode_DeleteWorkflowRequest_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return bit.workflowScheduler.grpc.Scheduler.gencode.SchedulerOuterClass.internal_static_bit_workflowScheduler_grpc_Scheduler_gencode_DeleteWorkflowRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.class, bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.Builder.class);
  }

  public static final int WORKFLOW_ID_FIELD_NUMBER = 1;
  private volatile java.lang.Object workflowId_;
  /**
   * <code>string workflow_id = 1;</code>
   */
  public java.lang.String getWorkflowId() {
    java.lang.Object ref = workflowId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      workflowId_ = s;
      return s;
    }
  }
  /**
   * <code>string workflow_id = 1;</code>
   */
  public com.google.protobuf.ByteString
      getWorkflowIdBytes() {
    java.lang.Object ref = workflowId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      workflowId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CUSTOM_ID_FIELD_NUMBER = 2;
  private volatile java.lang.Object customId_;
  /**
   * <code>string custom_id = 2;</code>
   */
  public java.lang.String getCustomId() {
    java.lang.Object ref = customId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      customId_ = s;
      return s;
    }
  }
  /**
   * <code>string custom_id = 2;</code>
   */
  public com.google.protobuf.ByteString
      getCustomIdBytes() {
    java.lang.Object ref = customId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      customId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    if (!getWorkflowIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, workflowId_);
    }
    if (!getCustomIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, customId_);
    }
    unknownFields.writeTo(output);
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getWorkflowIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, workflowId_);
    }
    if (!getCustomIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, customId_);
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
    if (!(obj instanceof bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest)) {
      return super.equals(obj);
    }
    bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest other = (bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest) obj;

    boolean result = true;
    result = result && getWorkflowId()
        .equals(other.getWorkflowId());
    result = result && getCustomId()
        .equals(other.getCustomId());
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
    hash = (37 * hash) + WORKFLOW_ID_FIELD_NUMBER;
    hash = (53 * hash) + getWorkflowId().hashCode();
    hash = (37 * hash) + CUSTOM_ID_FIELD_NUMBER;
    hash = (53 * hash) + getCustomId().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parseFrom(
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
  public static Builder newBuilder(bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest prototype) {
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
   * Protobuf type {@code bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest)
      bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return bit.workflowScheduler.grpc.Scheduler.gencode.SchedulerOuterClass.internal_static_bit_workflowScheduler_grpc_Scheduler_gencode_DeleteWorkflowRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return bit.workflowScheduler.grpc.Scheduler.gencode.SchedulerOuterClass.internal_static_bit_workflowScheduler_grpc_Scheduler_gencode_DeleteWorkflowRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.class, bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.Builder.class);
    }

    // Construct using bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.newBuilder()
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
      workflowId_ = "";

      customId_ = "";

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return bit.workflowScheduler.grpc.Scheduler.gencode.SchedulerOuterClass.internal_static_bit_workflowScheduler_grpc_Scheduler_gencode_DeleteWorkflowRequest_descriptor;
    }

    public bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest getDefaultInstanceForType() {
      return bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.getDefaultInstance();
    }

    public bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest build() {
      bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest buildPartial() {
      bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest result = new bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest(this);
      result.workflowId_ = workflowId_;
      result.customId_ = customId_;
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
      if (other instanceof bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest) {
        return mergeFrom((bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest other) {
      if (other == bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest.getDefaultInstance()) return this;
      if (!other.getWorkflowId().isEmpty()) {
        workflowId_ = other.workflowId_;
        onChanged();
      }
      if (!other.getCustomId().isEmpty()) {
        customId_ = other.customId_;
        onChanged();
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
      bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object workflowId_ = "";
    /**
     * <code>string workflow_id = 1;</code>
     */
    public java.lang.String getWorkflowId() {
      java.lang.Object ref = workflowId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        workflowId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string workflow_id = 1;</code>
     */
    public com.google.protobuf.ByteString
        getWorkflowIdBytes() {
      java.lang.Object ref = workflowId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        workflowId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string workflow_id = 1;</code>
     */
    public Builder setWorkflowId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      workflowId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string workflow_id = 1;</code>
     */
    public Builder clearWorkflowId() {
      
      workflowId_ = getDefaultInstance().getWorkflowId();
      onChanged();
      return this;
    }
    /**
     * <code>string workflow_id = 1;</code>
     */
    public Builder setWorkflowIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      workflowId_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object customId_ = "";
    /**
     * <code>string custom_id = 2;</code>
     */
    public java.lang.String getCustomId() {
      java.lang.Object ref = customId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        customId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string custom_id = 2;</code>
     */
    public com.google.protobuf.ByteString
        getCustomIdBytes() {
      java.lang.Object ref = customId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        customId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string custom_id = 2;</code>
     */
    public Builder setCustomId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      customId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string custom_id = 2;</code>
     */
    public Builder clearCustomId() {
      
      customId_ = getDefaultInstance().getCustomId();
      onChanged();
      return this;
    }
    /**
     * <code>string custom_id = 2;</code>
     */
    public Builder setCustomIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      customId_ = value;
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


    // @@protoc_insertion_point(builder_scope:bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest)
  }

  // @@protoc_insertion_point(class_scope:bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest)
  private static final bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest();
  }

  public static bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DeleteWorkflowRequest>
      PARSER = new com.google.protobuf.AbstractParser<DeleteWorkflowRequest>() {
    public DeleteWorkflowRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new DeleteWorkflowRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<DeleteWorkflowRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<DeleteWorkflowRequest> getParserForType() {
    return PARSER;
  }

  public bit.workflowScheduler.grpc.Scheduler.gencode.DeleteWorkflowRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

