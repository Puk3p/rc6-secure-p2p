package ro.p2p.crypto.core;

import ro.p2p.crypto.util.Word32;

public class RC6KeySchedule {

    public static final int WORD_SIZE = 32;
    public static final int ROUNDS = 20;
    public static final int BLOCK_SIZE = 16;
    public static final int SUBKEY_COUNT = 2 * ROUNDS + 4;

    private static final int P32 = 0xB7E15163;
    private static final int Q32 = 0x9E3779B9;

    public int[] generateSubkeys(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }

        if (!(key.length == 16 || key.length == 24 || key.length == 32)) {
            throw new IllegalArgumentException("RC6 key must have 16, 24 or 32 bytes");
        }

        int c = Math.max(1, (key.length + 3) / 4);
        int[] L = new int[c];

        for (int i = key.length - 1; i >= 0; i--) {
            L[i / 4] = (L[i / 4] << 8) | (key[i] & 0xFF);
        }

        int[] S = new int[SUBKEY_COUNT];
        S[0] = P32;
        for (int i = 1; i < SUBKEY_COUNT; i++) {
            S[i] = S[i - 1] + Q32;
        }

        int A = 0;
        int B = 0;
        int i = 0;
        int j = 0;
        int v = 3 * Math.max(c, SUBKEY_COUNT);

        for (int s = 0; s < v; s++) {
            A = S[i] = Word32.rotateLeft(S[i] + A + B, 3);
            B = L[j] = Word32.rotateLeft(L[j] + A + B, A + B);
            i = (i + 1) % SUBKEY_COUNT;
            j = (j + 1) % c;
        }

        return S;
    }
}