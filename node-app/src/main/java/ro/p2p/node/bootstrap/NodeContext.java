package ro.p2p.node.bootstrap;

import ro.p2p.filetransfer.service.FileTransferService;
import ro.p2p.messaging.service.MessageReceiveService;
import ro.p2p.messaging.service.SecureMessageService;
import ro.p2p.messaging.tracker.AckTracker;
import ro.p2p.network.connection.ConnectionRegistry;
import ro.p2p.node.handler.AckPacketHandler;
import ro.p2p.node.handler.FilePacketHandler;
import ro.p2p.node.handler.MessagePacketHandler;
import ro.p2p.node.handler.PacketRouter;

public class NodeContext {

    private final ConnectionRegistry connectionRegistry = new ConnectionRegistry();
    private final SecureMessageService secureMessageService = new SecureMessageService();
    private final MessageReceiveService messageReceiveService;
    private final AckTracker ackTracker = new AckTracker();
    private final FileTransferService fileTransferService = new FileTransferService();
    private final PacketRouter packetRouter;

    public NodeContext(MessageReceiveService messageReceiveService) {
        this.messageReceiveService = messageReceiveService;
        this.packetRouter =
                new PacketRouter(
                        new MessagePacketHandler(messageReceiveService, secureMessageService),
                        new AckPacketHandler(ackTracker),
                        new FilePacketHandler(fileTransferService));
    }

    public ConnectionRegistry getConnectionRegistry() {
        return connectionRegistry;
    }

    public SecureMessageService getSecureMessageService() {
        return secureMessageService;
    }

    public MessageReceiveService getMessageReceiveService() {
        return messageReceiveService;
    }

    public AckTracker getAckTracker() {
        return ackTracker;
    }

    public FileTransferService getFileTransferService() {
        return fileTransferService;
    }

    public PacketRouter getPacketRouter() {
        return packetRouter;
    }
}
