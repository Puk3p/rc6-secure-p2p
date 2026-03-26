package ro.p2p.crypto.mode;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RC6CtrModeTest {

    private static final byte[] KEY =
            new byte[] {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
            };

    private static final byte[] NONCE =
            new byte[] {
                0x10,
                0x20,
                0x30,
                0x40,
                0x50,
                0x60,
                0x70,
                (byte) 0x80,
                (byte) 0x90,
                (byte) 0xA0,
                (byte) 0xB0,
                (byte) 0xC0,
                (byte) 0xD0,
                (byte) 0xE0,
                (byte) 0xF0,
                0x00
            };

    @Test
    void encryptDecryptShortMessage() {
        RC6CtrMode ctr = new RC6CtrMode(KEY);
        byte[] plaintext = "Hello CTR!".getBytes();

        byte[] ciphertext = ctr.encrypt(plaintext, NONCE);
        byte[] decrypted = ctr.decrypt(ciphertext, NONCE);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void outputSameLengthAsInput() {
        RC6CtrMode ctr = new RC6CtrMode(KEY);
        byte[] plaintext = "Exact length preserved".getBytes();

        byte[] ciphertext = ctr.encrypt(plaintext, NONCE);

        assertEquals(plaintext.length, ciphertext.length);
    }

    @Test
    void encryptDecryptMultipleBlocks() {
        RC6CtrMode ctr = new RC6CtrMode(KEY);
        byte[] plaintext = new byte[100];
        for (int i = 0; i < 100; i++) {
            plaintext[i] = (byte) (i & 0xFF);
        }

        byte[] ciphertext = ctr.encrypt(plaintext, NONCE);
        byte[] decrypted = ctr.decrypt(ciphertext, NONCE);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void encryptDecryptSingleByte() {
        RC6CtrMode ctr = new RC6CtrMode(KEY);
        byte[] plaintext = new byte[] {0x42};

        byte[] ciphertext = ctr.encrypt(plaintext, NONCE);
        byte[] decrypted = ctr.decrypt(ciphertext, NONCE);

        assertEquals(1, ciphertext.length);
        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void differentNonceProducesDifferentCiphertext() {
        RC6CtrMode ctr = new RC6CtrMode(KEY);
        byte[] plaintext = "Same message".getBytes();
        byte[] nonce2 = new byte[16];
        nonce2[0] = (byte) 0xFF;

        byte[] ct1 = ctr.encrypt(plaintext, NONCE);
        byte[] ct2 = ctr.encrypt(plaintext, nonce2);

        assertFalse(java.util.Arrays.equals(ct1, ct2));
    }

    @Test
    void invalidNonceThrows() {
        RC6CtrMode ctr = new RC6CtrMode(KEY);

        assertThrows(
                IllegalArgumentException.class, () -> ctr.encrypt("test".getBytes(), new byte[8]));
        assertThrows(IllegalArgumentException.class, () -> ctr.encrypt("test".getBytes(), null));
    }

    @Test
    void nullInputThrows() {
        RC6CtrMode ctr = new RC6CtrMode(KEY);

        assertThrows(IllegalArgumentException.class, () -> ctr.encrypt(null, NONCE));
    }
}
