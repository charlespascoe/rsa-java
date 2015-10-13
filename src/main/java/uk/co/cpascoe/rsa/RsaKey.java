package uk.co.cpascoe.rsa;

import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;

public class RsaKey {
    protected BigInteger n;
    protected BigInteger e;

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
