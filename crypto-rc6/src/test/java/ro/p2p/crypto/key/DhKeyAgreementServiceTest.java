package ro.p2p.crypto.key;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class DhKeyAgreementServiceTest {
    @Test
    void initiatorAndResponderDeriveSameRc6Key() {
        DhKeyAgreementService service = new DhKeyAgreementService();

        DhKeyAgreementService.DhInitiatorState initiator = service.createInitiatorState();

        DhKeyAgreementService.DhResponderResult responder =
                service.createResponderResult(
                        initiator.getPublicKeyEncoded(), initiator.getNonce());

        byte[] initiatorKey =
                service.completeInitiator(
                        initiator.getKeyPair(),
                        responder.getPublicKeyEncoded(),
                        initiator.getNonce(),
                        responder.getNonce());

        assertNotNull(initiatorKey);
        assertNotNull(responder.getRc6Key());
        assertEquals(16, initiatorKey.length);
        assertEquals(16, responder.getRc6Key().length);
        assertArrayEquals(initiatorKey, responder.getRc6Key());
    }

    @Test
    void derivedKeyHasExpectedRc6Length() {
        DhKeyAgreementService service = new DhKeyAgreementService();

        DhKeyAgreementService.DhInitiatorState initiator = service.createInitiatorState();

        DhKeyAgreementService.DhResponderResult responder =
                service.createResponderResult(
                        initiator.getPublicKeyEncoded(), initiator.getNonce());

        byte[] initiatorKey =
                service.completeInitiator(
                        initiator.getKeyPair(),
                        responder.getPublicKeyEncoded(),
                        initiator.getNonce(),
                        responder.getNonce());

        assertEquals(16, initiatorKey.length);
    }

    @Test
    void differentSessionsProduceDifferentKeys() {
        DhKeyAgreementService service = new DhKeyAgreementService();

        DhKeyAgreementService.DhInitiatorState initiator1 = service.createInitiatorState();
        DhKeyAgreementService.DhResponderResult responder1 =
                service.createResponderResult(
                        initiator1.getPublicKeyEncoded(), initiator1.getNonce());

        byte[] sessionKey1 =
                service.completeInitiator(
                        initiator1.getKeyPair(),
                        responder1.getPublicKeyEncoded(),
                        initiator1.getNonce(),
                        responder1.getNonce());

        DhKeyAgreementService.DhInitiatorState initiator2 = service.createInitiatorState();
        DhKeyAgreementService.DhResponderResult responder2 =
                service.createResponderResult(
                        initiator2.getPublicKeyEncoded(), initiator2.getNonce());

        byte[] sessionKey2 =
                service.completeInitiator(
                        initiator2.getKeyPair(),
                        responder2.getPublicKeyEncoded(),
                        initiator2.getNonce(),
                        responder2.getNonce());

        assertFalse(Arrays.equals(sessionKey1, sessionKey2));
    }

    @Test
    void invalidResponderPublicKeyThrows() {
        DhKeyAgreementService service = new DhKeyAgreementService();

        DhKeyAgreementService.DhInitiatorState initiator = service.createInitiatorState();

        byte[] invalidPublicKey = new byte[] {0x01, 0x02, 0x03};

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.completeInitiator(
                                initiator.getKeyPair(),
                                invalidPublicKey,
                                initiator.getNonce(),
                                new byte[16]));
    }

    @Test
    void invalidInitiatorPublicKeyThrows() {
        DhKeyAgreementService service = new DhKeyAgreementService();

        byte[] invalidPublicKey = new byte[] {0x01, 0x02, 0x03};
        byte[] initiatorNonce = new byte[16];

        assertThrows(
                IllegalStateException.class,
                () -> service.createResponderResult(invalidPublicKey, initiatorNonce));
    }
}
