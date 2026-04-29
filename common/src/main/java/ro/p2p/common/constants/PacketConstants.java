package ro.p2p.common.constants;

public final class PacketConstants {

    public static final int TYPE_BYTES = 1;
    public static final int LENGTH_BYTES = 4;
    public static final int FRAME_HEADER_BYTES = TYPE_BYTES + LENGTH_BYTES;
    public static final int DEFAULT_CHUNK_SIZE = 4096;
    public static final int MAX_PAYLOAD_SIZE = 16 * 1024 * 1024;

    private PacketConstants() {}
}
