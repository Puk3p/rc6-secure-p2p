package ro.p2p.node.handler;

import java.util.Optional;
import ro.p2p.filetransfer.service.FileTransferService;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.protocol.packet.FileChunkPacket;

public class FilePacketHandler {

    private final FileTransferService fileTransferService;

    public FilePacketHandler(FileTransferService fileTransferService) {
        this.fileTransferService = fileTransferService;
    }

    public Optional<byte[]> handle(PeerConnection connection, FileChunkPacket packet) {
        return fileTransferService.receive(packet, connection.getSessionKey());
    }
}
