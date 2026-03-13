package ro.p2p.crypto.util;

public final class Word32 {

    private Word32() {
    }

    public static int rotateLeft(int value, int shift) {
        return Integer.rotateLeft(value, shift & 31);
    }

    public static int rotateRight(int value, int shift) {
        return Integer.rotateRight(value, shift & 31);
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int sub(int a, int b) {
        return a - b;
    }

    public static int bytesToIntLE(byte[] input, int offset) {
        return (input[offset] & 0xFF)
                | ((input[offset + 1] & 0xFF) << 8)
                | ((input[offset + 2] & 0xFF) << 16)
                | ((input[offset + 3] & 0xFF) << 24);
    }

    public static void intToBytesLE(int value, byte[] output, int offset) {
        output[offset] = (byte) (value);
        output[offset + 1] = (byte) (value >>> 8);
        output[offset + 2] = (byte) (value >>> 16);
        output[offset + 3] = (byte) (value >>> 24);
    }
}
