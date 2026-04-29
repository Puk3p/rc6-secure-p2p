package ro.p2p.protocol.serializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import ro.p2p.common.exception.ProtocolException;

public class BinarySerializer {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final DataOutputStream output = new DataOutputStream(buffer);

    public BinarySerializer writeBoolean(boolean value) {
        try {
            output.writeBoolean(value);
            return this;
        } catch (IOException e) {
            throw new ProtocolException("Failed to write boolean", e);
        }
    }

    public BinarySerializer writeInt(int value) {
        try {
            output.writeInt(value);
            return this;
        } catch (IOException e) {
            throw new ProtocolException("Failed to write int", e);
        }
    }

    public BinarySerializer writeLong(long value) {
        try {
            output.writeLong(value);
            return this;
        } catch (IOException e) {
            throw new ProtocolException("Failed to write long", e);
        }
    }

    public BinarySerializer writeString(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return writeBytes(bytes);
    }

    public BinarySerializer writeBytes(byte[] value) {
        try {
            output.writeInt(value.length);
            output.write(value);
            return this;
        } catch (IOException e) {
            throw new ProtocolException("Failed to write bytes", e);
        }
    }

    public byte[] toByteArray() {
        return buffer.toByteArray();
    }
}
