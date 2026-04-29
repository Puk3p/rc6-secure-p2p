package ro.p2p.common.model;

import java.util.Arrays;

public class FileMetadata {

    private final String transferId;
    private final String fileName;
    private final long fileSize;
    private final int totalChunks;
    private final byte[] hash;

    public FileMetadata(
            String transferId, String fileName, long fileSize, int totalChunks, byte[] hash) {
        if (transferId == null || transferId.isBlank()) {
            throw new IllegalArgumentException("Transfer id must not be blank");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name must not be blank");
        }
        if (fileSize < 0 || totalChunks < 0) {
            throw new IllegalArgumentException("File size and chunk count must not be negative");
        }
        this.transferId = transferId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.totalChunks = totalChunks;
        this.hash = hash == null ? new byte[0] : Arrays.copyOf(hash, hash.length);
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
