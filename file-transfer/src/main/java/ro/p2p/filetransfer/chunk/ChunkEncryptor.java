package ro.p2p.filetransfer.chunk;

import ro.p2p.crypto.facade.CryptoFacade;

public class ChunkEncryptor {

    public byte[] encrypt(byte[] chunk, byte[] sessionKey) {
        return new CryptoFacade(sessionKey).encryptWithMac(chunk);
    }
}
