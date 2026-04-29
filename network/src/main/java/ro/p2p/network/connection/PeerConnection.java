package ro.p2p.network.connection;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.Arrays;
import ro.p2p.common.enums.ConnectionState;
import ro.p2p.common.model.SessionInfo;
import ro.p2p.protocol.codec.FrameReader;
import ro.p2p.protocol.codec.FrameWriter;
import ro.p2p.protocol.packet.Packet;

public class PeerConnection implements Closeable {

    private final String peerId;
    private final Socket socket;
    private final SessionInfo sessionInfo;
    private final FrameReader reader;
    private final FrameWriter writer;
    private volatile ConnectionState state;

    public PeerConnection(String peerId, Socket socket, byte[] sessionKey) throws IOException {
        this.peerId = peerId;
        this.socket = socket;
        this.sessionInfo =
                new SessionInfo(
                        peerId, Arrays.copyOf(sessionKey, sessionKey.length), Instant.now());
        this.reader = new FrameReader(socket.getInputStream());
        this.writer = new FrameWriter(socket.getOutputStream());
        this.state = ConnectionState.ESTABLISHED;
    }

    public String getPeerId() {
        return peerId;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public byte[] getSessionKey() {
        return sessionInfo.getSessionKey();
    }

    public ConnectionState getState() {
        return state;
    }

    public boolean isOpen() {
        return !socket.isClosed() && state == ConnectionState.ESTABLISHED;
    }

    public void send(Packet packet) {
        writer.write(packet);
    }

    public java.util.Optional<Packet> receive() {
        return reader.read();
    }

    public void closeQuietly() {
        try {
            close();
        } catch (IOException ignored) {
            // Best-effort shutdown for tests and CLI cleanup.
        }
    }

    @Override
    public void close() throws IOException {
        state = ConnectionState.CLOSED;
        socket.close();
    }
}
