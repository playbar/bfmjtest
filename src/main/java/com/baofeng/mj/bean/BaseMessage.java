// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: basemessage.proto

package com.baofeng.mj.bean;

public final class BaseMessage {
    private BaseMessage() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    /**
     * Protobuf enum {@code com.baofeng.mj.bean.MessageType}
     */
    public enum MessageType
            implements com.google.protobuf.Internal.EnumLite {
        /**
         * <code>MessageType_HeartBeat = 1;</code>
         * <p>
         * <pre>
         * ����ҵ���߼�����Ϣ
         * </pre>
         */
        MessageType_HeartBeat(0, 1),
        /**
         * <code>MessageType_Login = 2;</code>
         * <p>
         * <pre>
         * ��½ҵ���߼�����Ϣ
         * </pre>
         */
        MessageType_Login(1, 2),
        /**
         * <code>MessageType_KeySet = 3;</code>
         * <p>
         * <pre>
         * ��������ҵ���߼�����Ϣ
         * </pre>
         */
        MessageType_KeySet(2, 3),
        /**
         * <code>MessageType_Game = 4;</code>
         * <p>
         * <pre>
         * ��Ϸҵ���߼�����Ϣ
         * </pre>
         */
        MessageType_Game(3, 4),
        /**
         * <code>MessageType_Music = 5;</code>
         * <p>
         * <pre>
         * ����ҵ���߼�����Ϣ
         * </pre>
         */
        MessageType_Music(4, 5),
        /**
         * <code>MessageType_Video = 6;</code>
         * <p>
         * <pre>
         * ��Ƶҵ���߼�����Ϣ
         * </pre>
         */
        MessageType_Video(5, 6),
        /**
         * <code>MessageType_Picture = 7;</code>
         * <p>
         * <pre>
         * ͼƬҵ���߼�����Ϣ
         * </pre>
         */
        MessageType_Picture(6, 7),
        /**
         * <code>MessageType_Stat = 8;</code>
         * <p>
         * <pre>
         * ����ͳ��ҵ���߼�����Ϣ
         * </pre>
         */
        MessageType_Stat(7, 8),
        /**
         * <code>MessageType_NEOGEOKeySet = 9;</code>
         * <p>
         * <pre>
         * NEOGEO�������õ���Ϣ
         * </pre>
         */
        MessageType_NEOGEOKeySet(8, 9),
        /**
         * <code>MessageType_PJ64KeySet = 10;</code>
         * <p>
         * <pre>
         * Project64����������Ϣ
         * </pre>
         */
        MessageType_PJ64KeySet(9, 10),
        /**
         * <code>MessageType_FSSearch = 11;</code>
         * <p>
         * <pre>
         * ��������
         * </pre>
         */
        MessageType_FSSearch(10, 11),
        /**
         * <code>MessageType_HeartBeatACK = 12;</code>
         * <p>
         * <pre>
         * ����Ӧ��
         * </pre>
         */
        MessageType_HeartBeatACK(11, 12),;

        /**
         * <code>MessageType_HeartBeat = 1;</code>
         * <p>
         * <pre>
         * ����ҵ���߼�����Ϣ
         * </pre>
         */
        public static final int MessageType_HeartBeat_VALUE = 1;
        /**
         * <code>MessageType_Login = 2;</code>
         * <p>
         * <pre>
         * ��½ҵ���߼�����Ϣ
         * </pre>
         */
        public static final int MessageType_Login_VALUE = 2;
        /**
         * <code>MessageType_KeySet = 3;</code>
         * <p>
         * <pre>
         * ��������ҵ���߼�����Ϣ
         * </pre>
         */
        public static final int MessageType_KeySet_VALUE = 3;
        /**
         * <code>MessageType_Game = 4;</code>
         * <p>
         * <pre>
         * ��Ϸҵ���߼�����Ϣ
         * </pre>
         */
        public static final int MessageType_Game_VALUE = 4;
        /**
         * <code>MessageType_Music = 5;</code>
         * <p>
         * <pre>
         * ����ҵ���߼�����Ϣ
         * </pre>
         */
        public static final int MessageType_Music_VALUE = 5;
        /**
         * <code>MessageType_Video = 6;</code>
         * <p>
         * <pre>
         * ��Ƶҵ���߼�����Ϣ
         * </pre>
         */
        public static final int MessageType_Video_VALUE = 6;
        /**
         * <code>MessageType_Picture = 7;</code>
         * <p>
         * <pre>
         * ͼƬҵ���߼�����Ϣ
         * </pre>
         */
        public static final int MessageType_Picture_VALUE = 7;
        /**
         * <code>MessageType_Stat = 8;</code>
         * <p>
         * <pre>
         * ����ͳ��ҵ���߼�����Ϣ
         * </pre>
         */
        public static final int MessageType_Stat_VALUE = 8;
        /**
         * <code>MessageType_NEOGEOKeySet = 9;</code>
         * <p>
         * <pre>
         * NEOGEO�������õ���Ϣ
         * </pre>
         */
        public static final int MessageType_NEOGEOKeySet_VALUE = 9;
        /**
         * <code>MessageType_PJ64KeySet = 10;</code>
         * <p>
         * <pre>
         * Project64����������Ϣ
         * </pre>
         */
        public static final int MessageType_PJ64KeySet_VALUE = 10;
        /**
         * <code>MessageType_FSSearch = 11;</code>
         * <p>
         * <pre>
         * ��������
         * </pre>
         */
        public static final int MessageType_FSSearch_VALUE = 11;
        /**
         * <code>MessageType_HeartBeatACK = 12;</code>
         * <p>
         * <pre>
         * ����Ӧ��
         * </pre>
         */
        public static final int MessageType_HeartBeatACK_VALUE = 12;


        public final int getNumber() {
            return value;
        }

        public static MessageType valueOf(int value) {
            switch (value) {
                case 1:
                    return MessageType_HeartBeat;
                case 2:
                    return MessageType_Login;
                case 3:
                    return MessageType_KeySet;
                case 4:
                    return MessageType_Game;
                case 5:
                    return MessageType_Music;
                case 6:
                    return MessageType_Video;
                case 7:
                    return MessageType_Picture;
                case 8:
                    return MessageType_Stat;
                case 9:
                    return MessageType_NEOGEOKeySet;
                case 10:
                    return MessageType_PJ64KeySet;
                case 11:
                    return MessageType_FSSearch;
                case 12:
                    return MessageType_HeartBeatACK;
                default:
                    return null;
            }
        }

        public static com.google.protobuf.Internal.EnumLiteMap<MessageType>
        internalGetValueMap() {
            return internalValueMap;
        }

        private static com.google.protobuf.Internal.EnumLiteMap<MessageType>
                internalValueMap =
                new com.google.protobuf.Internal.EnumLiteMap<MessageType>() {
                    public MessageType findValueByNumber(int number) {
                        return MessageType.valueOf(number);
                    }
                };

        private final int value;

        private MessageType(int index, int value) {
            this.value = value;
        }

        // @@protoc_insertion_point(enum_scope:com.baofeng.mj.bean.MessageType)
    }

