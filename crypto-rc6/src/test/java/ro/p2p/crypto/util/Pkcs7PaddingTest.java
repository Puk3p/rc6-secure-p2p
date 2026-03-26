package ro.p2p.crypto.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Pkcs7PaddingTest {

    @Test
    void addPaddingToShortData() {
        byte[] data = new byte[] {0x01, 0x02, 0x03};
        byte[] padded = Pkcs7Padding.addPadding(data);

        assertEquals(16, padded.length);
        for (int i = 3; i < 16; i++) {
            assertEquals(13, padded[i] & 0xFF);
        }
    }

    @Test
    void addPaddingToExactBlockSize() {
        byte[] data = new byte[16];
        byte[] padded = Pkcs7Padding.addPadding(data);

        assertEquals(32, padded.length);
        for (int i = 16; i < 32; i++) {
            assertEquals(16, padded[i] & 0xFF);
        }
    }

    @Test
    void addPaddingToEmptyData() {
        byte[] padded = Pkcs7Padding.addPadding(new byte[0]);

        assertEquals(16, padded.length);
        for (int i = 0; i < 16; i++) {
            assertEquals(16, padded[i] & 0xFF);
        }
    }

    @Test
    void roundTripVariousSizes() {
        for (int size = 0; size <= 48; size++) {
            byte[] original = new byte[size];
            for (int i = 0; i < size; i++) {
                original[i] = (byte) (i & 0xFF);
            }

            byte[] padded = Pkcs7Padding.addPadding(original);
            assertEquals(0, padded.length % 16);

            byte[] unpadded = Pkcs7Padding.removePadding(padded);
            assertArrayEquals(original, unpadded);
        }
    }

    @Test
    void removePaddingInvalidLengthThrows() {
        assertThrows(
                IllegalArgumentException.class, () -> Pkcs7Padding.removePadding(new byte[15]));
    }

    @Test
    void removePaddingCorruptedThrows() {
        byte[] corrupted = new byte[16];
        corrupted[15] = 3;
        corrupted[14] = 3;
        corrupted[13] = 99;

        assertThrows(IllegalArgumentException.class, () -> Pkcs7Padding.removePadding(corrupted));
    }

    @Test
    void addPaddingNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> Pkcs7Padding.addPadding(null));
    }

    @Test
    void removePaddingNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> Pkcs7Padding.removePadding(null));
    }
}
