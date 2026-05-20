package ro.p2p.crypto.key;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class DhKeyAgreementService {

    private static final String KDF_LABEL = "RC6-P2P-DH-v1";
    private static final int PRIVATE_EXPONENT_BITS = 256;
    private static final int RC6_KEY_SIZE = 16;
    private static final int NONCE_SIZE = 16;
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private static final BigInteger GENERATOR = TWO;

    // RFC 3526, 2048-bit MODP Group (group 14). This keeps DH explicit and reproducible.
    private static final BigInteger PRIME =
            new BigInteger(
                    "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                            + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                            + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                            + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                            + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
                            + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
                            + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                            + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B"
                            + "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9"
                            + "DE2BCBF6955817183995497CEA956AE515D2261898FA0510"
                            + "15728E5A8AACAA68FFFFFFFFFFFFFFFF",
                    16);
    private static final int PUBLIC_VALUE_SIZE = (PRIME.bitLength() + 7) / 8;

    private final SecureRandom secureRandom;

    public DhKeyAgreementService() {
        this(new SecureRandom());
    }

    DhKeyAgreementService(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public DhInitiatorState createInitiatorState() {
        byte[] nonce = new byte[NONCE_SIZE];
        secureRandom.nextBytes(nonce);
        return createInitiatorState(generatePrivateExponent(), nonce);
    }

    DhInitiatorState createInitiatorState(BigInteger privateExponent, byte[] nonce) {
        validatePrivateExponent(privateExponent);
        validateNonce(nonce, "Initiator nonce");
        BigInteger publicValue = GENERATOR.modPow(privateExponent, PRIME);
        return new DhInitiatorState(privateExponent, encodePublicValue(publicValue), nonce);
    }

    public DhResponderResult createResponderResult(
            byte[] initiatorPublicKeyBytes, byte[] initiatorNonce) {
        byte[] responderNonce = new byte[NONCE_SIZE];
        secureRandom.nextBytes(responderNonce);
        return createResponderResult(
                initiatorPublicKeyBytes, initiatorNonce, generatePrivateExponent(), responderNonce);
    }

    DhResponderResult createResponderResult(
            byte[] initiatorPublicKeyBytes,
            byte[] initiatorNonce,
            BigInteger responderPrivateExponent,
            byte[] responderNonce) {
        validateNonce(initiatorNonce, "Initiator nonce");
        validateNonce(responderNonce, "Responder nonce");
        validatePrivateExponent(responderPrivateExponent);

        BigInteger initiatorPublicValue = decodeAndValidatePublicValue(initiatorPublicKeyBytes);
        BigInteger responderPublicValue = GENERATOR.modPow(responderPrivateExponent, PRIME);
        byte[] responderPublicKey = encodePublicValue(responderPublicValue);
        byte[] sharedSecret = computeSharedSecret(initiatorPublicValue, responderPrivateExponent);
        byte[] rc6Key =
                deriveRc6Key(
                        sharedSecret,
                        initiatorPublicKeyBytes,
                        responderPublicKey,
                        initiatorNonce,
                        responderNonce);

        return new DhResponderResult(responderPublicKey, responderNonce, rc6Key);
    }

    public byte[] completeInitiator(
            DhInitiatorState initiatorState,
            byte[] responderPublicKeyBytes,
            byte[] responderNonce) {
        if (initiatorState == null) {
            throw new IllegalArgumentException("Initiator state must not be null");
        }
        validateNonce(responderNonce, "Responder nonce");

        BigInteger responderPublicValue = decodeAndValidatePublicValue(responderPublicKeyBytes);
        byte[] sharedSecret =
                computeSharedSecret(responderPublicValue, initiatorState.getPrivateExponent());
        return deriveRc6Key(
                sharedSecret,
                initiatorState.getPublicKeyEncoded(),
                responderPublicKeyBytes,
                initiatorState.getNonce(),
                responderNonce);
    }

    private BigInteger generatePrivateExponent() {
        BigInteger exponent;
        do {
            exponent = new BigInteger(PRIVATE_EXPONENT_BITS, secureRandom);
        } while (exponent.compareTo(TWO) < 0 || exponent.compareTo(PRIME.subtract(TWO)) > 0);
        return exponent;
    }

    private byte[] computeSharedSecret(BigInteger peerPublicValue, BigInteger ownPrivateExponent) {
        BigInteger sharedValue = peerPublicValue.modPow(ownPrivateExponent, PRIME);
        if (sharedValue.compareTo(BigInteger.ONE) <= 0) {
            throw new IllegalArgumentException("Invalid DH shared secret");
        }
        return toFixedLength(sharedValue, PUBLIC_VALUE_SIZE);
    }

    private byte[] deriveRc6Key(
            byte[] sharedSecret,
            byte[] initiatorPublicKey,
            byte[] responderPublicKey,
            byte[] initiatorNonce,
            byte[] responderNonce) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(KDF_LABEL.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
            digest.update(sharedSecret);
            digest.update(initiatorPublicKey);
            digest.update(responderPublicKey);
            digest.update(initiatorNonce);
            digest.update(responderNonce);
            return Arrays.copyOf(digest.digest(), RC6_KEY_SIZE);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is required for DH key derivation", e);
        }
    }

    private BigInteger decodeAndValidatePublicValue(byte[] encodedPublicValue) {
        if (encodedPublicValue == null || encodedPublicValue.length != PUBLIC_VALUE_SIZE) {
            throw new IllegalArgumentException(
                    "DH public value must be exactly " + PUBLIC_VALUE_SIZE + " bytes");
        }
        BigInteger publicValue = new BigInteger(1, encodedPublicValue);
        if (publicValue.compareTo(TWO) < 0 || publicValue.compareTo(PRIME.subtract(TWO)) > 0) {
            throw new IllegalArgumentException("DH public value is outside the valid group range");
        }
        return publicValue;
    }

    private byte[] encodePublicValue(BigInteger publicValue) {
        return toFixedLength(publicValue, PUBLIC_VALUE_SIZE);
    }

    private byte[] toFixedLength(BigInteger value, int length) {
        byte[] raw = value.toByteArray();
        if (raw.length == length) {
            return raw;
        }
        if (raw.length == length + 1 && raw[0] == 0) {
            return Arrays.copyOfRange(raw, 1, raw.length);
        }
        if (raw.length > length) {
            throw new IllegalArgumentException("Value does not fit in expected DH byte length");
        }
        byte[] result = new byte[length];
        System.arraycopy(raw, 0, result, length - raw.length, raw.length);
        return result;
    }

    private void validatePrivateExponent(BigInteger privateExponent) {
        if (privateExponent == null
                || privateExponent.compareTo(TWO) < 0
                || privateExponent.compareTo(PRIME.subtract(TWO)) > 0) {
            throw new IllegalArgumentException("Invalid DH private exponent");
        }
    }

    private void validateNonce(byte[] nonce, String name) {
        if (nonce == null || nonce.length != NONCE_SIZE) {
            throw new IllegalArgumentException(name + " must be " + NONCE_SIZE + " bytes");
        }
    }

    public static final class DhInitiatorState {
        private final BigInteger privateExponent;
        private final byte[] publicKeyEncoded;
        private final byte[] nonce;

        private DhInitiatorState(
                BigInteger privateExponent, byte[] publicKeyEncoded, byte[] nonce) {
            this.privateExponent = privateExponent;
            this.publicKeyEncoded = Arrays.copyOf(publicKeyEncoded, publicKeyEncoded.length);
            this.nonce = Arrays.copyOf(nonce, nonce.length);
        }

        BigInteger getPrivateExponent() {
            return privateExponent;
        }

        public byte[] getPublicKeyEncoded() {
            return Arrays.copyOf(publicKeyEncoded, publicKeyEncoded.length);
        }

        public byte[] getNonce() {
            return Arrays.copyOf(nonce, nonce.length);
        }
    }

    public static final class DhResponderResult {
        private final byte[] publicKeyEncoded;
        private final byte[] nonce;
        private final byte[] rc6Key;

        public DhResponderResult(byte[] publicKeyEncoded, byte[] nonce, byte[] rc6Key) {
            this.publicKeyEncoded = Arrays.copyOf(publicKeyEncoded, publicKeyEncoded.length);
            this.nonce = Arrays.copyOf(nonce, nonce.length);
            this.rc6Key = Arrays.copyOf(rc6Key, rc6Key.length);
        }

        public byte[] getPublicKeyEncoded() {
            return Arrays.copyOf(publicKeyEncoded, publicKeyEncoded.length);
        }

        public byte[] getNonce() {
            return Arrays.copyOf(nonce, nonce.length);
        }

        public byte[] getRc6Key() {
            return Arrays.copyOf(rc6Key, rc6Key.length);
        }
    }
}
