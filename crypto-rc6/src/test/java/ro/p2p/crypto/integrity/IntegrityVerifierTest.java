package ro.p2p.crypto.integrity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IntegrityVerifierTest {

    private static final byte[] KEY =
            new byte[] {
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
            };

    @Test
    void verifyValidMac() {
        SimpleMac mac = new SimpleMac(KEY);
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);

        byte[] data = "Hello integrity".getBytes();
        byte[] tag = mac.compute(data);

        assertTrue(verifier.verify(data, tag));
    }

    @Test
    void verifyInvalidMac() {
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);
        byte[] data = "Hello integrity".getBytes();
        byte[] fakeTag = new byte[16];

        assertFalse(verifier.verify(data, fakeTag));
    }

    @Test
    void verifyTamperedData() {
        SimpleMac mac = new SimpleMac(KEY);
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);

        byte[] data = "Original".getBytes();
        byte[] tag = mac.compute(data);

        byte[] tampered = "Tampered".getBytes();
        assertFalse(verifier.verify(tampered, tag));
    }

    @Test
    void verifyWithAppendedMacValid() {
        SimpleMac mac = new SimpleMac(KEY);
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);

        byte[] data = "Test data".getBytes();
        byte[] dataWithMac = mac.appendMac(data);

        assertTrue(verifier.verifyWithAppendedMac(dataWithMac));
    }

    @Test
    void verifyWithAppendedMacTampered() {
        SimpleMac mac = new SimpleMac(KEY);
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);

        byte[] data = "Test data".getBytes();
        byte[] dataWithMac = mac.appendMac(data);

        dataWithMac[0] = (byte) (dataWithMac[0] ^ 0xFF);

        assertFalse(verifier.verifyWithAppendedMac(dataWithMac));
    }

    @Test
    void extractDataValid() {
        SimpleMac mac = new SimpleMac(KEY);
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);

        byte[] data = "Extract me".getBytes();
        byte[] dataWithMac = mac.appendMac(data);

        byte[] extracted = verifier.extractData(dataWithMac);
        assertArrayEquals(data, extracted);
    }

    @Test
    void extractDataTamperedThrowsSecurityException() {
        SimpleMac mac = new SimpleMac(KEY);
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);

        byte[] dataWithMac = mac.appendMac("test".getBytes());
        dataWithMac[0] ^= 0xFF;

        assertThrows(SecurityException.class, () -> verifier.extractData(dataWithMac));
    }

    @Test
    void verifyWithAppendedMacTooShort() {
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);

        assertFalse(verifier.verifyWithAppendedMac(new byte[10]));
        assertFalse(verifier.verifyWithAppendedMac(null));
    }

    @Test
    void verifyNullArgThrows() {
        IntegrityVerifier verifier = new IntegrityVerifier(KEY);

        assertThrows(IllegalArgumentException.class, () -> verifier.verify(null, new byte[16]));
        assertThrows(
                IllegalArgumentException.class, () -> verifier.verify("data".getBytes(), null));
    }
}
