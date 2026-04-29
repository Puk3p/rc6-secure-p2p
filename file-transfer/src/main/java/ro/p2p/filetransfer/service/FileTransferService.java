package ro.p2p.filetransfer.service;

import java.util.Optional;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.protocol.packet.FileChunkPacket;

public class FileTransferService {

    private final FileSendService sendService;
    private final FileReceiveService receiveService;

    public FileTransferService() {
        this(new FileSendService(), new FileReceiveService());
    }

    public FileTransferService(FileSendService sendService, FileReceiveService receiveService) {
        this.sendService = sendService;
        this.receiveService = receiveService;
    }

    public String sendBytes(PeerConnection connection, byte[] data) {
        return sendService.sendBytes(connection, data);
    }

    public Optional<byte[]> receive(FileChunkPacket packet, byte[] sessionKey) {
        return receiveService.receive(packet, sessionKey);
    }
}
