package ro.p2p.network.connection;

public class ConnectionSupervisor {

    private final ConnectionRegistry registry;

    public ConnectionSupervisor(ConnectionRegistry registry) {
        this.registry = registry;
    }

    public int activeConnectionCount() {
        int active = 0;
        for (PeerConnection connection : registry.all()) {
            if (connection.isOpen()) {
                active++;
            }
        }
        return active;
    }
}
