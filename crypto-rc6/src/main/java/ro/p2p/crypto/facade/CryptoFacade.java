package ro.p2p.crypto.facade;

import ro.p2p.crypto.integrity.IntegrityVerifier;
import ro.p2p.crypto.integrity.SimpleMac;
import ro.p2p.crypto.key.NonceGenerator;
import ro.p2p.crypto.mode.RC6CbcMode;
import ro.p2p.crypto.mode.RC6CtrMode;

public class CryptoFacade {

    private final RC6CbcMode cbcMode;
    private final RC6CtrMode ctrMode;
    private final SimpleMac mac;
    private final IntegrityVerifier verifier;
    private final NonceGenerator nonceGenerator;

    public CryptoFacade(byte[] key) {
        this.cbcMode = new RC6CbcMode(key);
        this.ctrMode = new RC6CtrMode(key);
        this.mac = new SimpleMac(key);
        this.verifier = new IntegrityVerifier(key);
        this.nonceGenerator = new NonceGenerator();
    }

    public byte[] encryptCbc(byte[] plaintext) {
        byte[] iv = nonceGenerator.generateIv();
        byte[] ciphertext = cbcMode.encrypt(plaintext, iv);

        byte[] result = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);

        return result;
    }

    public byte[] decryptCbc(byte[] ivAndCiphertext) {
        if (ivAndCiphertext == null || ivAndCiphertext.length < 32) {
            throw new IllegalArgumentException("Invalid ciphertext: too short");
        }

        byte[] iv = new byte[16];
        byte[] ciphertext = new byte[ivAndCiphertext.length - 16];
        System.arraycopy(ivAndCiphertext, 0, iv, 0, 16);
        System.arraycopy(ivAndCiphertext, 16, ciphertext, 0, ciphertext.length);

        return cbcMode.decrypt(ciphertext, iv);
    }

    public byte[] encryptCtr(byte[] plaintext) {
        byte[] nonce = nonceGenerator.generateNonce();
        byte[] ciphertext = ctrMode.encrypt(plaintext, nonce);

        byte[] result = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, result, 0, nonce.length);
        System.arraycopy(ciphertext, 0, result, nonce.length, ciphertext.length);

        return result;
    }

    public byte[] decryptCtr(byte[] nonceAndCiphertext) {
        if (nonceAndCiphertext == null || nonceAndCiphertext.length < 17) {
            throw new IllegalArgumentException("Invalid ciphertext: too short");
        }

        byte[] nonce = new byte[16];
        byte[] ciphertext = new byte[nonceAndCiphertext.length - 16];
        System.arraycopy(nonceAndCiphertext, 0, nonce, 0, 16);
        System.arraycopy(nonceAndCiphertext, 16, ciphertext, 0, ciphertext.length);

        return ctrMode.decrypt(ciphertext, nonce);
    }

    public byte[] encryptWithMac(byte[] plaintext) {
        byte[] encrypted = encryptCbc(plaintext);
        return mac.appendMac(encrypted);
    }

    public byte[] decryptWithMac(byte[] dataWithMac) {
        byte[] encrypted = verifier.extractData(dataWithMac);
        return decryptCbc(encrypted);
    }

    public byte[] computeMac(byte[] data) {
        return mac.compute(data);
    }

    public boolean verifyMac(byte[] data, byte[] expectedMac) {
        return verifier.verify(data, expectedMac);
    }
}
