package ro.p2p.crypto.key;

import java.security.SecureRandom;
import ro.p2p.crypto.core.RC6Cipher;
import ro.p2p.crypto.core.RC6KeySchedule;

public class SessionKeyDeriver {

    private static final int DEFAULT_KEY_SIZE = 16;
    private final SecureRandom secureRandom;

    public SessionKeyDeriver() {
        this.secureRandom = new SecureRandom();
    }

    public byte[] generateSessionKey() {
        return generateSessionKey(DEFAULT_KEY_SIZE);
    }

    public byte[] generateSessionKey(int keySize) {
        if (!(keySize == 16 || keySize == 24 || keySize == 32)) {
            throw new IllegalArgumentException("Key size must be 16, 24 or 32 bytes");
        }

        byte[] key = new byte[keySize];
        secureRandom.nextBytes(key);
        return key;
    }

    public byte[] deriveKey(byte[] masterKey, byte[] context) {
        if (masterKey == null || context == null) {
            throw new IllegalArgumentException("Master key and context must not be null");
        }

        RC6Cipher cipher = new RC6Cipher(masterKey);

        byte[] block = new byte[RC6KeySchedule.BLOCK_SIZE];
        int copyLen = Math.min(context.length, RC6KeySchedule.BLOCK_SIZE);
        System.arraycopy(context, 0, block, 0, copyLen);

        byte[] derived = new byte[DEFAULT_KEY_SIZE];
        byte[] encrypted = cipher.encryptBlock(block);
        System.arraycopy(encrypted, 0, derived, 0, DEFAULT_KEY_SIZE);

        return derived;
    }
}
