package ro.p2p.crypto.key;

import java.security.SecureRandom;

public class NonceGenerator {

    private static final int NONCE_SIZE = 16;
    private final SecureRandom secureRandom;

    public NonceGenerator() {
        this.secureRandom = new SecureRandom();
    }

    public byte[] generateNonce() {
        byte[] nonce = new byte[NONCE_SIZE];
        secureRandom.nextBytes(nonce);
        return nonce;
    }

    public byte[] generateIv() {
        return generateNonce();
    }

    public static int getNonceSize() {
        return NONCE_SIZE;
    }
}
