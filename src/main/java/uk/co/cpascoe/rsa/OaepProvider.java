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

    public int getDigestLength() {
        return this.digestLength;
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
        return this.encode(msg, keyLength, label, labelIsHash, new SecureRandom().generateSeed(this.digestLength));
    }

    public byte[] encode(byte[] msg, int keyLength, byte[] label, boolean labelIsHash, byte[] seed) throws Exception {
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

        byte[] dataBlock = OaepProvider.buildDataBlock(labelHash, msg, this.maxMessageLength(keyLength));

        byte[] maskedDataBlock = Utils.xorBytes(dataBlock, this.maskGen.generateMask(seed, dataBlock.length));
        byte[] maskedSeed = Utils.xorBytes(seed, this.maskGen.generateMask(maskedDataBlock, seed.length));

        return Utils.concat(maskedSeed, maskedDataBlock);
    }

    public static byte[] buildDataBlock(byte[] labelHash, byte[] msg, int maxMessageLength) throws Exception {
        if (msg.length > maxMessageLength) {
            throw new Exception("Message too long");
        }

        int paddingLength = maxMessageLength - msg.length;
        byte[] padding = new byte[paddingLength];

        return Utils.concat(labelHash, padding, new byte[] {1}, msg);
    }

    public byte[] decode(byte[] encMsgBlock, int keySize, byte[] label, boolean labelIsHash) throws Exception {
        if (encMsgBlock.length != keySize - 1) {
            throw new Exception("encMsgBlock length must be equal to keySize - 1");
        }

        if (keySize < (2*this.getDigestLength() + 2)) {
            throw new Exception("keySize too small");
        }

        byte[] maskedSeed = Utils.takeBytes(encMsgBlock, this.getDigestLength());
        byte[] maskedDataBlock = Utils.removeBytes(encMsgBlock, this.getDigestLength());

        byte[] seed = Utils.xorBytes(maskedSeed, this.maskGen.generateMask(maskedDataBlock, maskedSeed.length));
        byte[] dataBlock = Utils.xorBytes(maskedDataBlock, this.maskGen.generateMask(seed, maskedDataBlock.length));

        byte[] decodedLabelHash = Utils.takeBytes(dataBlock, this.getDigestLength());
        byte[] decodedPaddedMessage = Utils.removeBytes(dataBlock, this.getDigestLength());

        return OaepProvider.removePadding(decodedPaddedMessage);
    }

    public static byte[] removePadding(byte[] paddedMessage) {
        for (int i = 0; i < paddedMessage.length; i++) {
            if (paddedMessage[i] == 1) {
                return Utils.removeBytes(paddedMessage, i + 1);
            }
        }

        return new byte[0];
    }
}
