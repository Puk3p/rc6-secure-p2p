package ro.p2p.crypto.integrity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimpleMacTest {

    private static final byte[] KEY =
            new byte[] {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
            };

    @Test
    void computeReturnsSixteenBytes() {
        SimpleMac mac = new SimpleMac(KEY);
        byte[] tag = mac.compute("Hello".getBytes());

        assertEquals(16, tag.length);
    }

    @Test
    void sameDataProducesSameMac() {
        SimpleMac mac = new SimpleMac(KEY);
        byte[] tag1 = mac.compute("Test data".getBytes());
        byte[] tag2 = mac.compute("Test data".getBytes());

        assertArrayEquals(tag1, tag2);
    }

    @Test
    void differentDataProducesDifferentMac() {
        SimpleMac mac = new SimpleMac(KEY);
        byte[] tag1 = mac.compute("Message A".getBytes());
        byte[] tag2 = mac.compute("Message B".getBytes());

        assertFalse(java.util.Arrays.equals(tag1, tag2));
    }

    @Test
    void differentKeyProducesDifferentMac() {
        byte[] key2 = new byte[16];
        key2[0] = (byte) 0xFF;

        SimpleMac mac1 = new SimpleMac(KEY);
        SimpleMac mac2 = new SimpleMac(key2);

        byte[] data = "Same data".getBytes();

        assertFalse(java.util.Arrays.equals(mac1.compute(data), mac2.compute(data)));
    }

    @Test
    void appendMacAdds16Bytes() {
        SimpleMac mac = new SimpleMac(KEY);
        byte[] data = "Test".getBytes();
        byte[] result = mac.appendMac(data);

        assertEquals(data.length + 16, result.length);
    }

    @Test
    void appendMacPreservesData() {
        SimpleMac mac = new SimpleMac(KEY);
        byte[] data = "Preserved".getBytes();
        byte[] result = mac.appendMac(data);

        byte[] extractedData = new byte[data.length];
        System.arraycopy(result, 0, extractedData, 0, data.length);

        assertArrayEquals(data, extractedData);
    }

    @Test
    void computeEmptyData() {
        SimpleMac mac = new SimpleMac(KEY);
        byte[] tag = mac.compute(new byte[0]);

        assertEquals(16, tag.length);
    }

    @Test
    void nullDataThrows() {
        SimpleMac mac = new SimpleMac(KEY);

        assertThrows(IllegalArgumentException.class, () -> mac.compute(null));
        assertThrows(IllegalArgumentException.class, () -> mac.appendMac(null));
    }
}
