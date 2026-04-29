package ro.p2p.common.model;

import java.util.Arrays;

public class ChunkInfo {

    private final String transferId;
    private final int chunkIndex;
    private final int totalChunks;
    private final byte[] data;

    public ChunkInfo(String transferId, int chunkIndex, int totalChunks, byte[] data) {
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("Transfer id must not be blank");
        }
        if (chunkIndex < 0 || totalChunks < 0 || chunkIndex >= totalChunks) {
            throw new IllegalArgumentException("Invalid chunk index");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data must not be null");
        }
        this.transferId = transferId;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.data = Arrays.copyOf(data, data.length);
    }

    public String getTransferId() {
        return transferId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }
}
