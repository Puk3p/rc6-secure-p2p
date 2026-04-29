package ro.p2p.network.server;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import ro.p2p.network.connection.ConnectionRegistry;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.network.handshake.SessionBootstrapService;

public class IncomingConnectionHandler implements Runnable {

    private final Socket socket;
    private final SessionBootstrapService bootstrapService;
    private final ConnectionRegistry registry;
    private final Consumer<PeerConnection> onConnection;

    public IncomingConnectionHandler(
            Socket socket,
            SessionBootstrapService bootstrapService,
            ConnectionRegistry registry,
            Consumer<PeerConnection> onConnection) {
        this.socket = socket;
        this.bootstrapService = bootstrapService;
        this.registry = registry;
        this.onConnection = onConnection;
    }

    @Override
    public void run() {
        try {
            PeerConnection connection = bootstrapService.bootstrapResponder(socket);
            registry.register(connection);
            onConnection.accept(connection);
        } catch (IOException | RuntimeException e) {
            try {
                socket.close();
            } catch (IOException ignored) {
                // Nothing else to do during failed bootstrap.
            }
        }
    }
}
