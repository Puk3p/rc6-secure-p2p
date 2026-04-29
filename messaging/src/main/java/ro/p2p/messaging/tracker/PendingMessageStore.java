package ro.p2p.messaging.tracker;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PendingMessageStore {

    private final Map<String, PendingMessage> messages = new ConcurrentHashMap<>();

    public Optional<byte[]> addChunk(
            String messageId, int chunkIndex, int totalChunks, byte[] plaintext) {
        PendingMessage pending =
                messages.computeIfAbsent(messageId, id -> new PendingMessage(totalChunks));
        pending.add(chunkIndex, plaintext);
        if (!pending.isComplete()) {
            return Optional.empty();
        }
        messages.remove(messageId);
        return Optional.of(pending.assemble());
    }

    private static final class PendingMessage {
        private final byte[][] chunks;

        PendingMessage(int totalChunks) {
            this.chunks = new byte[totalChunks][];
        }

        synchronized void add(int index, byte[] data) {
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
