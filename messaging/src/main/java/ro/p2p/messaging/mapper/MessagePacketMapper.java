package ro.p2p.messaging.mapper;

import ro.p2p.protocol.packet.MessagePacket;

public class MessagePacketMapper {

    public MessagePacket toPacket(
            String messageId,
            long timestampMillis,
            int chunkIndex,
            int totalChunks,
            byte[] encryptedPayload) {
        return new MessagePacket(
                messageId, timestampMillis, chunkIndex, totalChunks, encryptedPayload);
    }
}
