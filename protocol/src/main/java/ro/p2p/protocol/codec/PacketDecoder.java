package ro.p2p.protocol.codec;

import ro.p2p.common.enums.PacketType;
import ro.p2p.protocol.packet.AckPacket;
import ro.p2p.protocol.packet.ErrorPacket;
import ro.p2p.protocol.packet.FileChunkPacket;
import ro.p2p.protocol.packet.FileMetaPacket;
import ro.p2p.protocol.packet.HelloPacket;
import ro.p2p.protocol.packet.MessagePacket;
import ro.p2p.protocol.packet.Packet;
import ro.p2p.protocol.serializer.BinaryDeserializer;

public class PacketDecoder {

    public Packet decode(PacketType type, byte[] payload) {
        BinaryDeserializer deserializer = new BinaryDeserializer(payload);
        switch (type) {
            case HELLO:
                return new HelloPacket(
                        deserializer.readString(),
                        deserializer.readInt(),
                        deserializer.readBytes(),
                        deserializer.readBytes(),
                        deserializer.readBoolean());
            case MESSAGE:
                return new MessagePacket(
                        deserializer.readString(),
                        deserializer.readLong(),
                        deserializer.readInt(),
                        deserializer.readInt(),
                        deserializer.readBytes());
            case FILE_META:
                return new FileMetaPacket(
                        deserializer.readString(),
                        deserializer.readString(),
                        deserializer.readLong(),
                        deserializer.readInt(),
                        deserializer.readBytes());
            case FILE_CHUNK:
                return new FileChunkPacket(
                        deserializer.readString(),
                        deserializer.readInt(),
                        deserializer.readInt(),
                        deserializer.readBytes());
            case ACK:
                return new AckPacket(
                        PacketType.fromCode(deserializer.readInt()), deserializer.readString());
            case ERROR:
                return new ErrorPacket(deserializer.readString(), deserializer.readString());
            default:
                throw new IllegalArgumentException("Unsupported packet type: " + type);
        }
    }
}
