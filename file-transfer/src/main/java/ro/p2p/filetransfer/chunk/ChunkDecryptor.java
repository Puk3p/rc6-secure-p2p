package ro.p2p.filetransfer.chunk;

import ro.p2p.crypto.facade.CryptoFacade;

public class ChunkDecryptor {

    public byte[] decrypt(byte[] encryptedChunk, byte[] sessionKey) {
        return new CryptoFacade(sessionKey).decryptWithMac(encryptedChunk);
    }
}
