package ro.p2p.node.app;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import ro.p2p.common.model.PeerAddress;
import ro.p2p.messaging.service.MessageReceiveService;
import ro.p2p.network.client.TcpPeerClient;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.network.server.TcpPeerServer;
import ro.p2p.node.bootstrap.NodeContext;
import ro.p2p.protocol.packet.Packet;

public class NodeRuntime implements Closeable {

    private final String nodeId;
    private final NodeContext context;
    private final ExecutorService readerExecutor = Executors.newCachedThreadPool();
    private TcpPeerServer server;

    public NodeRuntime(String nodeId, Consumer<String> onMessage) {
        this.nodeId = nodeId;
        this.context = new NodeContext(nodeId, new MessageReceiveService(onMessage));
    }

    public void start(int port) throws IOException {
        server =
                new TcpPeerServer(
                        nodeId, port, context.getConnectionRegistry(), this::startReaderLoop);
        server.start();
    }

    public int getLocalPort() {
        return server.getLocalPort();
    }

    public PeerConnection connectTo(PeerAddress address) throws IOException {
        TcpPeerClient client = new TcpPeerClient(nodeId, getLocalPort());
        PeerConnection connection = client.connect(address);
        context.getConnectionRegistry().register(connection);
        startReaderLoop(connection);
        return connection;
    }

    public String sendMessage(String peerId, String message) {
        PeerConnection connection =
                context.getConnectionRegistry()
                        .find(peerId)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown peer: " + peerId));
        return context.getSecureMessageService().sendMessage(connection, message);
    }

    public String sendFileBytes(String peerId, byte[] data) {
        return sendFileBytes(peerId, data, "received.bin");
    }

    public String sendFileBytes(String peerId, byte[] data, String fileName) {
        PeerConnection connection =
                context.getConnectionRegistry()
                        .find(peerId)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown peer: " + peerId));
        return context.getFileTransferService().sendBytes(connection, data, fileName);
    }

    public boolean isConnected(String peerId) {
        return context.getConnectionRegistry()
                .find(peerId)
                .filter(PeerConnection::isOpen)
                .isPresent();
    }

    public Set<String> getConnectedPeerIds() {
        return context.getConnectionRegistry().all().stream()
                .filter(PeerConnection::isOpen)
                .map(PeerConnection::getPeerId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public NodeContext getContext() {
        return context;
    }

    private void startReaderLoop(PeerConnection connection) {
        readerExecutor.submit(
                () -> {
                    while (connection.isOpen()) {
                        Optional<Packet> packet = connection.receive();
                        if (packet.isEmpty()) {
                            connection.closeQuietly();
                            return;
                        }
                        context.getPacketRouter().route(connection, packet.get());
                    }
                });
    }

    @Override
    public void close() throws IOException {
        context.getConnectionRegistry().closeAll();
        if (server != null) {
            server.close();
        }
        readerExecutor.shutdownNow();
    }
}
