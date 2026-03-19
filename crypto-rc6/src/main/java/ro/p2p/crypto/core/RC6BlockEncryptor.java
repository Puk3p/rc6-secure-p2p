package ro.p2p.crypto.core;

import ro.p2p.crypto.util.Word32;

public class RC6BlockEncryptor {

    private static final int ROUNDS = RC6KeySchedule.ROUNDS;
    private static final int BLOCK_SIZE = RC6KeySchedule.BLOCK_SIZE;
    private static final int LOG_W = 5;

    public byte[] encryptBlock(byte[] plaintext, int[] subkeys) {
        if (plaintext == null || plaintext.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Plaintext block must be " + BLOCK_SIZE + " bytes");
        }
        if (subkeys == null || subkeys.length != RC6KeySchedule.SUBKEY_COUNT) {
            throw new IllegalArgumentException(
                    "Subkeys array must have " + RC6KeySchedule.SUBKEY_COUNT + " elements");
        }

        int A = Word32.bytesToIntLE(plaintext, 0);
        int B = Word32.bytesToIntLE(plaintext, 4);
        int C = Word32.bytesToIntLE(plaintext, 8);
        int D = Word32.bytesToIntLE(plaintext, 12);

        B = Word32.add(B, subkeys[0]);
        D = Word32.add(D, subkeys[1]);

        for (int i = 1; i <= ROUNDS; i++) {
            int t = Word32.rotateLeft(B * (2 * B + 1), LOG_W);
            int u = Word32.rotateLeft(D * (2 * D + 1), LOG_W);
            A = Word32.add(Word32.rotateLeft(A ^ t, u), subkeys[2 * i]);
            C = Word32.add(Word32.rotateLeft(C ^ u, t), subkeys[2 * i + 1]);

            int temp = A;
            A = B;
            B = C;
            C = D;
            D = temp;
        }

        A = Word32.add(A, subkeys[2 * ROUNDS + 2]);
        C = Word32.add(C, subkeys[2 * ROUNDS + 3]);

        byte[] ciphertext = new byte[BLOCK_SIZE];
        Word32.intToBytesLE(A, ciphertext, 0);
        Word32.intToBytesLE(B, ciphertext, 4);
        Word32.intToBytesLE(C, ciphertext, 8);
        Word32.intToBytesLE(D, ciphertext, 12);

        return ciphertext;
    }
}
