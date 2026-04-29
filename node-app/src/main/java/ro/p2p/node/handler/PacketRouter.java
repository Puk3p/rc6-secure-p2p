package ro.p2p.node.handler;

import java.util.Optional;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.protocol.packet.AckPacket;
import ro.p2p.protocol.packet.FileChunkPacket;
import ro.p2p.protocol.packet.FileMetaPacket;
import ro.p2p.protocol.packet.MessagePacket;
import ro.p2p.protocol.packet.Packet;

public class PacketRouter {

    private final MessagePacketHandler messageHandler;
    private final AckPacketHandler ackHandler;
    private final FilePacketHandler fileHandler;

    public PacketRouter(
            MessagePacketHandler messageHandler,
            AckPacketHandler ackHandler,
            FilePacketHandler fileHandler) {
        this.messageHandler = messageHandler;
        this.ackHandler = ackHandler;
        this.fileHandler = fileHandler;
    }

    public Optional<Object> route(PeerConnection connection, Packet packet) {
        if (packet instanceof MessagePacket) {
            return messageHandler
                    .handle(connection, (MessagePacket) packet)
                    .map(Object.class::cast);
        }
        if (packet instanceof AckPacket) {
            ackHandler.handle((AckPacket) packet);
            return Optional.empty();
        }
        if (packet instanceof FileChunkPacket) {
            return fileHandler.handle(connection, (FileChunkPacket) packet).map(Object.class::cast);
        }
        if (packet instanceof FileMetaPacket) {
            fileHandler.handleMeta((FileMetaPacket) packet);
            return Optional.empty();
        }
        return Optional.empty();
    }
}
