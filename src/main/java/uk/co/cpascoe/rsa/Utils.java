package uk.co.cpascoe.rsa;

import java.util.regex.Pattern;

public abstract class Utils {
    public static boolean isValidHex(String str) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2})+$");
        return p.matcher(str).matches();
    }
}
