package ro.p2p.network.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import ro.p2p.network.connection.ConnectionRegistry;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.network.handshake.SessionBootstrapService;

public class TcpPeerServer implements Closeable {

    private final String nodeId;
    private final int port;
    private final ConnectionRegistry registry;
    private final Consumer<PeerConnection> onConnection;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean running;
    private ServerSocket serverSocket;

    public TcpPeerServer(
            String nodeId,
            int port,
            ConnectionRegistry registry,
            Consumer<PeerConnection> onConnection) {
        this.nodeId = nodeId;
        this.port = port;
        this.registry = registry;
        this.onConnection = onConnection;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        executor.submit(this::acceptLoop);
    }

    public int getLocalPort() {
        return serverSocket == null ? port : serverSocket.getLocalPort();
    }

    private void acceptLoop() {
        SessionBootstrapService bootstrapService =
                new SessionBootstrapService(nodeId, getLocalPort());
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(
                        new IncomingConnectionHandler(
                                socket, bootstrapService, registry, onConnection));
            } catch (IOException e) {
                if (running) {
                    throw new IllegalStateException("Failed to accept peer connection", e);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        running = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
        executor.shutdownNow();
    }
}
