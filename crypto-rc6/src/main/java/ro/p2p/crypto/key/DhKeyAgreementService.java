package ro.p2p.crypto.key;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class DhKeyAgreementService {

    private static final int DH_KEY_SIZE = 2048;
    private static final int RC6_KEY_SIZE = 16;
    private final SecureRandom secureRandom = new SecureRandom();

    public DhInitiatorState createInitiatorState() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(DH_KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            byte[] nonce = new byte[16];
            secureRandom.nextBytes(nonce);

            return new DhInitiatorState(keyPair, nonce);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to create DH initiator state", e);
        }
    }

    public DhResponderResult createResponderResult(byte[] initiatorPublicKeyBytes, byte[] initiatorNonce) {
        try {
            PublicKey initiatorPublicKey = decodePublicKey(initiatorPublicKeyBytes);
            DHParameterSpec params = ((DHPublicKey) initiatorPublicKey).getParams();

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(params);
            KeyPair responderKeyPair = keyPairGenerator.generateKeyPair();

            byte[] responderNonce = new byte[16];
            secureRandom.nextBytes(responderNonce);

            byte[] sharedSecret = computeSharedSecret(responderKeyPair, initiatorPublicKey);
            byte[] rc6Key = deriveRc6Key(sharedSecret, initiatorNonce, responderNonce);

            return new DhResponderResult(
                responderKeyPair.getPublic().getEncoded(),
                responderNonce,
                rc6Key
            );
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to create DH responder result", e);
        }
    }

    public byte[] completeInitiator(
        KeyPair initiatorKeyPair,
        byte[] responderPublicKeyBytes,
        byte[] initiatorNonce,
        byte[] responderNonce) {
        try {
            PublicKey responderPublicKey = decodePublicKey(responderPublicKeyBytes);
            byte[] sharedSecret = computeSharedSecret(initiatorKeyPair, responderPublicKey);
            return deriveRc6Key(sharedSecret, initiatorNonce, responderNonce);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to complete DH initiator flow", e);
        }
    }

    private PublicKey decodePublicKey(byte[] encoded) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
    }

    private byte[] computeSharedSecret(KeyPair ownKeyPair, PublicKey peerPublicKey)
        throws GeneralSecurityException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(ownKeyPair.getPrivate());
        keyAgreement.doPhase(peerPublicKey, true);
        return keyAgreement.generateSecret();
    }

    private byte[] deriveRc6Key(byte[] sharedSecret, byte[] initiatorNonce, byte[] responderNonce)
        throws GeneralSecurityException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(sharedSecret);
        digest.update(initiatorNonce);
        digest.update(responderNonce);
        return Arrays.copyOf(digest.digest(), RC6_KEY_SIZE);
    }

    public static final class DhInitiatorState {
        private final KeyPair keyPair;
        private final byte[] nonce;

        public DhInitiatorState(KeyPair keyPair, byte[] nonce) {
            this.keyPair = keyPair;
            this.nonce = nonce;
        }

        public KeyPair getKeyPair() {
            return keyPair;
        }

        public byte[] getPublicKeyEncoded() {
            return keyPair.getPublic().getEncoded();
        }

        public byte[] getNonce() {
            return nonce;
        }
    }

    public static final class DhResponderResult {
        private final byte[] publicKeyEncoded;
        private final byte[] nonce;
        private final byte[] rc6Key;

        public DhResponderResult(byte[] publicKeyEncoded, byte[] nonce, byte[] rc6Key) {
            this.publicKeyEncoded = publicKeyEncoded;
            this.nonce = nonce;
            this.rc6Key = rc6Key;
        }

        public byte[] getPublicKeyEncoded() {
            return publicKeyEncoded;
        }

        public byte[] getNonce() {
            return nonce;
        }

        public byte[] getRc6Key() {
            return rc6Key;
        }
    }
}
