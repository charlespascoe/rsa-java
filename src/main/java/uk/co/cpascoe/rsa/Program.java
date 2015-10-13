package uk.co.cpascoe.rsa;

import java.util.Map;

public class Program {
    public static void main(String[] args) {
        RsaPrivateKey key = new RsaPrivateKey(2048);
        Map<String, String> data = key.exportToMap();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            System.out.printf("%s: %s%n%n", entry.getKey(), entry.getValue());
        }
    }
}
