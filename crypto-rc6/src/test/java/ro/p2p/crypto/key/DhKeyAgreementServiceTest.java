package ro.p2p.crypto.key;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
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
                        initiator, responder.getPublicKeyEncoded(), responder.getNonce());

        assertNotNull(initiatorKey);
        assertNotNull(responder.getRc6Key());
        assertEquals(16, initiatorKey.length);
        assertEquals(16, responder.getRc6Key().length);
        assertArrayEquals(initiatorKey, responder.getRc6Key());
    }

    @Test
    void deterministicDhVectorDerivesExpectedRc6Key() {
        DhKeyAgreementService service = new DhKeyAgreementService();
        byte[] initiatorNonce = hex("00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F");
        byte[] responderNonce = hex("10 11 12 13 14 15 16 17 18 19 1A 1B 1C 1D 1E 1F");

        DhKeyAgreementService.DhInitiatorState initiator =
                service.createInitiatorState(BigInteger.valueOf(5L), initiatorNonce);
        DhKeyAgreementService.DhResponderResult responder =
                service.createResponderResult(
                        initiator.getPublicKeyEncoded(),
                        initiator.getNonce(),
                        BigInteger.valueOf(7L),
                        responderNonce);

        byte[] initiatorKey =
                service.completeInitiator(
                        initiator, responder.getPublicKeyEncoded(), responder.getNonce());

        assertEquals(256, initiator.getPublicKeyEncoded().length);
        assertEquals("0000000000000020", tailHex(initiator.getPublicKeyEncoded(), 8));
        assertEquals("0000000000000080", tailHex(responder.getPublicKeyEncoded(), 8));
        assertArrayEquals(hex("3C EA AE 4B FA 0F E1 35 B0 C9 91 C9 D0 39 43 AA"), initiatorKey);
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
                        initiator, responder.getPublicKeyEncoded(), responder.getNonce());

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
                        initiator1, responder1.getPublicKeyEncoded(), responder1.getNonce());

        DhKeyAgreementService.DhInitiatorState initiator2 = service.createInitiatorState();
        DhKeyAgreementService.DhResponderResult responder2 =
                service.createResponderResult(
                        initiator2.getPublicKeyEncoded(), initiator2.getNonce());

        byte[] sessionKey2 =
                service.completeInitiator(
                        initiator2, responder2.getPublicKeyEncoded(), responder2.getNonce());

        assertFalse(Arrays.equals(sessionKey1, sessionKey2));
    }

    @Test
    void invalidResponderPublicKeyThrows() {
        DhKeyAgreementService service = new DhKeyAgreementService();

        DhKeyAgreementService.DhInitiatorState initiator = service.createInitiatorState();

        byte[] invalidPublicKey = new byte[] {0x01, 0x02, 0x03};

        assertThrows(
                IllegalArgumentException.class,
                () -> service.completeInitiator(initiator, invalidPublicKey, new byte[16]));
    }

    @Test
    void invalidInitiatorPublicKeyThrows() {
        DhKeyAgreementService service = new DhKeyAgreementService();

        byte[] invalidPublicKey = new byte[] {0x01, 0x02, 0x03};
        byte[] initiatorNonce = new byte[16];

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createResponderResult(invalidPublicKey, initiatorNonce));
    }

    @Test
    void invalidNonceLengthThrows() {
        DhKeyAgreementService service = new DhKeyAgreementService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createInitiatorState(BigInteger.valueOf(5L), new byte[8]));
    }

    @Test
    void exportedArraysAreDefensiveCopies() {
        DhKeyAgreementService service = new DhKeyAgreementService();
        DhKeyAgreementService.DhInitiatorState initiator = service.createInitiatorState();
        byte[] nonce = initiator.getNonce();
        byte[] publicKey = initiator.getPublicKeyEncoded();

        nonce[0] ^= 0x7F;
        publicKey[publicKey.length - 1] ^= 0x7F;

        assertFalse(Arrays.equals(nonce, initiator.getNonce()));
        assertFalse(Arrays.equals(publicKey, initiator.getPublicKeyEncoded()));
    }

    private static String tailHex(byte[] bytes, int tailBytes) {
        byte[] tail = Arrays.copyOfRange(bytes, bytes.length - tailBytes, bytes.length);
        return toHex(tail);
    }

    private static byte[] hex(String value) {
        String normalized = value.replaceAll("\\s+", "");
        byte[] bytes = new byte[normalized.length() / 2];
        for (int i = 0; i < normalized.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(normalized.substring(i, i + 2), 16);
        }
        return bytes;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02X", value));
        }
        return builder.toString();
    }
}
