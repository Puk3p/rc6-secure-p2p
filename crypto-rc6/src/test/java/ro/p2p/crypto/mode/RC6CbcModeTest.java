package ro.p2p.crypto.mode;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RC6CbcModeTest {

    private static final byte[] KEY =
            new byte[] {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
            };

    private static final byte[] IV =
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
        RC6CbcMode cbc = new RC6CbcMode(KEY);
        byte[] plaintext = "Hello RC6!".getBytes();

        byte[] ciphertext = cbc.encrypt(plaintext, IV);
        byte[] decrypted = cbc.decrypt(ciphertext, IV);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void encryptDecryptExactBlockSize() {
        RC6CbcMode cbc = new RC6CbcMode(KEY);
        byte[] plaintext = new byte[16];
        for (int i = 0; i < 16; i++) {
            plaintext[i] = (byte) (i + 1);
        }

        byte[] ciphertext = cbc.encrypt(plaintext, IV);
        byte[] decrypted = cbc.decrypt(ciphertext, IV);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void encryptDecryptMultipleBlocks() {
        RC6CbcMode cbc = new RC6CbcMode(KEY);
        byte[] plaintext = new byte[100];
        for (int i = 0; i < 100; i++) {
            plaintext[i] = (byte) (i & 0xFF);
        }

        byte[] ciphertext = cbc.encrypt(plaintext, IV);
        byte[] decrypted = cbc.decrypt(ciphertext, IV);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void encryptDecryptEmptyData() {
        RC6CbcMode cbc = new RC6CbcMode(KEY);
        byte[] plaintext = new byte[0];

        byte[] ciphertext = cbc.encrypt(plaintext, IV);
        byte[] decrypted = cbc.decrypt(ciphertext, IV);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void ciphertextDiffersFromPlaintext() {
        RC6CbcMode cbc = new RC6CbcMode(KEY);
        byte[] plaintext = "Sensitive data".getBytes();

        byte[] ciphertext = cbc.encrypt(plaintext, IV);

        assertFalse(java.util.Arrays.equals(plaintext, ciphertext));
    }

    @Test
    void differentIvProducesDifferentCiphertext() {
        RC6CbcMode cbc = new RC6CbcMode(KEY);
        byte[] plaintext = "Test message".getBytes();
        byte[] iv2 = new byte[16];
        iv2[0] = (byte) 0xFF;

        byte[] ciphertext1 = cbc.encrypt(plaintext, IV);
        byte[] ciphertext2 = cbc.encrypt(plaintext, iv2);

        assertFalse(java.util.Arrays.equals(ciphertext1, ciphertext2));
    }

    @Test
    void invalidIvThrows() {
        RC6CbcMode cbc = new RC6CbcMode(KEY);

        assertThrows(
                IllegalArgumentException.class, () -> cbc.encrypt("test".getBytes(), new byte[10]));
        assertThrows(IllegalArgumentException.class, () -> cbc.encrypt("test".getBytes(), null));
    }

    @Test
    void nullPlaintextThrows() {
        RC6CbcMode cbc = new RC6CbcMode(KEY);

        assertThrows(IllegalArgumentException.class, () -> cbc.encrypt(null, IV));
    }
}
