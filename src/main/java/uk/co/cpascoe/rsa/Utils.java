package uk.co.cpascoe.rsa;

import java.util.regex.Pattern;
import java.util.Arrays;

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

    /**
     * Returns the big-endian byte representation of the integer
     *
     * @param value The value to convert
     *
     * @return A byte array of length 4
     */
    public static byte[] intToBigEndianBytes(int value) {
        byte[] data = new byte[4];

        int mask = 255;

        for (int i = 3; i >= 0; i--) {
            data[i] = (byte)(mask & value);
            value = value >> 8;
        }

        return data;
    }

    public static int unsignedByte(byte value) {
        return value < 0 ? value + 256 : value;
    }

    public static long unsignedInt(int value) {
        return value < 0 ? value + Constants.TWO_POW_32 : value;
    }

    /**
     * Returns the integer represented by the big-endian byte array passed in
     */
    public static int bigEndianBytesToInt(byte[] data) {
        if (data.length != 4) {
            throw new Error("data must have a length of 4");
        }

        int value = 0;

        for (int i = 0; i < 4; i++) {
            value = (value << 8) + Utils.unsignedByte(data[i]);
        }

        return value;
    }

    /**
     * Returns the integer represented by the big-endian byte array passed in
     */
    public static int littleEndianBytesToInt(byte[] data) {
        if (data.length != 4) {
            throw new Error("data must have a length of 4");
        }

        int value = 0;

        for (int i = 3; i >= 0; i--) {
            value = (value << 8) + Utils.unsignedByte(data[i]);
        }

        return value;
    }

    public static int unsignedIntCompare(int a, int b) {
        return Long.compare(Utils.unsignedInt(a), Utils.unsignedInt(b));
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
