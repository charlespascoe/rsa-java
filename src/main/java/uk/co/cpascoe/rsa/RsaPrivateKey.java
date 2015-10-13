package uk.co.cpascoe.rsa;

import java.math.BigInteger;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class RsaPrivateKey extends RsaKey {
    protected BigInteger p;
    protected BigInteger q;
    protected BigInteger phi_n;
    protected BigInteger d;
    protected BigInteger dp;
    protected BigInteger dq;
    protected BigInteger qinv;

    public RsaPrivateKey(Map<String, String> data) throws Exception {
        if (!data.containsKey("n") ||
            !data.containsKey("e") ||
            !data.containsKey("p") ||
            !data.containsKey("q") ||
            !data.containsKey("phi_n") ||
            !data.containsKey("d") ||
            !data.containsKey("dp") ||
            !data.containsKey("dq") ||
            !data.containsKey("qinv")) {

            throw new Exception("Keys missing from data object");
        }

        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!Utils.isValidHex(entry.getValue())) {
                throw new Exception("Invalid hex values");
            }
        }

        this.n = new BigInteger(data.get("n"), 16);
        this.e = new BigInteger(data.get("e"), 16);
        this.p = new BigInteger(data.get("p"), 16);
        this.q = new BigInteger(data.get("q"), 16);
        this.phi_n = new BigInteger(data.get("phi_n"), 16);
        this.d = new BigInteger(data.get("d"), 16);
        this.dp = new BigInteger(data.get("dp"), 16);
        this.dq = new BigInteger(data.get("dq"), 16);
        this.qinv = new BigInteger(data.get("qinv"), 16);
    }

    public RsaPrivateKey(int bits) {
        this.e = new BigInteger("65537");

        Random r = new Random();

        do {
            this.p = new BigInteger(bits / 2, 15, r);
            this.q = new BigInteger(bits / 2, 15, r);

            this.n = this.p.multiply(q);
            this.phi_n = this.p.subtract(BigInteger.ONE).multiply(this.q.subtract(BigInteger.ONE));

        } while (!this.phi_n.gcd(this.e).equals(BigInteger.ONE));

        this.d = this.e.modInverse(this.phi_n);
        this.dp = this.d.mod(this.p.subtract(BigInteger.ONE));
        this.dq = this.d.mod(this.q.subtract(BigInteger.ONE));
        this.qinv = this.q.modInverse(this.p);
    }

    public BigInteger privateExponentation(BigInteger val) {
        return val.modPow(this.d, this.n);
    }

    public BigInteger fastPrivateExponentation(BigInteger val) {
        BigInteger m1 = val.modPow(this.dp, this.p);
        BigInteger m2 = val.modPow(this.dq, this.q);

        BigInteger h = this.qinv.multiply(m1.subtract(m2)).mod(this.p);

        return m2.add(h.multiply(this.q));
    }

    @Override
    public Map<String, String> exportToMap() {
        Map<String, String> data = super.exportToMap();

        data.put("p", this.p.toString(16));
        data.put("q", this.q.toString(16));
        data.put("phi_n", this.phi_n.toString(16));
        data.put("d", this.d.toString(16));
        data.put("dp", this.dp.toString(16));
        data.put("dq", this.dq.toString(16));
        data.put("qinv", this.qinv.toString(16));

        return data;
    }
}
