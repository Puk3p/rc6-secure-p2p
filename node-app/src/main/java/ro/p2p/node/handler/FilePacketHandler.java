package ro.p2p.node.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import ro.p2p.filetransfer.service.FileTransferService;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.protocol.packet.FileChunkPacket;
import ro.p2p.protocol.packet.FileMetaPacket;

public class FilePacketHandler {

    private final String localNodeId;
    private final FileTransferService fileTransferService;
    private final Map<String, FileMetaPacket> metadataByTransferId = new ConcurrentHashMap<>();

    public FilePacketHandler(String localNodeId, FileTransferService fileTransferService) {
        this.localNodeId = localNodeId;
        this.fileTransferService = fileTransferService;
    }

    public void handleMeta(FileMetaPacket packet) {
        metadataByTransferId.put(packet.getTransferId(), packet);
        System.out.println(
                "\n[file] Incoming "
                        + packet.getFileName()
                        + " ("
                        + packet.getFileSize()
                        + " bytes, "
                        + packet.getTotalChunks()
                        + " chunks)");
    }

    public Optional<Path> handle(PeerConnection connection, FileChunkPacket packet) {
        Optional<byte[]> assembled =
                fileTransferService.receive(packet, connection.getSessionKey());
        if (assembled.isEmpty()) {
            return Optional.empty();
        }

        FileMetaPacket metadata = metadataByTransferId.remove(packet.getTransferId());
        String fileName =
                metadata == null ? packet.getTransferId() + ".bin" : metadata.getFileName();
        Path output = Path.of("received", localNodeId, sanitize(fileName));
        try {
            Files.createDirectories(output.getParent());
            Files.write(output, assembled.get());
            System.out.println("\n[file] Saved received file to " + output);
            return Optional.of(output);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save received file: " + output, e);
        }
    }

    private String sanitize(String fileName) {
        return Path.of(fileName).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
