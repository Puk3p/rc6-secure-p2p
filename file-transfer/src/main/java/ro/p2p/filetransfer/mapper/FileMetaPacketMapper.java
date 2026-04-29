package ro.p2p.filetransfer.mapper;

import ro.p2p.common.model.FileMetadata;
import ro.p2p.protocol.packet.FileMetaPacket;

public class FileMetaPacketMapper {

    public FileMetaPacket toPacket(FileMetadata metadata) {
        return new FileMetaPacket(
                metadata.getTransferId(),
                metadata.getFileName(),
                metadata.getFileSize(),
                metadata.getTotalChunks(),
                metadata.getHash());
    }
}
