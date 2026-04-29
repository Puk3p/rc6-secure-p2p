package ro.p2p.node.app;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import ro.p2p.common.model.PeerAddress;
import ro.p2p.filetransfer.chunk.ChunkEncryptor;
import ro.p2p.filetransfer.chunk.FileChunker;
import ro.p2p.filetransfer.service.FileReceiveService;
import ro.p2p.network.connection.PeerConnection;
import ro.p2p.protocol.packet.FileChunkPacket;

class EncryptedCommunicationIntegrationTest {

    @Test
    void twoStationsExchangeEncryptedMessageOverTcp() throws Exception {
        CountDownLatch receivedByB = new CountDownLatch(1);
        StringBuilder receivedMessage = new StringBuilder();

        try (NodeRuntime nodeA = new NodeRuntime("node-a", message -> {});
                NodeRuntime nodeB =
                        new NodeRuntime(
                                "node-b",
                                message -> {
                                    receivedMessage.append(message);
                                    receivedByB.countDown();
                                })) {
            nodeA.start(0);
            nodeB.start(0);

            PeerConnection connectionToB =
                    nodeA.connectTo(new PeerAddress("127.0.0.1", nodeB.getLocalPort()));

            String message = "Salut din Node A, criptat cu RC6 dupa handshake DH.";
            nodeA.sendMessage(connectionToB.getPeerId(), message);

            assertTrue(receivedByB.await(5, TimeUnit.SECONDS));
            assertEquals(message, receivedMessage.toString());
            assertTrue(
                    nodeA.getContext().getMessageReceiveService().getReceivedMessages().isEmpty(),
                    "Sender should not receive its own message");
        }
    }

    @Test
    void diffieHellmanHandshakeCreatesSameSessionKeyOnBothStations() throws Exception {
        try (NodeRuntime nodeA = new NodeRuntime("node-a", message -> {});
                NodeRuntime nodeB = new NodeRuntime("node-b", message -> {})) {
            nodeA.start(0);
            nodeB.start(0);

            PeerConnection connectionToB =
                    nodeA.connectTo(new PeerAddress("127.0.0.1", nodeB.getLocalPort()));

            org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(
                    Duration.ofSeconds(5),
                    () -> {
                        while (nodeB.getContext()
                                .getConnectionRegistry()
                                .find("node-a")
                                .isEmpty()) {
                            Thread.sleep(20);
                        }
                    });

            PeerConnection connectionToA =
                    nodeB.getContext().getConnectionRegistry().find("node-a").orElseThrow();
            assertArrayEquals(connectionToB.getSessionKey(), connectionToA.getSessionKey());
        }
    }

    @Test
    void encryptedChunksAreReassembledAtReceiver() {
        byte[] sessionKey = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);
        byte[] data =
                ("Acesta este un mesaj mai lung care trebuie impartit in mai multe blocuri "
                                + "si recompus corect la receptie.")
                        .getBytes(StandardCharsets.UTF_8);

        FileChunker chunker = new FileChunker(17);
        ChunkEncryptor encryptor = new ChunkEncryptor();
        FileReceiveService receiver = new FileReceiveService();
        java.util.List<byte[]> chunks = chunker.split(data);

        Optional<byte[]> assembled = Optional.empty();
        for (int i = 0; i < chunks.size(); i++) {
            byte[] encrypted = encryptor.encrypt(chunks.get(i), sessionKey);
            assertFalse(java.util.Arrays.equals(chunks.get(i), encrypted));
            assembled =
                    receiver.receive(
                            new FileChunkPacket("transfer-1", i, chunks.size(), encrypted),
                            sessionKey);
        }

        assertTrue(assembled.isPresent());
        assertArrayEquals(data, assembled.get());
    }
}
