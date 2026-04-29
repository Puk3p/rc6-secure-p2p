package ro.p2p.network.io;

import java.io.InputStream;
import java.util.Optional;
import ro.p2p.protocol.codec.FrameReader;
import ro.p2p.protocol.packet.Packet;

public class SocketReader {

    private final FrameReader frameReader;

    public SocketReader(InputStream inputStream) {
        this.frameReader = new FrameReader(inputStream);
    }

    public Optional<Packet> readPacket() {
        return frameReader.read();
    }
}
