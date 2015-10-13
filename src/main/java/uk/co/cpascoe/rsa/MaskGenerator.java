package uk.co.cpascoe.rsa;

import java.security.MessageDigest;

public class MaskGenerator {
    private String mdName;
    private int digestLength;

    public MaskGenerator(String mdName) {
        this.mdName = mdName;
        this.digestLength = MessageDigest.getInstance(mdName).getDigestLength();
    }

    public byte[] generateMask(byte[] seed, int length) {
        int lengthModDigestLength = length % this.digestLength;

        int blocks = (length / this.digestLength) + (lengthModDigestLength == 0 ? 0 : 1);

        byte[] mask = new byte[length];

        for (int i = 0; i < blocks; i++) {
            MessageDigest md = MessageDigest.getInstance(this.mdName);

            md.update(seed);
            md.update(Utils.intToBytes(i));

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
