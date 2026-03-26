package ro.p2p.crypto.mode;

import ro.p2p.crypto.core.RC6Cipher;
import ro.p2p.crypto.core.RC6KeySchedule;
import ro.p2p.crypto.util.Pkcs7Padding;

public class RC6CbcMode {

    private static final int BLOCK_SIZE = RC6KeySchedule.BLOCK_SIZE;

    private final RC6Cipher cipher;

    public RC6CbcMode(byte[] key) {
        this.cipher = new RC6Cipher(key);
    }

    public byte[] encrypt(byte[] plaintext, byte[] iv) {
        if (plaintext == null) {
            throw new IllegalArgumentException("Plaintext must not be null");
        }
        validateIv(iv);

        byte[] padded = Pkcs7Padding.addPadding(plaintext);
        byte[] ciphertext = new byte[padded.length];

        byte[] previousBlock = iv.clone();

        for (int offset = 0; offset < padded.length; offset += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, offset, block, 0, BLOCK_SIZE);

            for (int i = 0; i < BLOCK_SIZE; i++) {
                block[i] ^= previousBlock[i];
            }

            byte[] encrypted = cipher.encryptBlock(block);
            System.arraycopy(encrypted, 0, ciphertext, offset, BLOCK_SIZE);

            previousBlock = encrypted;
        }

        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext, byte[] iv) {
        if (ciphertext == null || ciphertext.length == 0) {
            throw new IllegalArgumentException("Ciphertext must not be null or empty");
        }
        if (ciphertext.length % BLOCK_SIZE != 0) {
            throw new IllegalArgumentException(
                    "Ciphertext length must be a multiple of " + BLOCK_SIZE);
        }
        validateIv(iv);

        byte[] padded = new byte[ciphertext.length];
        byte[] previousBlock = iv.clone();

        for (int offset = 0; offset < ciphertext.length; offset += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertext, offset, block, 0, BLOCK_SIZE);

            byte[] decrypted = cipher.decryptBlock(block);

            for (int i = 0; i < BLOCK_SIZE; i++) {
                decrypted[i] ^= previousBlock[i];
            }

            System.arraycopy(decrypted, 0, padded, offset, BLOCK_SIZE);

            previousBlock = block;
        }

        return Pkcs7Padding.removePadding(padded);
    }

    private void validateIv(byte[] iv) {
        if (iv == null || iv.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("IV must be " + BLOCK_SIZE + " bytes");
        }
    }
}
