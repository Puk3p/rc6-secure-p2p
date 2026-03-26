package ro.p2p.crypto.key;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SessionKeyDeriverTest {

    private static final byte[] MASTER_KEY =
            new byte[] {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
            };

    @Test
    void generateSessionKeyDefault16Bytes() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();
        byte[] key = deriver.generateSessionKey();

        assertEquals(16, key.length);
    }

    @Test
    void generateSessionKey24Bytes() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();
        byte[] key = deriver.generateSessionKey(24);

        assertEquals(24, key.length);
    }

    @Test
    void generateSessionKey32Bytes() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();
        byte[] key = deriver.generateSessionKey(32);

        assertEquals(32, key.length);
    }

    @Test
    void invalidKeySizeThrows() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();

        assertThrows(IllegalArgumentException.class, () -> deriver.generateSessionKey(10));
        assertThrows(IllegalArgumentException.class, () -> deriver.generateSessionKey(0));
    }

    @Test
    void twoSessionKeysAreDifferent() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();
        byte[] key1 = deriver.generateSessionKey();
        byte[] key2 = deriver.generateSessionKey();

        assertFalse(java.util.Arrays.equals(key1, key2));
    }

    @Test
    void deriveKeyReturns16Bytes() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();
        byte[] derived = deriver.deriveKey(MASTER_KEY, "session-1".getBytes());

        assertEquals(16, derived.length);
    }

    @Test
    void deriveKeyDeterministic() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();
        byte[] d1 = deriver.deriveKey(MASTER_KEY, "ctx".getBytes());
        byte[] d2 = deriver.deriveKey(MASTER_KEY, "ctx".getBytes());

        assertArrayEquals(d1, d2);
    }

    @Test
    void deriveKeyDifferentContextProducesDifferentKey() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();
        byte[] d1 = deriver.deriveKey(MASTER_KEY, "context-A".getBytes());
        byte[] d2 = deriver.deriveKey(MASTER_KEY, "context-B".getBytes());

        assertFalse(java.util.Arrays.equals(d1, d2));
    }

    @Test
    void deriveKeyNullThrows() {
        SessionKeyDeriver deriver = new SessionKeyDeriver();

        assertThrows(
                IllegalArgumentException.class, () -> deriver.deriveKey(null, "ctx".getBytes()));
        assertThrows(IllegalArgumentException.class, () -> deriver.deriveKey(MASTER_KEY, null));
    }
}