    public interface BasicMessageOrBuilder
            extends com.google.protobuf.MessageLiteOrBuilder {

        // required .com.baofeng.mj.bean.MessageType mt = 1;

        /**
         * <code>required .com.baofeng.mj.bean.MessageType mt = 1;</code>
         */
        boolean hasMt();

        /**
         * <code>required .com.baofeng.mj.bean.MessageType mt = 1;</code>
         */
        com.baofeng.mj.bean.BaseMessage.MessageType getMt();

        // optional string detailMsg = 2;

        /**
         * <code>optional string detailMsg = 2;</code>
         */
        boolean hasDetailMsg();

        /**
         * <code>optional string detailMsg = 2;</code>
         */
        java.lang.String getDetailMsg();

        /**
         * <code>optional string detailMsg = 2;</code>
         */
        com.google.protobuf.ByteString
        getDetailMsgBytes();
    }

    /**
     * Protobuf type {@code com.baofeng.mj.bean.BasicMessage}
     * <p>
     * <pre>
     * ��Ϣ�ܰ�
     * @mt :����ҵ��������Ϣ
     * @detailMsg:����ҵ���߼���Ϣ��protobuf
     * </pre>
     */
    public static final class BasicMessage extends
            com.google.protobuf.GeneratedMessageLite
            implements BasicMessageOrBuilder {
        // Use BasicMessage.newBuilder() to construct.
        private BasicMessage(com.google.protobuf.GeneratedMessageLite.Builder builder) {
            super(builder);

        }

        private BasicMessage(boolean noInit) {
        }

        private static final BasicMessage defaultInstance;

        public static BasicMessage getDefaultInstance() {
            return defaultInstance;
        }

        public BasicMessage getDefaultInstanceForType() {
            return defaultInstance;
        }

        private BasicMessage(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            initFields();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!parseUnknownField(input,
                                    extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {
                            int rawValue = input.readEnum();
                            com.baofeng.mj.bean.BaseMessage.MessageType value = com.baofeng.mj.bean.BaseMessage.MessageType.valueOf(rawValue);
                            if (value != null) {
                                bitField0_ |= 0x00000001;
                                mt_ = value;
                            }
                            break;
                        }
                        case 18: {
                            bitField0_ |= 0x00000002;
                            detailMsg_ = input.readBytes();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e.getMessage()).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }

        public static com.google.protobuf.Parser<BasicMessage> PARSER =
                new com.google.protobuf.AbstractParser<BasicMessage>() {
                    public BasicMessage parsePartialFrom(
                            com.google.protobuf.CodedInputStream input,
                            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                            throws com.google.protobuf.InvalidProtocolBufferException {
                        return new BasicMessage(input, extensionRegistry);
                    }
                };

        @java.lang.Override
        public com.google.protobuf.Parser<BasicMessage> getParserForType() {
            return PARSER;
        }

        private int bitField0_;
        // required .com.baofeng.mj.bean.MessageType mt = 1;
        public static final int MT_FIELD_NUMBER = 1;
        private com.baofeng.mj.bean.BaseMessage.MessageType mt_;

        /**
         * <code>required .com.baofeng.mj.bean.MessageType mt = 1;</code>
         */
        public boolean hasMt() {
            return ((bitField0_ & 0x00000001) == 0x00000001);
        }

        /**
         * <code>required .com.baofeng.mj.bean.MessageType mt = 1;</code>
         */
        public com.baofeng.mj.bean.BaseMessage.MessageType getMt() {
            return mt_;
        }

        // optional string detailMsg = 2;
        public static final int DETAILMSG_FIELD_NUMBER = 2;
        private java.lang.Object detailMsg_;

        /**
         * <code>optional string detailMsg = 2;</code>
         */
        public boolean hasDetailMsg() {
            return ((bitField0_ & 0x00000002) == 0x00000002);
        }

        /**
         * <code>optional string detailMsg = 2;</code>
         */
        public java.lang.String getDetailMsg() {
            java.lang.Object ref = detailMsg_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    detailMsg_ = s;
                }
                return s;
            }
        }

        /**
         * <code>optional string detailMsg = 2;</code>
         */
        public com.google.protobuf.ByteString
        getDetailMsgBytes() {
            java.lang.Object ref = detailMsg_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                detailMsg_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private void initFields() {
            mt_ = com.baofeng.mj.bean.BaseMessage.MessageType.MessageType_HeartBeat;
            detailMsg_ = "";
        }

        private byte memoizedIsInitialized = -1;

        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized != -1) return isInitialized == 1;

            if (!hasMt()) {
                memoizedIsInitialized = 0;
                return false;
            }
            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            getSerializedSize();
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                output.writeEnum(1, mt_.getNumber());
            }
            if (((bitField0_ & 0x00000002) == 0x00000002)) {
                output.writeBytes(2, getDetailMsgBytes());
            }
        }

        private int memoizedSerializedSize = -1;

        public int getSerializedSize() {
            int size = memoizedSerializedSize;
            if (size != -1) return size;

            size = 0;
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeEnumSize(1, mt_.getNumber());
            }
            if (((bitField0_ & 0x00000002) == 0x00000002)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeBytesSize(2, getDetailMsgBytes());
            }
            memoizedSerializedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;

        @java.lang.Override
        protected java.lang.Object writeReplace()
                throws java.io.ObjectStreamException {
            return super.writeReplace();
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return PARSER.parseFrom(input);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return PARSER.parseFrom(input);
        }

        public static com.baofeng.mj.bean.BaseMessage.BasicMessage parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(com.baofeng.mj.bean.BaseMessage.BasicMessage prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        /**
         * Protobuf type {@code com.baofeng.mj.bean.BasicMessage}
         * <p>
         * <pre>
         * ��Ϣ�ܰ�
         * @mt :����ҵ��������Ϣ
         * @detailMsg:����ҵ���߼���Ϣ��protobuf
         * </pre>
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageLite.Builder<
                        com.baofeng.mj.bean.BaseMessage.BasicMessage, Builder>
                implements com.baofeng.mj.bean.BaseMessage.BasicMessageOrBuilder {
            // Construct using com.baofeng.mj.bean.BaseMessage.BasicMessage.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
            }

            private static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                mt_ = com.baofeng.mj.bean.BaseMessage.MessageType.MessageType_HeartBeat;
                bitField0_ = (bitField0_ & ~0x00000001);
                detailMsg_ = "";
                bitField0_ = (bitField0_ & ~0x00000002);
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public com.baofeng.mj.bean.BaseMessage.BasicMessage getDefaultInstanceForType() {
                return com.baofeng.mj.bean.BaseMessage.BasicMessage.getDefaultInstance();
            }

            public com.baofeng.mj.bean.BaseMessage.BasicMessage build() {
                com.baofeng.mj.bean.BaseMessage.BasicMessage result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public com.baofeng.mj.bean.BaseMessage.BasicMessage buildPartial() {
                com.baofeng.mj.bean.BaseMessage.BasicMessage result = new com.baofeng.mj.bean.BaseMessage.BasicMessage(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
                    to_bitField0_ |= 0x00000001;
                }
                result.mt_ = mt_;
                if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
                    to_bitField0_ |= 0x00000002;
                }
                result.detailMsg_ = detailMsg_;
                result.bitField0_ = to_bitField0_;
                return result;
            }

            public Builder mergeFrom(com.baofeng.mj.bean.BaseMessage.BasicMessage other) {
                if (other == com.baofeng.mj.bean.BaseMessage.BasicMessage.getDefaultInstance())
                    return this;
                if (other.hasMt()) {
                    setMt(other.getMt());
                }
                if (other.hasDetailMsg()) {
                    bitField0_ |= 0x00000002;
                    detailMsg_ = other.detailMsg_;

                }
                return this;
            }

            public final boolean isInitialized() {
                if (!hasMt()) {

                    return false;
                }
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                com.baofeng.mj.bean.BaseMessage.BasicMessage parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (com.baofeng.mj.bean.BaseMessage.BasicMessage) e.getUnfinishedMessage();
                    throw e;
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            // required .com.baofeng.mj.bean.MessageType mt = 1;
            private com.baofeng.mj.bean.BaseMessage.MessageType mt_ = com.baofeng.mj.bean.BaseMessage.MessageType.MessageType_HeartBeat;

            /**
             * <code>required .com.baofeng.mj.bean.MessageType mt = 1;</code>
             */
            public boolean hasMt() {
                return ((bitField0_ & 0x00000001) == 0x00000001);
            }

            /**
             * <code>required .com.baofeng.mj.bean.MessageType mt = 1;</code>
             */
            public com.baofeng.mj.bean.BaseMessage.MessageType getMt() {
                return mt_;
            }

            /**
             * <code>required .com.baofeng.mj.bean.MessageType mt = 1;</code>
             */
            public Builder setMt(com.baofeng.mj.bean.BaseMessage.MessageType value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000001;
                mt_ = value;

                return this;
            }

            /**
             * <code>required .com.baofeng.mj.bean.MessageType mt = 1;</code>
             */
            public Builder clearMt() {
                bitField0_ = (bitField0_ & ~0x00000001);
                mt_ = com.baofeng.mj.bean.BaseMessage.MessageType.MessageType_HeartBeat;

                return this;
            }

            // optional string detailMsg = 2;
            private java.lang.Object detailMsg_ = "";

            /**
             * <code>optional string detailMsg = 2;</code>
             */
            public boolean hasDetailMsg() {
                return ((bitField0_ & 0x00000002) == 0x00000002);
            }

            /**
             * <code>optional string detailMsg = 2;</code>
             */
            public java.lang.String getDetailMsg() {
                java.lang.Object ref = detailMsg_;
                if (!(ref instanceof java.lang.String)) {
                    java.lang.String s = ((com.google.protobuf.ByteString) ref)
                            .toStringUtf8();
                    detailMsg_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>optional string detailMsg = 2;</code>
             */
            public com.google.protobuf.ByteString
            getDetailMsgBytes() {
                java.lang.Object ref = detailMsg_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    detailMsg_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>optional string detailMsg = 2;</code>
             */
            public Builder setDetailMsg(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000002;
                detailMsg_ = value;

                return this;
            }

            /**
             * <code>optional string detailMsg = 2;</code>
             */
            public Builder clearDetailMsg() {
                bitField0_ = (bitField0_ & ~0x00000002);
                detailMsg_ = getDefaultInstance().getDetailMsg();

                return this;
            }

            /**
             * <code>optional string detailMsg = 2;</code>
             */
            public Builder setDetailMsgBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000002;
                detailMsg_ = value;

                return this;
            }

            // @@protoc_insertion_point(builder_scope:com.baofeng.mj.bean.BasicMessage)
        }

        static {
            defaultInstance = new BasicMessage(true);
            defaultInstance.initFields();
        }

        // @@protoc_insertion_point(class_scope:com.baofeng.mj.bean.BasicMessage)
    }


    static {
    }

    // @@protoc_insertion_point(outer_class_scope)
}
