package uk.co.cpascoe.rsa.crypto.pkcs1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import uk.co.cpascoe.rsa.Utils;

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

    /**
     * Creates a new OaepProvider instance, using the default SHA-256 MaskGenerator
     */
    public OaepProvider() {
        this(new MaskGenerator());
    }

    /**
     * Creates a new OaepProvider instance, using the given MaskGenerator
     */
    public OaepProvider(MaskGenerator maskGen) {
        this.maskGen = maskGen;
        this.digestName = maskGen.getDigestName();
        this.digestLength = maskGen.getDigestLength();
    }

    /**
     * Creates a new OaepProvider instance, using the given MaskGenerator, and the given message digest for label/seed hashing
     * @param mdName The name of the message digest (hashing) function to use
     * @throws NoSuchAlgorithmException If the given message digest does not exist
     */
    public OaepProvider(MaskGenerator maskGen, String digestName) throws NoSuchAlgorithmException {
        this.maskGen = maskGen;
        this.digestName = digestName;
        this.digestLength = MessageDigest.getInstance(digestName).getDigestLength();
    }

    /**
     * Returns the name of the message digest
     */
    public int getDigestLength() {
        return this.digestLength;
    }

    /**
     * Returns the hash of the given input data, using the message digest
     */
    protected byte[] hash(byte[] input) {
        try {
            return MessageDigest.getInstance(this.digestName).digest(input);
        } catch (NoSuchAlgorithmException ex) {
            // This should never run, as the OaepProvider constructor should throw an exception
            // if the message digest doesn't exist
            throw new Error("Unexpected NoSuchAlgorithmException", ex);
        }
    }

    /**
     * Returns the maximum allowed message length for the given key length
     * @param keyLength The length of the RSA Key Modulus, in bytes
     */
    public int maxMessageLength(int keylength) {
        return keylength - 2*maskGen.getDigestLength() - 2;
    }

    /**
     * Encodes the given message for a key of the given length
     * @param msg The message to encode
     * @param keyLength The length of the RSA Key Modulus, in bytes
     * @throws EncodingException
     */
    public byte[] encode(byte[] msg, int keyLength) throws EncodingException {
        return this.encode(msg, keyLength, new byte[0]);
    }

    /**
     * Encodes the given message for a key of the given length
     * @param msg The message to encode
     * @param keyLength The length of the RSA Key Modulus, in bytes
     * @param label The label to encode with this message, as per PKCS#1v2.2 standard
     * @throws EncodingException
     */
    public byte[] encode(byte[] msg, int keyLength, byte[] label) throws EncodingException {
        return this.encode(msg, keyLength, label, false);
    }

    /**
     * Encodes the given message for a key of the given length
     * @param msg The message to encode
     * @param keyLength The length of the RSA Key Modulus, in bytes
     * @param label The label or the hash of the label to encode with this message, as per PKCS#1v2.2 standard
     * @param labelIsHash True if the value of "label" is already a hash (using the same message digest); False otherwise
     * @throws EncodingException
     */
    public byte[] encode(byte[] msg, int keyLength, byte[] label, boolean labelIsHash) throws EncodingException {
        return this.encode(msg, keyLength, label, labelIsHash, new SecureRandom().generateSeed(this.digestLength));
    }

    /**
     * Encodes the given message for a key of the given length
     * @param msg The message to encode
     * @param keyLength The length of the RSA Key Modulus, in bytes
     * @param label The label or the hash of the label to encode with this message, as per PKCS#1v2.2 standard
     * @param labelIsHash True if the value of "label" is already a hash (using the same message digest); False otherwise
     * @param seed The seed to use to encode the message, as per PKCS#1v2.2 standard
     * @throws EncodingException
     */
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

    /**
     * Creates the data block (combination of label hash, padding, and the message)
     * @throws EncodingException
     */
    protected static byte[] buildDataBlock(byte[] labelHash, byte[] msg, int maxMessageLength) throws EncodingException {
        if (msg.length > maxMessageLength) {
            throw new EncodingException("Message too long");
        }

        int paddingLength = maxMessageLength - msg.length;
        byte[] padding = new byte[paddingLength];

        return Utils.concat(labelHash, padding, new byte[] {1}, msg);
    }

    /**
     * Decodes the given encoded message block
     * @param encMsgBlock The encoded message block, whose length must be equal to keyLength
     * @param keyLength The length of the RSA Key Modulus, in bytes
     * @throws DecodingException
     */
    public byte[] decode(byte[] encMsgBlock, int keyLength) throws DecodingException {
        return this.decode(encMsgBlock, keyLength, new byte[0]);
    }

    /**
     * Decodes the given encoded message block
     * @param encMsgBlock The encoded message block, whose length must be equal to keyLength
     * @param keyLength The length of the RSA Key Modulus, in bytes
     * @param label The label that the original message was encoded with - decoding fails if this does not match
     * @throws DecodingException
     */
    public byte[] decode(byte[] encMsgBlock, int keyLength, byte[] label) throws DecodingException {
        return this.decode(encMsgBlock, keyLength, label, false);
    }

    /**
     * Decodes the given encoded message block
     * @param encMsgBlock The encoded message block, whose length must be equal to keyLength
     * @param keyLength The length of the RSA Key Modulus, in bytes
     * @param label The label or hash of the label that the original message was encoded with - decoding fails if this does not match
     * @param labelIsHash True if the value of "label" is already a hash (using the same message digest); False otherwise
     * @throws DecodingException
     */
    public byte[] decode(byte[] encMsgBlock, int keyLength, byte[] label, boolean labelIsHash) throws DecodingException {
        if (encMsgBlock.length != keyLength - 1) {
            throw new DecodingException("encMsgBlock length must be equal to keyLength - 1");
        }

        if (keyLength < (2*this.getDigestLength() + 2)) {
            throw new DecodingException("keyLength too small");
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

    /**
     * Removes the padding from the given padded message, as per PKCS#1v2.2
     */
    protected static byte[] removePadding(byte[] paddedMessage) {
        for (int i = 0; i < paddedMessage.length; i++) {
            if (paddedMessage[i] == 1) {
                return Utils.removeBytes(paddedMessage, i + 1);
            }
        }

        return new byte[0];
    }
}
