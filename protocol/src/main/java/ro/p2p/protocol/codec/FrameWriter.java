package ro.p2p.protocol.codec;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import ro.p2p.common.constants.PacketConstants;
import ro.p2p.common.exception.ProtocolException;
import ro.p2p.protocol.packet.Packet;

public class FrameWriter {

    private final DataOutputStream output;
    private final PacketEncoder encoder;

    public FrameWriter(OutputStream output) {
        this.output = new DataOutputStream(output);
        this.encoder = new PacketEncoder();
    }

    public synchronized void write(Packet packet) {
        PacketEncoder.EncodedPacket encoded = encoder.encode(packet);
        byte[] payload = encoded.getPayload();
        if (payload.length > PacketConstants.MAX_PAYLOAD_SIZE) {
            throw new ProtocolException("Payload too large: " + payload.length);
        }
        try {
            output.writeByte(encoded.getType().getCode());
            output.writeInt(payload.length);
            output.write(payload);
            output.flush();
        } catch (IOException e) {
            throw new ProtocolException("Failed to write frame", e);
        }
    }
}
