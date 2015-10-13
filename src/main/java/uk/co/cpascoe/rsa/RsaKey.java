package uk.co.cpascoe.rsa;

import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;

public class RsaKey {
    protected BigInteger n;
    protected BigInteger e;

    public RsaKey(Map<String, String> data) throws Exception {
        if (!data.containsKey("n") || !data.containsKey("e")) {
            throw new Exception("Keys missing from data object");
        }

        if (!Utils.isValidHex(data.get("n")) || !Utils.isValidHex(data.get("e"))) {
            throw new Exception("Invalid hex values");
        }

        this.n = new BigInteger(data.get("n"), 16);
        this.e = new BigInteger(data.get("e"), 16);
    }

    public BigInteger publicExponentation(BigInteger val) {
        return val.modPow(this.e, this.n);
    }

    public byte[] exportDer() {
        return new byte[0];
    }

    public Map<String, String> exportToMap() {
        Map<String, String> data = new HashMap<String, String>();

        data.put("n", n.toString(16));
        data.put("e", e.toString(16));

        return data;
    }
}
