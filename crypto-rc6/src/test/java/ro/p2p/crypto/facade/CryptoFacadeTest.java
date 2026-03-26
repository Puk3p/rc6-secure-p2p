package ro.p2p.crypto.facade;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CryptoFacadeTest {

    private static final byte[] KEY =
            new byte[] {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
            };

    @Test
    void cbcEncryptDecryptRoundTrip() {
        CryptoFacade facade = new CryptoFacade(KEY);
        byte[] plaintext = "Hello CryptoFacade CBC!".getBytes();

        byte[] encrypted = facade.encryptCbc(plaintext);
        byte[] decrypted = facade.decryptCbc(encrypted);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void ctrEncryptDecryptRoundTrip() {
        CryptoFacade facade = new CryptoFacade(KEY);
        byte[] plaintext = "Hello CryptoFacade CTR!".getBytes();

        byte[] encrypted = facade.encryptCtr(plaintext);
        byte[] decrypted = facade.decryptCtr(encrypted);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void encryptWithMacDecryptRoundTrip() {
        CryptoFacade facade = new CryptoFacade(KEY);
        byte[] plaintext = "Authenticated message".getBytes();

        byte[] encrypted = facade.encryptWithMac(plaintext);
        byte[] decrypted = facade.decryptWithMac(encrypted);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void encryptWithMacTamperedThrows() {
        CryptoFacade facade = new CryptoFacade(KEY);
        byte[] encrypted = facade.encryptWithMac("secret".getBytes());

        encrypted[0] ^= 0xFF;

        assertThrows(SecurityException.class, () -> facade.decryptWithMac(encrypted));
    }

    @Test
    void computeAndVerifyMac() {
        CryptoFacade facade = new CryptoFacade(KEY);
        byte[] data = "Data to verify".getBytes();

        byte[] mac = facade.computeMac(data);
        assertTrue(facade.verifyMac(data, mac));
    }

    @Test
    void verifyMacWithWrongDataFails() {
        CryptoFacade facade = new CryptoFacade(KEY);

        byte[] mac = facade.computeMac("original".getBytes());
        assertFalse(facade.verifyMac("modified".getBytes(), mac));
    }

    @Test
    void cbcEncryptionIncludesIv() {
        CryptoFacade facade = new CryptoFacade(KEY);
        byte[] plaintext = "Test".getBytes();

        byte[] encrypted = facade.encryptCbc(plaintext);

        assertTrue(encrypted.length >= 32);
    }

    @Test
    void ctrEncryptionIncludesNonce() {
        CryptoFacade facade = new CryptoFacade(KEY);
        byte[] plaintext = "Test".getBytes();

        byte[] encrypted = facade.encryptCtr(plaintext);

        assertEquals(16 + plaintext.length, encrypted.length);
    }

    @Test
    void cbcDecryptTooShortThrows() {
        CryptoFacade facade = new CryptoFacade(KEY);

        assertThrows(IllegalArgumentException.class, () -> facade.decryptCbc(new byte[16]));
        assertThrows(IllegalArgumentException.class, () -> facade.decryptCbc(null));
    }

    @Test
    void ctrDecryptTooShortThrows() {
        CryptoFacade facade = new CryptoFacade(KEY);

        assertThrows(IllegalArgumentException.class, () -> facade.decryptCtr(new byte[10]));
        assertThrows(IllegalArgumentException.class, () -> facade.decryptCtr(null));
    }

    @Test
    void multipleEncryptionsProduceDifferentCiphertext() {
        CryptoFacade facade = new CryptoFacade(KEY);
        byte[] plaintext = "Same message".getBytes();

        byte[] enc1 = facade.encryptCbc(plaintext);
        byte[] enc2 = facade.encryptCbc(plaintext);

        assertFalse(java.util.Arrays.equals(enc1, enc2));
    }
}
