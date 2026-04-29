package ro.p2p.messaging.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import ro.p2p.common.constants.PacketConstants;
import ro.p2p.common.enums.PacketType;
import ro.p2p.common.util.IdGenerator;
import ro.p2p.common.util.TimeUtils;
import ro.p2p.crypto.facade.CryptoFacade;
import ro.p2p.messaging.mapper.MessagePacketMapper;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.protocol.packet.AckPacket;
import ro.p2p.protocol.packet.MessagePacket;

public class SecureMessageService implements MessageService {

    private final int chunkSize;
    private final MessagePacketMapper mapper = new MessagePacketMapper();

    public SecureMessageService() {
        this(PacketConstants.DEFAULT_CHUNK_SIZE);
    }

    public SecureMessageService(int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }
        this.chunkSize = chunkSize;
    }

    @Override
    public String sendMessage(PeerConnection connection, String message) {
        byte[] plaintext = message.getBytes(StandardCharsets.UTF_8);
        String messageId = IdGenerator.newId();
        int totalChunks = Math.max(1, (int) Math.ceil((double) plaintext.length / chunkSize));
        long timestamp = TimeUtils.nowMillis();
        CryptoFacade crypto = new CryptoFacade(connection.getSessionKey());

        for (int index = 0; index < totalChunks; index++) {
            int from = index * chunkSize;
            int to = Math.min(plaintext.length, from + chunkSize);
            byte[] chunk = Arrays.copyOfRange(plaintext, from, to);
            byte[] encrypted = crypto.encryptWithMac(chunk);
            MessagePacket packet =
                    mapper.toPacket(messageId, timestamp, index, totalChunks, encrypted);
            connection.send(packet);
        }
        return messageId;
    }

    public void acknowledge(PeerConnection connection, MessagePacket packet) {
        connection.send(new AckPacket(PacketType.MESSAGE, packet.getMessageId()));
    }
}
