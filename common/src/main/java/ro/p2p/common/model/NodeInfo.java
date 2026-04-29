package ro.p2p.common.model;

public class NodeInfo {

    private final String nodeId;
    private final PeerAddress address;

    public NodeInfo(String nodeId, PeerAddress address) {
        if (nodeId == null || nodeId.isBlank()) {
            throw new IllegalArgumentException("Node id must not be blank");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address must not be null");
        }
        this.nodeId = nodeId;
        this.address = address;
    }

    public String getNodeId() {
        return nodeId;
    }

    public PeerAddress getAddress() {
        return address;
    }
}
