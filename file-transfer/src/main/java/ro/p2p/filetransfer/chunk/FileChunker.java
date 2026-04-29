package ro.p2p.filetransfer.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ro.p2p.common.constants.PacketConstants;

public class FileChunker {

    private final int chunkSize;

    public FileChunker() {
        this(PacketConstants.DEFAULT_CHUNK_SIZE);
    }

    public FileChunker(int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        this.chunkSize = chunkSize;
    }

    public List<byte[]> split(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data must not be null");
        }
        List<byte[]> chunks = new ArrayList<>();
        if (data.length == 0) {
            chunks.add(new byte[0]);
            return chunks;
        }
        for (int offset = 0; offset < data.length; offset += chunkSize) {
            chunks.add(Arrays.copyOfRange(data, offset, Math.min(data.length, offset + chunkSize)));
        }
        return chunks;
    }
}
