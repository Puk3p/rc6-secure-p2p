package ro.p2p.protocol.packet;

import java.util.Arrays;

public class HelloPacket {

    private final String nodeId;
    private final int listenPort;
    private final byte[] publicKey;
    private final byte[] nonce;
    private final boolean response;

    public HelloPacket(String nodeId, int listenPort, byte[] publicKey, byte[] nonce, boolean response) {
        this.nodeId = nodeId;
        this.listenPort = listenPort;
        this.publicKey = Arrays.copyOf(publicKey, publicKey.length);
        this.nonce = Arrays.copyOf(nonce, nonce.length);
        this.response = response;
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
