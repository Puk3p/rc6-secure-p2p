package ro.p2p.protocol.codec;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import ro.p2p.common.constants.PacketConstants;
import ro.p2p.common.enums.PacketType;
import ro.p2p.common.exception.ProtocolException;
import ro.p2p.protocol.packet.Packet;

public class FrameReader {

    private final DataInputStream input;
    private final PacketDecoder decoder;

    public FrameReader(InputStream input) {
        this.input = new DataInputStream(input);
        this.decoder = new PacketDecoder();
    }

    public Optional<Packet> read() {
        try {
            int typeCode = input.readUnsignedByte();
            int length = input.readInt();
            if (length < 0 || length > PacketConstants.MAX_PAYLOAD_SIZE) {
                throw new ProtocolException("Invalid payload length: " + length);
            }
            byte[] payload = new byte[length];
            input.readFully(payload);
            return Optional.of(decoder.decode(PacketType.fromCode(typeCode), payload));
        } catch (EOFException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new ProtocolException("Failed to read frame", e);
        }
    }
}
