package uk.co.cpascoe.rsa;


public class RsaProvider {
    private RsaKey key;

    public RsaProvider(RsaKey key) {
        this.key = key;
    }

    public byte[] encrypt(byte[] data) {
        return new byte[0];
    }

    public byte[] decrypt(byte[] data) {
        return new byte[0];
    }
}
