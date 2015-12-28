package uk.co.cpascoe.rsa;

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
        Pattern p = Pattern.compile("^[0-9A-Fa-f]+$");
        return p.matcher(str).matches();
    }

    public static String bytesToBase64(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public static byte[] base64ToBytes(String base64) {
        return DatatypeConverter.parseBase64Binary(base64);
    }

    /**
     * Returns the result of the two byte arrays xored together
     *
     * @param a The first array
     * @param b The second array, which must be the same length as 'a'
     */
    public static byte[] xorBytes(byte[] a, byte[] b) throws Exception {
        if (a.length != b.length) {
            throw new Exception("Arrays have different lengths");
        }

        byte[] out = new byte[a.length];

        for (int i = 0; i < out.length; i++) {
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
}
