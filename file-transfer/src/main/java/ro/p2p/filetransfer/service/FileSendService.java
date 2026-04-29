package ro.p2p.filetransfer.service;

import java.util.List;
import ro.p2p.common.model.FileMetadata;
import ro.p2p.common.util.IdGenerator;
import ro.p2p.filetransfer.chunk.ChunkEncryptor;
import ro.p2p.filetransfer.chunk.FileChunker;
import ro.p2p.filetransfer.mapper.FileChunkPacketMapper;
import ro.p2p.filetransfer.mapper.FileMetaPacketMapper;
import ro.p2p.network.connection.PeerConnection;

public class FileSendService {

    private final FileChunker chunker;
    private final ChunkEncryptor encryptor = new ChunkEncryptor();
    private final FileChunkPacketMapper mapper = new FileChunkPacketMapper();
    private final FileMetaPacketMapper metaMapper = new FileMetaPacketMapper();

    public FileSendService() {
        this(new FileChunker());
    }

    public FileSendService(FileChunker chunker) {
        this.chunker = chunker;
    }

    public String sendBytes(PeerConnection connection, byte[] data) {
        return sendBytes(connection, data, "received.bin");
    }

    public String sendBytes(PeerConnection connection, byte[] data, String fileName) {
        String transferId = IdGenerator.newId();
        List<byte[]> chunks = chunker.split(data);
        connection.send(
                metaMapper.toPacket(
                        new FileMetadata(
                                transferId, fileName, data.length, chunks.size(), new byte[0])));
        for (int i = 0; i < chunks.size(); i++) {
            byte[] encrypted = encryptor.encrypt(chunks.get(i), connection.getSessionKey());
            connection.send(mapper.toPacket(transferId, i, chunks.size(), encrypted));
        }
        return transferId;
    }
}
