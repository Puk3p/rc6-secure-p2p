package ro.p2p.crypto.integrity;

import ro.p2p.crypto.core.RC6Cipher;
import ro.p2p.crypto.core.RC6KeySchedule;

public class SimpleMac {

    private static final int BLOCK_SIZE = RC6KeySchedule.BLOCK_SIZE;
    public static final int MAC_SIZE = BLOCK_SIZE;

    private final RC6Cipher cipher;

    public SimpleMac(byte[] key) {
        this.cipher = new RC6Cipher(key);
    }

    public byte[] compute(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data must not be null");
        }

        byte[] mac = new byte[BLOCK_SIZE];

        int fullBlocks = data.length / BLOCK_SIZE;
        int remaining = data.length % BLOCK_SIZE;

        for (int i = 0; i < fullBlocks; i++) {
            int offset = i * BLOCK_SIZE;
            for (int j = 0; j < BLOCK_SIZE; j++) {
                mac[j] ^= data[offset + j];
            }
            mac = cipher.encryptBlock(mac);
        }

        if (remaining > 0) {
            byte[] lastBlock = new byte[BLOCK_SIZE];
            System.arraycopy(data, fullBlocks * BLOCK_SIZE, lastBlock, 0, remaining);
            lastBlock[remaining] = (byte) 0x80;

            for (int j = 0; j < BLOCK_SIZE; j++) {
                mac[j] ^= lastBlock[j];
            }
            mac = cipher.encryptBlock(mac);
        }

        return mac;
    }

    public byte[] appendMac(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data must not be null");
        }

        byte[] mac = compute(data);
        byte[] result = new byte[data.length + MAC_SIZE];
        System.arraycopy(data, 0, result, 0, data.length);
        System.arraycopy(mac, 0, result, data.length, MAC_SIZE);

        return result;
    }
}
