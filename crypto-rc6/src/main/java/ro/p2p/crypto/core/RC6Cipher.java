package ro.p2p.crypto.core;

public class RC6Cipher {

    private final int[] subkeys;
    private final RC6BlockEncryptor encryptor;
    private final RC6BlockDecryptor decryptor;

    public RC6Cipher(byte[] key) {
        RC6KeySchedule keySchedule = new RC6KeySchedule();
        this.subkeys = keySchedule.generateSubkeys(key);
        this.encryptor = new RC6BlockEncryptor();
        this.decryptor = new RC6BlockDecryptor();
    }

    public byte[] encryptBlock(byte[] plaintext) {
        return encryptor.encryptBlock(plaintext, subkeys);
    }

    public byte[] decryptBlock(byte[] ciphertext) {
        return decryptor.decryptBlock(ciphertext, subkeys);
    }

    public int getBlockSize() {
        return RC6KeySchedule.BLOCK_SIZE;
    }

    public int[] getSubkeys() {
        return subkeys.clone();
    }
}
