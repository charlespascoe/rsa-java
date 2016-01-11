package uk.co.cpascoe.rsa.crypto.pkcs1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import uk.co.cpascoe.rsa.maths.MathUtils;

public class MaskGenerator {
    private String mdName;
    private int digestLength;

    /**
     * Creates a new MaskGenerator instance, using SHA-256 as the message digest
     */
    public MaskGenerator() {
        this.mdName = "SHA-256";

        try {
            this.digestLength = MessageDigest.getInstance(this.mdName).getDigestLength();
        } catch (NoSuchAlgorithmException ex) {
            throw new Error("SHA-256 no longer exists!");
        }
    }

    /**
     * Creates a new MaskGenerator instance, using the given message digest
     * @param mdName The name of the message digest (hashing) function to use
     * @throws NoSuchAlgorithmException If the given message digest does not exist
     */
    public MaskGenerator(String mdName) throws NoSuchAlgorithmException {
        this.mdName = mdName;
        this.digestLength = MessageDigest.getInstance(mdName).getDigestLength();
    }

    /**
     * Returns the name of the message digest
     */
    public String getDigestName() {
        return this.mdName;
    }

    /**
     * Returns the length of the output of the message digest, in bytes
     */
    public int getDigestLength() {
        return this.digestLength;
    }

    /**
     * Generates a mask using the given seed
     * @return A byte array, the length equal to the given length
     */
    public byte[] generateMask(byte[] seed, int length) {
        int lengthModDigestLength = length % this.digestLength;

        int blocks = MathUtils.divCeil(length, this.digestLength);

        byte[] mask = new byte[length];

        for (int i = 0; i < blocks; i++) {
            MessageDigest md;

            try {
                md = MessageDigest.getInstance(this.mdName);
            } catch (NoSuchAlgorithmException ex) {
                // This should never run under normal circumstances, as the MaskGenerator constructor should throw an exception
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
