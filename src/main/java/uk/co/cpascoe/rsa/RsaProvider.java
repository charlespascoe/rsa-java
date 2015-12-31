package uk.co.cpascoe.rsa;


public class RsaProvider {
    private RsaKey key;

    public RsaProvider(RsaKey key) {
        if (key == null) throw new NullPointerException("key should not be null");
        this.key = key;
    }

    public byte[] encrypt(byte[] data) {
        return new byte[0];
    }

    public byte[] decrypt(byte[] data) {
        return new byte[0];
    }
}
