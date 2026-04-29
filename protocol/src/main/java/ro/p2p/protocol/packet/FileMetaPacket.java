package ro.p2p.protocol.packet;

import java.util.Arrays;
import ro.p2p.common.enums.PacketType;

public class FileMetaPacket implements Packet {

    private final String transferId;
    private final String fileName;
    private final long fileSize;
    private final int totalChunks;
    private final byte[] hash;

    public FileMetaPacket(
            String transferId, String fileName, long fileSize, int totalChunks, byte[] hash) {
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("Transfer id must not be blank");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name must not be blank");
        }
        this.transferId = transferId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.totalChunks = totalChunks;
        this.hash = hash == null ? new byte[0] : Arrays.copyOf(hash, hash.length);
    }

    @Override
    public PacketType getType() {
        return PacketType.FILE_META;
    }

    public String getTransferId() {
        return transferId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public byte[] getHash() {
        return Arrays.copyOf(hash, hash.length);
    }
}
