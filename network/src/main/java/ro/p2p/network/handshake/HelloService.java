package ro.p2p.network.handshake;

import ro.p2p.crypto.key.DhKeyAgreementService;
import ro.p2p.crypto.key.DhKeyAgreementService.DhInitiatorState;
import ro.p2p.crypto.key.DhKeyAgreementService.DhResponderResult;
import ro.p2p.protocol.packet.HelloPacket;

public class HelloService {

    private final DhKeyAgreementService keyAgreementService;

    public HelloService() {
        this(new DhKeyAgreementService());
    }

    public HelloService(DhKeyAgreementService keyAgreementService) {
        this.keyAgreementService = keyAgreementService;
    }

    public InitiatorHello createInitiatorHello(String nodeId, int listenPort) {
        DhInitiatorState state = keyAgreementService.createInitiatorState();
        HelloPacket packet =
                new HelloPacket(
                        nodeId, listenPort, state.getPublicKeyEncoded(), state.getNonce(), false);
        return new InitiatorHello(packet, state);
    }

    public ResponderHello createResponderHello(
            String nodeId, int listenPort, HelloPacket initiator) {
        DhResponderResult result =
                keyAgreementService.createResponderResult(
                        initiator.getPublicKey(), initiator.getNonce());
        HelloPacket packet =
                new HelloPacket(
                        nodeId, listenPort, result.getPublicKeyEncoded(), result.getNonce(), true);
        return new ResponderHello(packet, result.getRc6Key());
    }

    public byte[] completeInitiator(InitiatorHello initiator, HelloPacket responder) {
        return keyAgreementService.completeInitiator(
                initiator.getState().getKeyPair(),
                responder.getPublicKey(),
                initiator.getState().getNonce(),
                responder.getNonce());
    }

    public static final class InitiatorHello {
        private final HelloPacket packet;
        private final DhInitiatorState state;

        public InitiatorHello(HelloPacket packet, DhInitiatorState state) {
            this.packet = packet;
            this.state = state;
        }

        public HelloPacket getPacket() {
            return packet;
        }

        public DhInitiatorState getState() {
            return state;
        }
    }

    public static final class ResponderHello {
        private final HelloPacket packet;
        private final byte[] sessionKey;

        public ResponderHello(HelloPacket packet, byte[] sessionKey) {
            this.packet = packet;
            this.sessionKey = sessionKey.clone();
        }

        public HelloPacket getPacket() {
            return packet;
        }

        public byte[] getSessionKey() {
            return sessionKey.clone();
        }
    }
}
