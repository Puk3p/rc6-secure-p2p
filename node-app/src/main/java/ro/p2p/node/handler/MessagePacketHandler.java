package ro.p2p.node.handler;

import java.util.Optional;
import ro.p2p.messaging.service.MessageReceiveService;
import ro.p2p.messaging.service.SecureMessageService;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.protocol.packet.MessagePacket;

public class MessagePacketHandler {

    private final MessageReceiveService receiveService;
    private final SecureMessageService sendService;

    public MessagePacketHandler(
            MessageReceiveService receiveService, SecureMessageService sendService) {
        this.receiveService = receiveService;
        this.sendService = sendService;
    }

    public Optional<String> handle(PeerConnection connection, MessagePacket packet) {
        Optional<String> message = receiveService.receive(packet, connection.getSessionKey());
        sendService.acknowledge(connection, packet);
        return message;
    }
}
