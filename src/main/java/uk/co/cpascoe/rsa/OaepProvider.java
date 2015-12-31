package uk.co.cpascoe.rsa;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class OaepProvider {
    public static class EncodingException extends Exception {
        public EncodingException(String message) {
            super(message);
        }
    }

    public static class DecodingException extends Exception {
        public DecodingException(String message) {
            super(message);
        }
    }

    private MaskGenerator maskGen;
    private String digestName;
    private int digestLength;

    public OaepProvider() {
        this(new MaskGenerator());
    }

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

    protected byte[] hash(byte[] input) {
        try {
            return MessageDigest.getInstance(this.digestName).digest(input);
        } catch (NoSuchAlgorithmException ex) {
            // This should never run, as the OaepProvider constructor should throw an exception
            // if the message digest doesn't exist
            throw new Error("Unexpected NoSuchAlgorithmException", ex);
        }
    }

    public int maxMessageLength(int keylength) {
        return keylength - 2*maskGen.getDigestLength() - 2;
    }

    public byte[] encode(byte[] msg, int keyLength) throws EncodingException {
        return this.encode(msg, keyLength, new byte[0]);
    }

    public byte[] encode(byte[] msg, int keyLength, byte[] label) throws EncodingException {
        return this.encode(msg, keyLength, label, false);
    }

    public byte[] encode(byte[] msg, int keyLength, byte[] label, boolean labelIsHash) throws EncodingException {
        return this.encode(msg, keyLength, label, labelIsHash, new SecureRandom().generateSeed(this.digestLength));
    }

    public byte[] encode(byte[] msg, int keyLength, byte[] label, boolean labelIsHash, byte[] seed) throws EncodingException {
        if (msg.length > this.maxMessageLength(keyLength)) {
            throw new EncodingException("Message too long");
        }

        byte[] labelHash;

        if (labelIsHash) {
            if (label.length != this.digestLength) {
                throw new EncodingException("Label hash length is not equal to the digest length");
            }

            labelHash = label;
        } else {
            labelHash = this.hash(label);
        }

        byte[] dataBlock = OaepProvider.buildDataBlock(labelHash, msg, this.maxMessageLength(keyLength));

        byte[] maskedDataBlock = Utils.xorBytes(dataBlock, this.maskGen.generateMask(seed, dataBlock.length));
        byte[] maskedSeed = Utils.xorBytes(seed, this.maskGen.generateMask(maskedDataBlock, seed.length));

        return Utils.concat(maskedSeed, maskedDataBlock);
    }

    public static byte[] buildDataBlock(byte[] labelHash, byte[] msg, int maxMessageLength) throws EncodingException {
        if (msg.length > maxMessageLength) {
            throw new EncodingException("Message too long");
        }

        int paddingLength = maxMessageLength - msg.length;
        byte[] padding = new byte[paddingLength];

        return Utils.concat(labelHash, padding, new byte[] {1}, msg);
    }

    public byte[] decode(byte[] encMsgBlock, int keySize) throws DecodingException {
        return this.decode(encMsgBlock, keySize, new byte[0]);
    }

    public byte[] decode(byte[] encMsgBlock, int keySize, byte[] label) throws DecodingException {
        return this.decode(encMsgBlock, keySize, label, false);
    }

    public byte[] decode(byte[] encMsgBlock, int keySize, byte[] label, boolean labelIsHash) throws DecodingException {
        if (encMsgBlock.length != keySize - 1) {
            throw new DecodingException("encMsgBlock length must be equal to keySize - 1");
        }

        if (keySize < (2*this.getDigestLength() + 2)) {
            throw new DecodingException("keySize too small");
        }

        byte[] labelHash;

        if (labelIsHash) {
            if (label.length != this.digestLength) {
                throw new DecodingException("Label length is not equal to the digest length");
            }

            labelHash = label;
        } else {
            labelHash = this.hash(label);
        }

        byte[] maskedSeed = Utils.takeBytes(encMsgBlock, this.getDigestLength());
        byte[] maskedDataBlock = Utils.removeBytes(encMsgBlock, this.getDigestLength());

        byte[] seed = Utils.xorBytes(maskedSeed, this.maskGen.generateMask(maskedDataBlock, maskedSeed.length));
        byte[] dataBlock = Utils.xorBytes(maskedDataBlock, this.maskGen.generateMask(seed, maskedDataBlock.length));

        byte[] decodedLabelHash = Utils.takeBytes(dataBlock, this.getDigestLength());
        byte[] decodedPaddedMessage = Utils.removeBytes(dataBlock, this.getDigestLength());

        if (!Utils.constantTimeEquals(labelHash, decodedLabelHash)) {
            throw new DecodingException("Label hashes do not match");
        }

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
