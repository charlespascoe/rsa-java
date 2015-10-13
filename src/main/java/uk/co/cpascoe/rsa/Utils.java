package uk.co.cpascoe.rsa;

import java.util.regex.Pattern;

public abstract class Utils {
    public static boolean isValidHex(String str) {
        Pattern p = Pattern.compile("^[0-9A-Fa-f]+$");
        return p.matcher(str).matches();
    }

    public static byte[] intToBytes(int value) {
        byte[] data = new byte[4];

        int mask = 255;

        for (int i = 3; i >= 0; i--) {
            data[i] = (byte)(mask & value);
            mask = mask << 8;
        }

        return data;
    }
}
