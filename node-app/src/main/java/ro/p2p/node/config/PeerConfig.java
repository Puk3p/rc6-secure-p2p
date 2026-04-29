package ro.p2p.node.config;

import ro.p2p.common.model.PeerAddress;

public class PeerConfig {

    private final String peerId;
    private final PeerAddress address;

    public PeerConfig(String peerId, PeerAddress address) {
        if (peerId == null || peerId.isBlank()) {
            throw new IllegalArgumentException("Peer id must not be blank");
        }
        if (address == null) {
            throw new IllegalArgumentException("Peer address must not be null");
        }
        this.peerId = peerId;
        this.address = address;
    }

    public String getPeerId() {
        return peerId;
    }

    public PeerAddress getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return peerId + '@' + address;
    }
}
