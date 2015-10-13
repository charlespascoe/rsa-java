package uk.co.cpascoe.rsa;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
        if (msg.length > this.maxMessageLength(keyLength)) {
            throw new Exception("Message too long");
        }

        byte[] labelHash;

        if (labelIsHash) {
            if (label.length != this.digestLength) {
                throw new Exception("Label length is not equal to the digest length");
            }

            labelHash = label;
        } else {
            labelHash = MessageDigest.getInstance(this.digestName).digest(label);
        }

        byte[] dataBlock = OaepProvider.buildDataBlock(labelHash, msg, keyLength, this.maxMessageLength(keyLength));
        byte[] seed = new SecureRandom().generateSeed(this.digestLength);

        byte[] maskedDataBlock = Utils.xorBytes(dataBlock, this.maskGen.generateMask(seed, dataBlock.length));
        byte[] maskedSeed = Utils.xorBytes(seed, this.maskGen.generateMask(maskedDataBlock, seed.length));

        return Utils.concat(maskedDataBlock, maskedSeed);
    }

    public static byte[] buildDataBlock(byte[] labelHash, byte[] msg, int keyLength, int maxMessageLength) throws Exception {
        if (msg.length > maxMessageLength) {
            throw new Exception("Message too long");
        }

        int paddingLength = maxMessageLength - msg.length;
        int blockLength = labelHash.length + paddingLength + 1 + msg.length;

        byte[] dataBlock = new byte[blockLength];

        int position = 0;
        System.arraycopy(labelHash, 0, dataBlock, position, labelHash.length);
        position += labelHash.length;

        // Add 0 padding
        position += paddingLength;

        dataBlock[position] = 1;
        position++;

        System.arraycopy(msg, 0, dataBlock, position, msg.length);

        return dataBlock;
    }
}
