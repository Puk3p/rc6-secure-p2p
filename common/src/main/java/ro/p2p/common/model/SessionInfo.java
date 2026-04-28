package ro.p2p.common.model;

import java.time.Instant;
import java.util.Arrays;

public class SessionInfo {

    private final String peerId;
    private final byte[] sessionKey;
    private final Instant establishedAt;

    public SessionInfo(String peerId, byte[] sessionKey, Instant establishedAt) {
        this.peerId = peerId;
        this.sessionKey = Arrays.copyOf(sessionKey, sessionKey.length);
        this.establishedAt = establishedAt;
    }

    public String getPeerId() {
        return peerId;
    }

    public byte[] getSessionKey() {
        return Arrays.copyOf(sessionKey, sessionKey.length);
    }

    public Instant getEstablishedAt() {
        return establishedAt;
    }
}
