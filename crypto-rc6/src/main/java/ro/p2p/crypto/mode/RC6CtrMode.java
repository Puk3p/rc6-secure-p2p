package ro.p2p.crypto.mode;

import ro.p2p.crypto.core.RC6Cipher;
import ro.p2p.crypto.core.RC6KeySchedule;

public class RC6CtrMode {

    private static final int BLOCK_SIZE = RC6KeySchedule.BLOCK_SIZE;

    private final RC6Cipher cipher;

    public RC6CtrMode(byte[] key) {
        this.cipher = new RC6Cipher(key);
    }

    public byte[] encrypt(byte[] plaintext, byte[] nonce) {
        return process(plaintext, nonce);
    }

    public byte[] decrypt(byte[] ciphertext, byte[] nonce) {
        return process(ciphertext, nonce);
    }

    private byte[] process(byte[] input, byte[] nonce) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        if (nonce == null || nonce.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Nonce must be " + BLOCK_SIZE + " bytes");
        }

        byte[] output = new byte[input.length];

        int fullBlocks = input.length / BLOCK_SIZE;
        int remaining = input.length % BLOCK_SIZE;

        for (int counter = 0; counter < fullBlocks; counter++) {
            byte[] counterBlock = buildCounterBlock(nonce, counter);
            byte[] keystream = cipher.encryptBlock(counterBlock);

            int offset = counter * BLOCK_SIZE;
            for (int i = 0; i < BLOCK_SIZE; i++) {
                output[offset + i] = (byte) (input[offset + i] ^ keystream[i]);
            }
        }

        if (remaining > 0) {
            byte[] counterBlock = buildCounterBlock(nonce, fullBlocks);
            byte[] keystream = cipher.encryptBlock(counterBlock);

            int offset = fullBlocks * BLOCK_SIZE;
            for (int i = 0; i < remaining; i++) {
                output[offset + i] = (byte) (input[offset + i] ^ keystream[i]);
            }
        }

        return output;
    }

    private byte[] buildCounterBlock(byte[] nonce, int counter) {
        byte[] block = new byte[BLOCK_SIZE];
        System.arraycopy(nonce, 0, block, 0, BLOCK_SIZE / 2);

        block[BLOCK_SIZE - 4] = (byte) (counter >>> 24);
        block[BLOCK_SIZE - 3] = (byte) (counter >>> 16);
        block[BLOCK_SIZE - 2] = (byte) (counter >>> 8);
        block[BLOCK_SIZE - 1] = (byte) (counter);

        return block;
    }
}
