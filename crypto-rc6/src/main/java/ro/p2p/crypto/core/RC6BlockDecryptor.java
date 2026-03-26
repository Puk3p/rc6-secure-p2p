package ro.p2p.crypto.core;

import ro.p2p.crypto.util.Word32;

public class RC6BlockDecryptor {

    private static final int ROUNDS = RC6KeySchedule.ROUNDS;
    private static final int BLOCK_SIZE = RC6KeySchedule.BLOCK_SIZE;
    private static final int LOG_W = 5;

    public byte[] decryptBlock(byte[] ciphertext, int[] subkeys) {
        if (ciphertext == null || ciphertext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Ciphertext block must be " + BLOCK_SIZE + " bytes");
        }
        if (subkeys == null || subkeys.length != RC6KeySchedule.SUBKEY_COUNT) {
            throw new IllegalArgumentException(
                    "Subkeys array must have " + RC6KeySchedule.SUBKEY_COUNT + " elements");
        }

        int A = Word32.bytesToIntLE(ciphertext, 0);
        int B = Word32.bytesToIntLE(ciphertext, 4);
        int C = Word32.bytesToIntLE(ciphertext, 8);
        int D = Word32.bytesToIntLE(ciphertext, 12);

        C = Word32.sub(C, subkeys[2 * ROUNDS + 3]);
        A = Word32.sub(A, subkeys[2 * ROUNDS + 2]);

        for (int i = ROUNDS; i >= 1; i--) {
            int temp = D;
            D = C;
            C = B;
            B = A;
            A = temp;

            int u = Word32.rotateLeft(D * (2 * D + 1), LOG_W);
            int t = Word32.rotateLeft(B * (2 * B + 1), LOG_W);
            C = Word32.rotateRight(Word32.sub(C, subkeys[2 * i + 1]), t) ^ u;
            A = Word32.rotateRight(Word32.sub(A, subkeys[2 * i]), u) ^ t;
        }

        D = Word32.sub(D, subkeys[1]);
        B = Word32.sub(B, subkeys[0]);

        byte[] plaintext = new byte[BLOCK_SIZE];
        Word32.intToBytesLE(A, plaintext, 0);
        Word32.intToBytesLE(B, plaintext, 4);
        Word32.intToBytesLE(C, plaintext, 8);
        Word32.intToBytesLE(D, plaintext, 12);

        return plaintext;
    }
}
