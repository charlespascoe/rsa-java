package uk.co.cpascoe.rsa;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

public abstract class Utils {
    /**
     * Returns true if the string is a string of only one or more hex characters (0-9,a-f,A-F)
     *
     * @param str The string under test
     */
    public static boolean isValidHex(String str) {
        if (str == null) return false;
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2})+$");
        return p.matcher(str).matches();
    }

    public static String bytesToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    public static byte[] hexToBytes(String base64) {
        return DatatypeConverter.parseHexBinary(base64);
    }

    /**
     * Returns the result of the two byte arrays xored together
     *
     * @param a The first array
     * @param b The second array, which can have a different length to 'a'
     * @return The XORed values of the common bytes, of length min(a.length, b.length)
     */
    public static byte[] xorBytes(byte[] a, byte[] b) {
        int length = Math.min(a.length, b.length);

        byte[] out = new byte[length];

        for (int i = 0; i < length; i++) {
            out[i] = (byte)(a[i] ^ b[i]);
        }

        return out;
    }

    /**
     * Returns a byte array consisting of the concatenated byte arrays passed in as parameters
     */
    public static byte[] concat(byte[]... arrays) {
        int length = 0;

        for (byte[] array : arrays) {
            length += array.length;
        }

        byte[] out = new byte[length];

        int pos = 0;

        for (byte[] array : arrays) {
            System.arraycopy(array, 0, out, pos, array.length);
            pos += array.length;
        }

        return out;
    }

    /**
     * Returns the first n bytes of the input array
     */
    public static byte[] takeBytes(byte[] input, int count) {
        return Arrays.copyOf(input, count);
    }

    /**
     * Returns the input array without the first n bytes
     */
    public static byte[] removeBytes(byte[] input, int count) {
        return Arrays.copyOfRange(input, count, input.length);
    }

    public static void pipeStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[65536];

        int readCount = 0;

        while ((readCount = input.read(buffer)) != -1) {
            output.write(buffer, 0, readCount);
        }
    }

    public static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;

        int result = 0;

        for (int i = 0; i < a.length; i++) {
            result += a[i] ^ b[i];
        }

        return result == 0;
    }
}
