package ro.p2p.crypto.key;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NonceGeneratorTest {

    @Test
    void generateNonceReturns16Bytes() {
        NonceGenerator generator = new NonceGenerator();
        byte[] nonce = generator.generateNonce();

        assertEquals(16, nonce.length);
    }

    @Test
    void generateIvReturns16Bytes() {
        NonceGenerator generator = new NonceGenerator();
        byte[] iv = generator.generateIv();

        assertEquals(16, iv.length);
    }

    @Test
    void twoNoncesAreDifferent() {
        NonceGenerator generator = new NonceGenerator();
        byte[] nonce1 = generator.generateNonce();
        byte[] nonce2 = generator.generateNonce();

        assertFalse(java.util.Arrays.equals(nonce1, nonce2));
    }

    @Test
    void getNonceSizeReturns16() {
        assertEquals(16, NonceGenerator.getNonceSize());
    }
}
