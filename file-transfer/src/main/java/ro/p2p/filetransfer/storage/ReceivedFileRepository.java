package ro.p2p.filetransfer.storage;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ReceivedFileRepository {

    private final Map<String, byte[]> files = new ConcurrentHashMap<>();

    public void save(String transferId, byte[] data) {
        files.put(transferId, data.clone());
    }

    public Optional<byte[]> find(String transferId) {
        byte[] data = files.get(transferId);
        return data == null ? Optional.empty() : Optional.of(data.clone());
    }
}
