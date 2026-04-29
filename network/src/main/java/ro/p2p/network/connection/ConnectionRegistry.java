package ro.p2p.network.connection;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionRegistry {

    private final ConcurrentMap<String, PeerConnection> connections = new ConcurrentHashMap<>();

    public void register(PeerConnection connection) {
        connections.put(connection.getPeerId(), connection);
    }

    public Optional<PeerConnection> find(String peerId) {
        return Optional.ofNullable(connections.get(peerId));
    }

    public Collection<PeerConnection> all() {
        return connections.values();
    }

    public void remove(String peerId) {
        PeerConnection removed = connections.remove(peerId);
        if (removed != null) {
            removed.closeQuietly();
        }
    }

    public void closeAll() {
        connections.values().forEach(PeerConnection::closeQuietly);
        connections.clear();
    }
}
