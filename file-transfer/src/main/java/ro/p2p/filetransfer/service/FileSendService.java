package ro.p2p.filetransfer.service;

import java.util.List;
import ro.p2p.common.util.IdGenerator;
import ro.p2p.filetransfer.chunk.ChunkEncryptor;
import ro.p2p.filetransfer.chunk.FileChunker;
import ro.p2p.filetransfer.mapper.FileChunkPacketMapper;
import ro.p2p.network.connection.PeerConnection;

public class FileSendService {

    private final FileChunker chunker;
    private final ChunkEncryptor encryptor = new ChunkEncryptor();
    private final FileChunkPacketMapper mapper = new FileChunkPacketMapper();

    public FileSendService() {
        this(new FileChunker());
    }

    public FileSendService(FileChunker chunker) {
        this.chunker = chunker;
    }

    public String sendBytes(PeerConnection connection, byte[] data) {
        String transferId = IdGenerator.newId();
        List<byte[]> chunks = chunker.split(data);
        for (int i = 0; i < chunks.size(); i++) {
            byte[] encrypted = encryptor.encrypt(chunks.get(i), connection.getSessionKey());
            connection.send(mapper.toPacket(transferId, i, chunks.size(), encrypted));
        }
        return transferId;
    }
}
