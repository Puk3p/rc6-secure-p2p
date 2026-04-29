package ro.p2p.messaging.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import ro.p2p.crypto.facade.CryptoFacade;
import ro.p2p.messaging.tracker.PendingMessageStore;
import ro.p2p.protocol.packet.MessagePacket;

public class MessageReceiveService {

    private final PendingMessageStore pendingMessageStore = new PendingMessageStore();
    private final List<String> receivedMessages = Collections.synchronizedList(new ArrayList<>());
    private final Consumer<String> onMessage;

    public MessageReceiveService() {
        this(message -> {});
    }

    public MessageReceiveService(Consumer<String> onMessage) {
        this.onMessage = onMessage;
    }

    public Optional<String> receive(MessagePacket packet, byte[] sessionKey) {
        CryptoFacade crypto = new CryptoFacade(sessionKey);
        byte[] plaintextChunk = crypto.decryptWithMac(packet.getEncryptedPayload());
        Optional<byte[]> assembled =
                pendingMessageStore.addChunk(
                        packet.getMessageId(),
                        packet.getChunkIndex(),
                        packet.getTotalChunks(),
                        plaintextChunk);
        if (assembled.isEmpty()) {
            return Optional.empty();
        }
        String message = new String(assembled.get(), StandardCharsets.UTF_8);
        receivedMessages.add(message);
        onMessage.accept(message);
        return Optional.of(message);
    }

    public List<String> getReceivedMessages() {
        synchronized (receivedMessages) {
            return List.copyOf(receivedMessages);
        }
    }
}
