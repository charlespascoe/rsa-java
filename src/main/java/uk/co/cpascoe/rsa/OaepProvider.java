package uk.co.cpascoe.rsa;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OaepProvider {
    private MaskGenerator maskGen;
    private String digestName;
    private int digestLength;

    public OaepProvider(MaskGenerator maskGen) {
        this.maskGen = maskGen;
        this.digestName = maskGen.getDigestName();
        this.digestLength = maskGen.getDigestLength();
    }

    public OaepProvider(MaskGenerator maskGen, String digestName) throws NoSuchAlgorithmException {
        this.maskGen = maskGen;
        this.digestName = digestName;
        this.digestLength = MessageDigest.getInstance(digestName).getDigestLength();
    }

    public int maxMessageLength(int keylength) {
        return keylength - 2*maskGen.getDigestLength() - 2;
    }

    public byte[] encode(byte[] msg, int keyLength) throws Exception {
        return this.encode(msg, keyLength, new byte[0]);
    }

    public byte[] encode(byte[] msg, int keyLength, byte[] label) throws Exception {
        return this.encode(msg, keyLength, label, false);
    }

    public byte[] encode(byte[] msg, int keyLength, byte[] label, boolean labelIsHash) throws Exception {
        return new byte[0];
    }
}
