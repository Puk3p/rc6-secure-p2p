package ro.p2p.protocol.packet;

import java.util.Arrays;
import ro.p2p.common.enums.PacketType;

public class FileChunkPacket implements Packet {

    private final String transferId;
    private final int chunkIndex;
    private final int totalChunks;
    private final byte[] encryptedPayload;

    public FileChunkPacket(
            String transferId, int chunkIndex, int totalChunks, byte[] encryptedPayload) {
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("Transfer id must not be blank");
        }
        if (chunkIndex < 0 || totalChunks <= 0 || chunkIndex >= totalChunks) {
            throw new IllegalArgumentException("Invalid chunk index");
        }
        if (encryptedPayload == null) {
            throw new IllegalArgumentException("Encrypted payload must not be null");
        }
        this.transferId = transferId;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.encryptedPayload = Arrays.copyOf(encryptedPayload, encryptedPayload.length);
    }

    @Override
    public PacketType getType() {
        return PacketType.FILE_CHUNK;
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

    public byte[] getEncryptedPayload() {
        return Arrays.copyOf(encryptedPayload, encryptedPayload.length);
    }
}
