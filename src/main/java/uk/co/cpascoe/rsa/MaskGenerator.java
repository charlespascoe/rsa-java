package uk.co.cpascoe.rsa;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MaskGenerator {
    private String mdName;
    private int digestLength;

    public MaskGenerator() {
        this.mdName = "SHA-256";

        try {
            this.digestLength = MessageDigest.getInstance(this.mdName).getDigestLength();
        } catch (NoSuchAlgorithmException ex) {
            throw new Error("SHA-256 no longer exists!");
        }
    }

    public MaskGenerator(String mdName) throws NoSuchAlgorithmException {
        this.mdName = mdName;
        this.digestLength = MessageDigest.getInstance(mdName).getDigestLength();
    }

    public String getDigestName() {
        return this.mdName;
    }

    public int getDigestLength() {
        return this.digestLength;
    }

    public byte[] generateMask(byte[] seed, int length) {
        int lengthModDigestLength = length % this.digestLength;

        int blocks = (length / this.digestLength) + (lengthModDigestLength == 0 ? 0 : 1);

        byte[] mask = new byte[length];

        for (int i = 0; i < blocks; i++) {
            MessageDigest md;

            try {
                md = MessageDigest.getInstance(this.mdName);
            } catch (NoSuchAlgorithmException ex) {
                // This should never run, as the MaskGenerator constructor should throw an exception
                // if the message digest doesn't exist
                throw new Error("Unexpected NoSuchAlgorithmException", ex);
            }

            md.update(seed);
            md.update(MathUtils.intToBigEndianBytes(i));

            int copyLength = this.digestLength;

            // If it's the last block and the length is not a multiple of the digest length,
            // then only copy the last number of bytes
            if (i == blocks - 1 && lengthModDigestLength != 0) {
                copyLength = lengthModDigestLength;
            }

            System.arraycopy(md.digest(), 0, mask, i * this.digestLength, copyLength);
        }

        return mask;
    }
}
