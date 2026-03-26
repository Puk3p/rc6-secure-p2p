package ro.p2p.crypto.util;

public final class Pkcs7Padding {

    private static final int BLOCK_SIZE = 16;

    private Pkcs7Padding() {}

    public static byte[] addPadding(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data must not be null");
        }

        int paddingLength = BLOCK_SIZE - (data.length % BLOCK_SIZE);
        byte[] padded = new byte[data.length + paddingLength];

        System.arraycopy(data, 0, padded, 0, data.length);

        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) paddingLength;
        }

        return padded;
    }

    public static byte[] removePadding(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data must not be null or empty");
        }

        if (data.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Data length must be a multiple of " + BLOCK_SIZE);
        }

        int paddingLength = data[data.length - 1] & 0xFF;

        if (paddingLength < 1 || paddingLength > BLOCK_SIZE) {
            throw new IllegalArgumentException("Invalid padding value: " + paddingLength);
        }

        for (int i = data.length - paddingLength; i < data.length; i++) {
            if ((data[i] & 0xFF) != paddingLength) {
                throw new IllegalArgumentException("Corrupted PKCS#7 padding");
            }
        }

        byte[] unpadded = new byte[data.length - paddingLength];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);

        return unpadded;
    }
}
