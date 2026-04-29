package ro.p2p.protocol.packet;

import java.util.Arrays;
import ro.p2p.common.enums.PacketType;

public class HelloPacket implements Packet {

    private final String nodeId;
    private final int listenPort;
    private final byte[] publicKey;
    private final byte[] nonce;
    private final boolean response;

    public HelloPacket(
            String nodeId, int listenPort, byte[] publicKey, byte[] nonce, boolean response) {
        if (nodeId == null || nodeId.isBlank()) {
            throw new IllegalArgumentException("Node id must not be blank");
        }
        if (publicKey == null || nonce == null) {
            throw new IllegalArgumentException("Public key and nonce must not be null");
        }
        this.nodeId = nodeId;
        this.listenPort = listenPort;
        this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
        this.nonce = Arrays.copyOf(nonce, nonce.length);
        this.response = response;
    }

    @Override
    public PacketType getType() {
        return PacketType.HELLO;
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getListenPort() {
        return listenPort;
    }

    public byte[] getPublicKey() {
        return Arrays.copyOf(publicKey, publicKey.length);
    }

    public byte[] getNonce() {
        return Arrays.copyOf(nonce, nonce.length);
    }

    public boolean isResponse() {
        return response;
    }
}
