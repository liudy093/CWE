// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: resource_allocator.proto

package bit.workflowScheduler.grpc.resource_allocator.gencode;

/**
 * <pre>
 * 请求创建调度器POD
 * </pre>
 *
 * Protobuf type {@code bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest}
 */
public  final class CreateSchedulerPodRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest)
    CreateSchedulerPodRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use CreateSchedulerPodRequest.newBuilder() to construct.
  private CreateSchedulerPodRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private CreateSchedulerPodRequest() {
    image_ = "";
    cpu_ = 0L;
    mem_ = 0L;
    env_ = com.google.protobuf.LazyStringArrayList.EMPTY;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private CreateSchedulerPodRequest(
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

            image_ = s;
            break;
          }
          case 16: {

            cpu_ = input.readInt64();
            break;
          }
          case 24: {

            mem_ = input.readInt64();
            break;
          }
          case 34: {
            java.lang.String s = input.readStringRequireUtf8();
            if (!((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
              env_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000008;
            }
            env_.add(s);
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
      if (((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
        env_ = env_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateSchedulerPodRequest_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateSchedulerPodRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.class, bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.Builder.class);
  }

  private int bitField0_;
  public static final int IMAGE_FIELD_NUMBER = 1;
  private volatile java.lang.Object image_;
  /**
   * <pre>
   *镜像
   * </pre>
   *
   * <code>string image = 1;</code>
   */
  public java.lang.String getImage() {
    java.lang.Object ref = image_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      image_ = s;
      return s;
    }
  }
  /**
   * <pre>
   *镜像
   * </pre>
   *
   * <code>string image = 1;</code>
   */
  public com.google.protobuf.ByteString
      getImageBytes() {
    java.lang.Object ref = image_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      image_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CPU_FIELD_NUMBER = 2;
  private long cpu_;
  /**
   * <pre>
   *基本单位 millicore(1Core=1000millicore)
   * </pre>
   *
   * <code>int64 cpu = 2;</code>
   */
  public long getCpu() {
    return cpu_;
  }

  public static final int MEM_FIELD_NUMBER = 3;
  private long mem_;
  /**
   * <pre>
   *基本单位 MiB
   * </pre>
   *
   * <code>int64 mem = 3;</code>
   */
  public long getMem() {
    return mem_;
  }

  public static final int ENV_FIELD_NUMBER = 4;
  private com.google.protobuf.LazyStringList env_;
  /**
   * <pre>
   *需要输入给 POD 的环境变量
   * </pre>
   *
   * <code>repeated string env = 4;</code>
   */
  public com.google.protobuf.ProtocolStringList
      getEnvList() {
    return env_;
  }
  /**
   * <pre>
   *需要输入给 POD 的环境变量
   * </pre>
   *
   * <code>repeated string env = 4;</code>
   */
  public int getEnvCount() {
    return env_.size();
  }
  /**
   * <pre>
   *需要输入给 POD 的环境变量
   * </pre>
   *
   * <code>repeated string env = 4;</code>
   */
  public java.lang.String getEnv(int index) {
    return env_.get(index);
  }
  /**
   * <pre>
   *需要输入给 POD 的环境变量
   * </pre>
   *
   * <code>repeated string env = 4;</code>
   */
  public com.google.protobuf.ByteString
      getEnvBytes(int index) {
    return env_.getByteString(index);
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
    if (!getImageBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, image_);
    }
    if (cpu_ != 0L) {
      output.writeInt64(2, cpu_);
    }
    if (mem_ != 0L) {
      output.writeInt64(3, mem_);
    }
    for (int i = 0; i < env_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, env_.getRaw(i));
    }
    unknownFields.writeTo(output);
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getImageBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, image_);
    }
    if (cpu_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(2, cpu_);
    }
    if (mem_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(3, mem_);
    }
    {
      int dataSize = 0;
      for (int i = 0; i < env_.size(); i++) {
        dataSize += computeStringSizeNoTag(env_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getEnvList().size();
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
    if (!(obj instanceof bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest)) {
      return super.equals(obj);
    }
    bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest other = (bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest) obj;

    boolean result = true;
    result = result && getImage()
        .equals(other.getImage());
    result = result && (getCpu()
        == other.getCpu());
    result = result && (getMem()
        == other.getMem());
    result = result && getEnvList()
        .equals(other.getEnvList());
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
    hash = (37 * hash) + IMAGE_FIELD_NUMBER;
    hash = (53 * hash) + getImage().hashCode();
    hash = (37 * hash) + CPU_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getCpu());
    hash = (37 * hash) + MEM_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getMem());
    if (getEnvCount() > 0) {
      hash = (37 * hash) + ENV_FIELD_NUMBER;
      hash = (53 * hash) + getEnvList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parseFrom(
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
  public static Builder newBuilder(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest prototype) {
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
   * <pre>
   * 请求创建调度器POD
   * </pre>
   *
   * Protobuf type {@code bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest)
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateSchedulerPodRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateSchedulerPodRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.class, bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.Builder.class);
    }

    // Construct using bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.newBuilder()
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
      image_ = "";

      cpu_ = 0L;

      mem_ = 0L;

      env_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000008);
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.ResourceAllocator.internal_static_bit_workflowScheduler_grpc_resource_allocator_gencode_CreateSchedulerPodRequest_descriptor;
    }

    public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest getDefaultInstanceForType() {
      return bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.getDefaultInstance();
    }

    public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest build() {
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest buildPartial() {
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest result = new bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.image_ = image_;
      result.cpu_ = cpu_;
      result.mem_ = mem_;
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        env_ = env_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000008);
      }
      result.env_ = env_;
      result.bitField0_ = to_bitField0_;
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
      if (other instanceof bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest) {
        return mergeFrom((bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest other) {
      if (other == bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest.getDefaultInstance()) return this;
      if (!other.getImage().isEmpty()) {
        image_ = other.image_;
        onChanged();
      }
      if (other.getCpu() != 0L) {
        setCpu(other.getCpu());
      }
      if (other.getMem() != 0L) {
        setMem(other.getMem());
      }
      if (!other.env_.isEmpty()) {
        if (env_.isEmpty()) {
          env_ = other.env_;
          bitField0_ = (bitField0_ & ~0x00000008);
        } else {
          ensureEnvIsMutable();
          env_.addAll(other.env_);
        }
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
      bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.lang.Object image_ = "";
    /**
     * <pre>
     *镜像
     * </pre>
     *
     * <code>string image = 1;</code>
     */
    public java.lang.String getImage() {
      java.lang.Object ref = image_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        image_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     *镜像
     * </pre>
     *
     * <code>string image = 1;</code>
     */
    public com.google.protobuf.ByteString
        getImageBytes() {
      java.lang.Object ref = image_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        image_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     *镜像
     * </pre>
     *
     * <code>string image = 1;</code>
     */
    public Builder setImage(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      image_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *镜像
     * </pre>
     *
     * <code>string image = 1;</code>
     */
    public Builder clearImage() {
      
      image_ = getDefaultInstance().getImage();
      onChanged();
      return this;
    }
    /**
     * <pre>
     *镜像
     * </pre>
     *
     * <code>string image = 1;</code>
     */
    public Builder setImageBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      image_ = value;
      onChanged();
      return this;
    }

    private long cpu_ ;
    /**
     * <pre>
     *基本单位 millicore(1Core=1000millicore)
     * </pre>
     *
     * <code>int64 cpu = 2;</code>
     */
    public long getCpu() {
      return cpu_;
    }
    /**
     * <pre>
     *基本单位 millicore(1Core=1000millicore)
     * </pre>
     *
     * <code>int64 cpu = 2;</code>
     */
    public Builder setCpu(long value) {
      
      cpu_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *基本单位 millicore(1Core=1000millicore)
     * </pre>
     *
     * <code>int64 cpu = 2;</code>
     */
    public Builder clearCpu() {
      
      cpu_ = 0L;
      onChanged();
      return this;
    }

    private long mem_ ;
    /**
     * <pre>
     *基本单位 MiB
     * </pre>
     *
     * <code>int64 mem = 3;</code>
     */
    public long getMem() {
      return mem_;
    }
    /**
     * <pre>
     *基本单位 MiB
     * </pre>
     *
     * <code>int64 mem = 3;</code>
     */
    public Builder setMem(long value) {
      
      mem_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *基本单位 MiB
     * </pre>
     *
     * <code>int64 mem = 3;</code>
     */
    public Builder clearMem() {
      
      mem_ = 0L;
      onChanged();
      return this;
    }

    private com.google.protobuf.LazyStringList env_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureEnvIsMutable() {
      if (!((bitField0_ & 0x00000008) == 0x00000008)) {
        env_ = new com.google.protobuf.LazyStringArrayList(env_);
        bitField0_ |= 0x00000008;
       }
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public com.google.protobuf.ProtocolStringList
        getEnvList() {
      return env_.getUnmodifiableView();
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public int getEnvCount() {
      return env_.size();
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public java.lang.String getEnv(int index) {
      return env_.get(index);
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public com.google.protobuf.ByteString
        getEnvBytes(int index) {
      return env_.getByteString(index);
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public Builder setEnv(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureEnvIsMutable();
      env_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public Builder addEnv(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureEnvIsMutable();
      env_.add(value);
      onChanged();
      return this;
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public Builder addAllEnv(
        java.lang.Iterable<java.lang.String> values) {
      ensureEnvIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, env_);
      onChanged();
      return this;
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public Builder clearEnv() {
      env_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000008);
      onChanged();
      return this;
    }
    /**
     * <pre>
     *需要输入给 POD 的环境变量
     * </pre>
     *
     * <code>repeated string env = 4;</code>
     */
    public Builder addEnvBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      ensureEnvIsMutable();
      env_.add(value);
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


    // @@protoc_insertion_point(builder_scope:bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest)
  }

  // @@protoc_insertion_point(class_scope:bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest)
  private static final bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest();
  }

  public static bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CreateSchedulerPodRequest>
      PARSER = new com.google.protobuf.AbstractParser<CreateSchedulerPodRequest>() {
    public CreateSchedulerPodRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new CreateSchedulerPodRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<CreateSchedulerPodRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CreateSchedulerPodRequest> getParserForType() {
    return PARSER;
  }

  public bit.workflowScheduler.grpc.resource_allocator.gencode.CreateSchedulerPodRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

