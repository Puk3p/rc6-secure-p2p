package ro.p2p.protocol.codec;

import ro.p2p.common.enums.PacketType;
import ro.p2p.common.exception.ProtocolException;
import ro.p2p.protocol.packet.AckPacket;
import ro.p2p.protocol.packet.ErrorPacket;
import ro.p2p.protocol.packet.FileChunkPacket;
import ro.p2p.protocol.packet.FileMetaPacket;
import ro.p2p.protocol.packet.HelloPacket;
import ro.p2p.protocol.packet.MessagePacket;
import ro.p2p.protocol.packet.Packet;
import ro.p2p.protocol.serializer.BinarySerializer;

public class PacketEncoder {

    public EncodedPacket encode(Packet packet) {
        if (packet == null) {
            throw new ProtocolException("Packet must not be null");
        }
        return new EncodedPacket(packet.getType(), encodePayload(packet));
    }

    private byte[] encodePayload(Packet packet) {
        BinarySerializer serializer = new BinarySerializer();
        if (packet instanceof HelloPacket) {
            HelloPacket hello = (HelloPacket) packet;
            return serializer
                    .writeString(hello.getNodeId())
                    .writeInt(hello.getListenPort())
                    .writeBytes(hello.getPublicKey())
                    .writeBytes(hello.getNonce())
                    .writeBoolean(hello.isResponse())
                    .toByteArray();
        }
        if (packet instanceof MessagePacket) {
            MessagePacket message = (MessagePacket) packet;
            return serializer
                    .writeString(message.getMessageId())
                    .writeLong(message.getTimestampMillis())
                    .writeInt(message.getChunkIndex())
                    .writeInt(message.getTotalChunks())
                    .writeBytes(message.getEncryptedPayload())
                    .toByteArray();
        }
        if (packet instanceof FileMetaPacket) {
            FileMetaPacket meta = (FileMetaPacket) packet;
            return serializer
                    .writeString(meta.getTransferId())
                    .writeString(meta.getFileName())
                    .writeLong(meta.getFileSize())
                    .writeInt(meta.getTotalChunks())
                    .writeBytes(meta.getHash())
                    .toByteArray();
        }
        if (packet instanceof FileChunkPacket) {
            FileChunkPacket chunk = (FileChunkPacket) packet;
            return serializer
                    .writeString(chunk.getTransferId())
                    .writeInt(chunk.getChunkIndex())
                    .writeInt(chunk.getTotalChunks())
                    .writeBytes(chunk.getEncryptedPayload())
                    .toByteArray();
        }
        if (packet instanceof AckPacket) {
            AckPacket ack = (AckPacket) packet;
            return serializer
                    .writeInt(ack.getAcknowledgedType().getCode())
                    .writeString(ack.getAcknowledgedId())
                    .toByteArray();
        }
        if (packet instanceof ErrorPacket) {
            ErrorPacket error = (ErrorPacket) packet;
            return serializer
                    .writeString(error.getErrorCode())
                    .writeString(error.getMessage())
                    .toByteArray();
        }
        throw new ProtocolException("Unsupported packet class: " + packet.getClass().getName());
    }

    public static final class EncodedPacket {
        private final PacketType type;
        private final byte[] payload;

        public EncodedPacket(PacketType type, byte[] payload) {
            this.type = type;
            this.payload = payload.clone();
        }

        public PacketType getType() {
            return type;
        }

        public byte[] getPayload() {
            return payload.clone();
        }
    }
}
