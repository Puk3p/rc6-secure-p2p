package ro.p2p.network.io;

import java.io.OutputStream;
import ro.p2p.protocol.codec.FrameWriter;
import ro.p2p.protocol.packet.Packet;

public class SocketWriter {

    private final FrameWriter frameWriter;

    public SocketWriter(OutputStream outputStream) {
        this.frameWriter = new FrameWriter(outputStream);
    }

    public void writePacket(Packet packet) {
        frameWriter.write(packet);
    }
}
