package ro.p2p.node.config;

import java.util.List;

public class NodeConfig {

    private final String nodeId;
    private final String host;
    private final int port;
    private final List<PeerConfig> peers;

    public NodeConfig(String nodeId, String host, int port, List<PeerConfig> peers) {
        if (nodeId == null || nodeId.isBlank()) {
            throw new IllegalArgumentException("Node id must not be blank");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Host must not be blank");
        }
        if (port <= 0 || port > 65_535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        this.nodeId = nodeId;
        this.host = host;
        this.port = port;
        this.peers = List.copyOf(peers);
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public List<PeerConfig> getPeers() {
        return peers;
    }
}
