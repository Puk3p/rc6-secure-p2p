package ro.p2p.crypto.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class RC6CipherTest {

    private static final byte[] KEY_128 =
        hex("01 23 45 67 89 AB CD EF 01 23 45 67 89 AB CD EF");

    private static final byte[] PLAINTEXT_BLOCK =
        hex("02 13 24 35 46 57 68 79 8A 9B AC BD CE DF E0 F1");

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

        assertFalse(Arrays.equals(PLAINTEXT_BLOCK, ciphertext));
    }

    @Test
    void differentKeysProduceDifferentCiphertext() {
        byte[] key2 = hex("FF EE DD CC BB AA 99 88 77 66 55 44 33 22 11 00");

        RC6Cipher cipher1 = new RC6Cipher(KEY_128);
        RC6Cipher cipher2 = new RC6Cipher(key2);

        byte[] ciphertext1 = cipher1.encryptBlock(PLAINTEXT_BLOCK);
        byte[] ciphertext2 = cipher2.encryptBlock(PLAINTEXT_BLOCK);

        assertFalse(Arrays.equals(ciphertext1, ciphertext2));
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
    void officialVector128AllZeroKeyAndPlaintextMatchesExpectedCiphertext() {
        assertOfficialVector(
            hex("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
            hex("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
            hex("8F C3 A5 36 56 B1 F7 78 C1 29 DF 4E 98 48 A4 1E"));
    }

    @Test
    void officialVector128CustomKeyAndPlaintextMatchesExpectedCiphertext() {
        assertOfficialVector(
            hex("01 23 45 67 89 AB CD EF 01 12 23 34 45 56 67 78"),
            hex("02 13 24 35 46 57 68 79 8A 9B AC BD CE DF E0 F1"),
            hex("52 4E 19 2F 47 15 C6 23 1F 51 F6 36 7E A4 3F 18"));
    }

    @Test
    void officialVector192AllZeroKeyAndPlaintextMatchesExpectedCiphertext() {
        assertOfficialVector(
            hex("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "
                + "00 00 00 00 00 00 00 00"),
            hex("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
            hex("6C D6 1B CB 19 0B 30 38 4E 8A 3F 16 86 90 AE 82"));
    }

    @Test
    void officialVector192CustomKeyAndPlaintextMatchesExpectedCiphertext() {
        assertOfficialVector(
            hex("01 23 45 67 89 AB CD EF 01 12 23 34 45 56 67 78 "
                + "89 9A AB BC CD DE EF F0"),
            hex("02 13 24 35 46 57 68 79 8A 9B AC BD CE DF E0 F1"),
            hex("68 83 29 D0 19 E5 05 04 1E 52 E9 2A F9 52 91 D4"));
    }

    @Test
    void officialVector256AllZeroKeyAndPlaintextMatchesExpectedCiphertext() {
        assertOfficialVector(
            hex("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 "
                + "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
            hex("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"),
            hex("8F 5F BD 05 10 D1 5F A8 93 FA 3F DA 6E 85 7E C2"));
    }

    @Test
    void officialVector256CustomKeyAndPlaintextMatchesExpectedCiphertext() {
        assertOfficialVector(
            hex("01 23 45 67 89 AB CD EF 01 12 23 34 45 56 67 78 "
                + "89 9A AB BC CD DE EF F0 10 32 54 76 98 BA DC FE"),
            hex("02 13 24 35 46 57 68 79 8A 9B AC BD CE DF E0 F1"),
            hex("C8 24 18 16 F0 D7 E4 89 20 AD 16 A1 67 4E 5D 48"));
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

    private static void assertOfficialVector(byte[] key, byte[] plaintext, byte[] expectedCiphertext) {
        RC6Cipher cipher = new RC6Cipher(key);

        byte[] ciphertext = cipher.encryptBlock(plaintext);
        byte[] decrypted = cipher.decryptBlock(expectedCiphertext);

        assertArrayEquals(expectedCiphertext, ciphertext);
        assertArrayEquals(plaintext, decrypted);
    }

    private static byte[] hex(String value) {
        String normalized = value.replaceAll("\\s+", "");
        if ((normalized.length() & 1) != 0) {
            throw new IllegalArgumentException("Hex string must contain an even number of characters");
        }

        byte[] bytes = new byte[normalized.length() / 2];
        for (int i = 0; i < normalized.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(normalized.substring(i, i + 2), 16);
        }
        return bytes;
    }
}
