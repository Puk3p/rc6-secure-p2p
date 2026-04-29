package ro.p2p.filetransfer.service;

import java.util.Optional;
import ro.p2p.filetransfer.chunk.ChunkDecryptor;
import ro.p2p.protocol.packet.FileChunkPacket;

public class FileReceiveService {

    private final ChunkDecryptor decryptor = new ChunkDecryptor();
    private final FileAssemblyService assemblyService = new FileAssemblyService();

    public Optional<byte[]> receive(FileChunkPacket packet, byte[] sessionKey) {
        byte[] plaintext = decryptor.decrypt(packet.getEncryptedPayload(), sessionKey);
        return assemblyService.addChunk(
                packet.getTransferId(), packet.getChunkIndex(), packet.getTotalChunks(), plaintext);
    }
}
