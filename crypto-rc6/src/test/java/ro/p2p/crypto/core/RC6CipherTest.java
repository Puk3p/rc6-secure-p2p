package ro.p2p.crypto.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RC6CipherTest {

    private static final byte[] KEY_128 =
            new byte[] {
                0x01,
                0x23,
                0x45,
                0x67,
                (byte) 0x89,
                (byte) 0xAB,
                (byte) 0xCD,
                (byte) 0xEF,
                0x01,
                0x23,
                0x45,
                0x67,
                (byte) 0x89,
                (byte) 0xAB,
                (byte) 0xCD,
                (byte) 0xEF
            };

    private static final byte[] PLAINTEXT_BLOCK =
            new byte[] {
                0x02,
                0x13,
                0x24,
                0x35,
                0x46,
                0x57,
                0x68,
                0x79,
                (byte) 0x8A,
                (byte) 0x9B,
                (byte) 0xAC,
                (byte) 0xBD,
                (byte) 0xCE,
                (byte) 0xDF,
                (byte) 0xE0,
                (byte) 0xF1
            };

    @Test
    void encryptThenDecryptReturnsOriginalPlaintext() {
        RC6Cipher cipher = new RC6Cipher(KEY_128);

        byte[] ciphertext = cipher.encryptBlock(PLAINTEXT_BLOCK);
        byte[] decrypted = cipher.decryptBlock(ciphertext);

        assertArrayEquals(PLAINTEXT_BLOCK, decrypted);
    }

    @Test
    void encryptProducesDifferentOutput() {
        RC6Cipher cipher = new RC6Cipher(KEY_128);

        byte[] ciphertext = cipher.encryptBlock(PLAINTEXT_BLOCK);

        assertFalse(java.util.Arrays.equals(PLAINTEXT_BLOCK, ciphertext));
    }

    @Test
    void differentKeysProduceDifferentCiphertext() {
        byte[] key2 =
                new byte[] {
                    (byte) 0xFF,
                    (byte) 0xEE,
                    (byte) 0xDD,
                    (byte) 0xCC,
                    (byte) 0xBB,
                    (byte) 0xAA,
                    (byte) 0x99,
                    (byte) 0x88,
                    0x77,
                    0x66,
                    0x55,
                    0x44,
                    0x33,
                    0x22,
                    0x11,
                    0x00
                };

        RC6Cipher cipher1 = new RC6Cipher(KEY_128);
        RC6Cipher cipher2 = new RC6Cipher(key2);

        byte[] ciphertext1 = cipher1.encryptBlock(PLAINTEXT_BLOCK);
        byte[] ciphertext2 = cipher2.encryptBlock(PLAINTEXT_BLOCK);

        assertFalse(java.util.Arrays.equals(ciphertext1, ciphertext2));
    }

    @Test
    void encryptDecryptWithKey192() {
        byte[] key192 = new byte[24];
        for (int i = 0; i < 24; i++) {
            key192[i] = (byte) (i + 1);
        }

        RC6Cipher cipher = new RC6Cipher(key192);

        byte[] ciphertext = cipher.encryptBlock(PLAINTEXT_BLOCK);
        byte[] decrypted = cipher.decryptBlock(ciphertext);

        assertArrayEquals(PLAINTEXT_BLOCK, decrypted);
    }

    @Test
    void encryptDecryptWithKey256() {
        byte[] key256 = new byte[32];
        for (int i = 0; i < 32; i++) {
            key256[i] = (byte) (i + 1);
        }

        RC6Cipher cipher = new RC6Cipher(key256);

        byte[] ciphertext = cipher.encryptBlock(PLAINTEXT_BLOCK);
        byte[] decrypted = cipher.decryptBlock(ciphertext);

        assertArrayEquals(PLAINTEXT_BLOCK, decrypted);
    }

    @Test
    void encryptDecryptZeroBlock() {
        RC6Cipher cipher = new RC6Cipher(KEY_128);
        byte[] zeroBlock = new byte[16];

        byte[] ciphertext = cipher.encryptBlock(zeroBlock);
        byte[] decrypted = cipher.decryptBlock(ciphertext);

        assertArrayEquals(zeroBlock, decrypted);
    }

    @Test
    void invalidBlockSizeThrowsException() {
        RC6Cipher cipher = new RC6Cipher(KEY_128);

        assertThrows(IllegalArgumentException.class, () -> cipher.encryptBlock(new byte[15]));
        assertThrows(IllegalArgumentException.class, () -> cipher.decryptBlock(new byte[17]));
        assertThrows(IllegalArgumentException.class, () -> cipher.encryptBlock(null));
    }

    @Test
    void invalidKeySizeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new RC6Cipher(new byte[10]));
        assertThrows(IllegalArgumentException.class, () -> new RC6Cipher(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> new RC6Cipher(null));
    }

    @Test
    void getBlockSizeReturns16() {
        RC6Cipher cipher = new RC6Cipher(KEY_128);
        assertEquals(16, cipher.getBlockSize());
    }

    @Test
    void getSubkeysReturnsDefensiveCopy() {
        RC6Cipher cipher = new RC6Cipher(KEY_128);
        int[] subkeys1 = cipher.getSubkeys();
        int[] subkeys2 = cipher.getSubkeys();

        assertArrayEquals(subkeys1, subkeys2);
        assertNotSame(subkeys1, subkeys2);
    }
}
