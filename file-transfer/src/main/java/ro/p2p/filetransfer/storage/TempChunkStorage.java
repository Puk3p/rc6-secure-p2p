package ro.p2p.filetransfer.storage;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TempChunkStorage {

    private final Map<String, TransferChunks> transfers = new ConcurrentHashMap<>();

    public Optional<byte[]> put(String transferId, int index, int totalChunks, byte[] data) {
        TransferChunks chunks =
                transfers.computeIfAbsent(transferId, id -> new TransferChunks(totalChunks));
        chunks.put(index, data);
        if (!chunks.isComplete()) {
            return Optional.empty();
        }
        transfers.remove(transferId);
        return Optional.of(chunks.assemble());
    }

    private static final class TransferChunks {
        private final byte[][] chunks;

        TransferChunks(int totalChunks) {
            this.chunks = new byte[totalChunks][];
        }

        synchronized void put(int index, byte[] data) {
            chunks[index] = data.clone();
        }

        synchronized boolean isComplete() {
            for (byte[] chunk : chunks) {
                if (chunk == null) {
                    return false;
                }
            }
            return true;
        }

        synchronized byte[] assemble() {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (byte[] chunk : chunks) {
                output.writeBytes(chunk);
            }
            return output.toByteArray();
        }
    }
}
