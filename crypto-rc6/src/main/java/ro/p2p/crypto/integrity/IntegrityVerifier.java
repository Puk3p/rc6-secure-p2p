package ro.p2p.crypto.integrity;

public class IntegrityVerifier {

    private final SimpleMac mac;

    public IntegrityVerifier(byte[] key) {
        this.mac = new SimpleMac(key);
    }

    public boolean verify(byte[] data, byte[] expectedMac) {
        if (data == null || expectedMac == null) {
            throw new IllegalArgumentException("Data and expected MAC must not be null");
        }
        if (expectedMac.length != SimpleMac.MAC_SIZE) {
            return false;
        }

        byte[] computedMac = mac.compute(data);
        return constantTimeEquals(computedMac, expectedMac);
    }

    public boolean verifyWithAppendedMac(byte[] dataWithMac) {
        if (dataWithMac == null || dataWithMac.length < SimpleMac.MAC_SIZE) {
            return false;
        }

        int dataLength = dataWithMac.length - SimpleMac.MAC_SIZE;
        byte[] data = new byte[dataLength];
        byte[] receivedMac = new byte[SimpleMac.MAC_SIZE];

        System.arraycopy(dataWithMac, 0, data, 0, dataLength);
        System.arraycopy(dataWithMac, dataLength, receivedMac, 0, SimpleMac.MAC_SIZE);

        return verify(data, receivedMac);
    }

    public byte[] extractData(byte[] dataWithMac) {
        if (dataWithMac == null || dataWithMac.length < SimpleMac.MAC_SIZE) {
            throw new IllegalArgumentException("Invalid data with MAC");
        }

        if (!verifyWithAppendedMac(dataWithMac)) {
            throw new SecurityException("MAC verification failed: data may be corrupted");
        }

        int dataLength = dataWithMac.length - SimpleMac.MAC_SIZE;
        byte[] data = new byte[dataLength];
        System.arraycopy(dataWithMac, 0, data, 0, dataLength);

        return data;
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
