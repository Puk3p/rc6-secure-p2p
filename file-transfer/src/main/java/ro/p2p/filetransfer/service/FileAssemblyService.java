package ro.p2p.filetransfer.service;

import java.util.Optional;
import ro.p2p.filetransfer.storage.TempChunkStorage;

public class FileAssemblyService {

    private final TempChunkStorage storage = new TempChunkStorage();

    public Optional<byte[]> addChunk(String transferId, int index, int totalChunks, byte[] data) {
        return storage.put(transferId, index, totalChunks, data);
    }
}
