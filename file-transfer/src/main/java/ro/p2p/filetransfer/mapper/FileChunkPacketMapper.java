package ro.p2p.filetransfer.mapper;

import ro.p2p.protocol.packet.FileChunkPacket;

public class FileChunkPacketMapper {

    public FileChunkPacket toPacket(
            String transferId, int chunkIndex, int totalChunks, byte[] encryptedPayload) {
        return new FileChunkPacket(transferId, chunkIndex, totalChunks, encryptedPayload);
    }
}
