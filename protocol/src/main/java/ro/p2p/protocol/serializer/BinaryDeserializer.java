package ro.p2p.protocol.serializer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import ro.p2p.common.constants.PacketConstants;
import ro.p2p.common.exception.ProtocolException;

public class BinaryDeserializer {

    private final DataInputStream input;

    public BinaryDeserializer(byte[] payload) {
        this.input = new DataInputStream(new ByteArrayInputStream(payload));
    }

    public boolean readBoolean() {
        try {
            return input.readBoolean();
        } catch (IOException e) {
            throw new ProtocolException("Failed to read boolean", e);
        }
    }

    public int readInt() {
        try {
            return input.readInt();
        } catch (IOException e) {
            throw new ProtocolException("Failed to read int", e);
        }
    }

    public long readLong() {
        try {
            return input.readLong();
        } catch (IOException e) {
            throw new ProtocolException("Failed to read long", e);
        }
    }

    public String readString() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    public byte[] readBytes() {
        try {
            int length = input.readInt();
            if (length < 0 || length > PacketConstants.MAX_PAYLOAD_SIZE) {
                throw new ProtocolException("Invalid field length: " + length);
            }
            byte[] bytes = new byte[length];
            input.readFully(bytes);
            return bytes;
        } catch (IOException e) {
            throw new ProtocolException("Failed to read bytes", e);
        }
    }
}
