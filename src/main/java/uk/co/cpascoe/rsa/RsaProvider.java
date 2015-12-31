package uk.co.cpascoe.rsa;

import java.util.Arrays;

public class RsaProvider {
    private final RsaKey rsaKey;

    public RsaProvider(RsaKey rsaKey) {
        if (rsaKey == null) throw new NullPointerException("rsaKey should not be null");
        this.rsaKey = rsaKey;
    }

    protected byte[] encryptKey(byte[] symmetricKey) throws EncryptionException {
        int keyByteLength = this.rsaKey.byteCount();

        OaepProvider oaep = new OaepProvider();

        if (symmetricKey.length > oaep.maxMessageLength(keyByteLength)) {
            throw new EncryptionException("Key too long");
        }

        byte[] encodedKeyBlock;

        try {
            encodedKeyBlock = oaep.encode(symmetricKey, keyByteLength);
        } catch (OaepProvider.EncodingException ex) {
            throw new EncryptionException(ex);
        }

        BigInt m = new BigInt(encodedKeyBlock);
        BigInt c = this.rsaKey.publicExponentation(m);

        return Arrays.copyOf(c.exportToByteArray(), keyByteLength);
    }

    protected byte[] decryptKey(byte[] encryptedKeyBlock) throws DecryptionException {
        int keyByteLength = this.rsaKey.byteCount();

        BigInt c = new BigInt(encryptedKeyBlock);
        BigInt m = ((RsaPrivateKey)this.rsaKey).privateExponentation(c);

        byte[] encodedBlock = Arrays.copyOf(m.exportToByteArray(), keyByteLength - 1);

        OaepProvider oaep = new OaepProvider();

        try {
            return oaep.decode(encodedBlock, keyByteLength);
        } catch (Exception ex) {
            throw new DecryptionException(ex);
        }
    }

    public byte[] encrypt(byte[] data) {
        return new byte[0];
    }

    public byte[] decrypt(byte[] data) {
        return new byte[0];
    }
}
