package uk.co.cpascoe.rsa;

import java.io.*;
import java.security.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RsaProvider {
    public final static String CIPHER_NAME = "AES";
    public final static String CIPHER_MODE = RsaProvider.CIPHER_NAME + "/CBC/PKCS5Padding";

    public final static int KEY_SIZE = 128;
    public final static int BLOCK_SIZE = 128;

    private final RsaKey rsaKey;

    /**
     * Creates a new RsaProvider which uses the given RsaKey
     * @throws NullPointerException If the RSA Key is null
     */
    public RsaProvider(RsaKey rsaKey) {
        if (rsaKey == null) throw new NullPointerException("rsaKey should not be null");
        this.rsaKey = rsaKey;
    }

    /**
     * Encrypts the given symmetric key using RSA
     * @throws EncryptionException
     */
    protected byte[] encryptKey(byte[] symmetricKey) throws EncryptionException {
        int keyByteLength = this.rsaKey.byteCount();

        OaepProvider oaep = new OaepProvider();

        if (symmetricKey.length > oaep.maxMessageLength(keyByteLength)) {
            throw new EncryptionException("Key too long");
        }

        byte[] encodedKeyBlock;

        try {
            encodedKeyBlock = oaep.encode(symmetricKey, keyByteLength);
        } catch (OaepProvider.EncodingException ex) {
            throw new EncryptionException(ex);
        }

        BigInt m = new BigInt(encodedKeyBlock);
        BigInt c = this.rsaKey.publicExponentation(m);

        return Arrays.copyOf(c.exportToByteArray(), keyByteLength);
    }

    /**
     * Decrypts the symmetric key using RSA
     * @throws DecryptionException
     */
    protected byte[] decryptKey(byte[] encryptedKeyBlock) throws DecryptionException {
        int keyByteLength = this.rsaKey.byteCount();

        BigInt c = new BigInt(encryptedKeyBlock);
        BigInt m = ((RsaPrivateKey)this.rsaKey).privateExponentation(c);

        byte[] encodedBlock = Arrays.copyOf(m.exportToByteArray(), keyByteLength - 1);

        OaepProvider oaep = new OaepProvider();

        try {
            return oaep.decode(encodedBlock, keyByteLength);
        } catch (Exception ex) {
            throw new DecryptionException(ex);
        }
    }

    /**
     * Encrypts all data from an input stream and writes it to an output stream, including keying data
     * @param input The unencrypted input stream
     * @param output The encrypted output stream
     * @throws IOException
     * @throws EncryptionException
     */
    public void encrypt(InputStream input, OutputStream output) throws IOException, EncryptionException {
        KeyGenerator keyGen;
        Cipher cipher;
        SecretKey key;

        SecureRandom r = new SecureRandom();

        byte[] iv = new byte[RsaProvider.BLOCK_SIZE / 8];
        r.nextBytes(iv);

        try {
            keyGen = KeyGenerator.getInstance(RsaProvider.CIPHER_NAME);
            keyGen.init(RsaProvider.KEY_SIZE, r);
            key = keyGen.generateKey();

            cipher = Cipher.getInstance(RsaProvider.CIPHER_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (Exception ex) {
            throw new EncryptionException(ex);
        }

        output.write(this.encryptKey(Utils.concat(key.getEncoded(), iv)));

        CipherOutputStream encryptedOutput = new CipherOutputStream(output, cipher);
        Utils.pipeStream(input, encryptedOutput);
        encryptedOutput.flush();
        encryptedOutput.close();
    }

    /**
     * Decrypts all data from an input stream and writes it to an output stream
     * @param input The encrypted input stream
     * @param output The decrypted output stream
     * @throws IOException
     * @throws EncryptionException
     */
    public void decrypt(InputStream input, OutputStream output) throws IOException, DecryptionException {
        if (!(this.rsaKey instanceof RsaPrivateKey)) {
            throw new DecryptionException("RsaPrivateKey required for decryption!");
        }

        Cipher cipher;
        SecretKey key;

        byte[] encryptedKey = new byte[this.rsaKey.byteCount()];

        input.read(encryptedKey);

        byte[] keyAndIv = this.decryptKey(encryptedKey);

        byte[] keyBytes = Utils.takeBytes(keyAndIv, RsaProvider.KEY_SIZE / 8);
        byte[] iv = Utils.removeBytes(keyAndIv, keyBytes.length);

        try {
            key = new SecretKeySpec(keyBytes, RsaProvider.CIPHER_NAME);

            cipher = Cipher.getInstance(RsaProvider.CIPHER_MODE);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (Exception ex) {
            throw new DecryptionException(ex);
        }

        CipherOutputStream decryptedOutput = new CipherOutputStream(output, cipher);
        Utils.pipeStream(input, decryptedOutput);
        decryptedOutput.flush();
        decryptedOutput.close();
    }

    /**
     * Encrypts the given data with RSA/AES
     */
    public byte[] encrypt(byte[] data) throws EncryptionException {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            this.encrypt(input, output);
        } catch (IOException ex) {
            throw new EncryptionException("Unexpected IOException", ex);
        }

        return output.toByteArray();
    }

    /**
     * Decrypts the given data that was encrypted with RSA/AES and the RSA Key
     */
    public byte[] decrypt(byte[] data) throws DecryptionException {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            this.decrypt(input, output);
        } catch (IOException ex) {
            throw new DecryptionException("Unexpected IOException", ex);
        }

        return output.toByteArray();
    }
}
