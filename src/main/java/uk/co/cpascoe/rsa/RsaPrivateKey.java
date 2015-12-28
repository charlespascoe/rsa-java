package uk.co.cpascoe.rsa;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class RsaPrivateKey extends RsaKey {
    protected BigInt p;
    protected BigInt q;
    protected BigInt phi_n;
    protected BigInt d;
    protected BigInt dp;
    protected BigInt dq;
    protected BigInt qinv;

    /*
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

        this.n = new BigInt(data.get("n"), 16);
        this.e = new BigInt(data.get("e"), 16);
        this.p = new BigInt(data.get("p"), 16);
        this.q = new BigInt(data.get("q"), 16);
        this.phi_n = new BigInt(data.get("phi_n"), 16);
        this.d = new BigInt(data.get("d"), 16);
        this.dp = new BigInt(data.get("dp"), 16);
        this.dq = new BigInt(data.get("dq"), 16);
        this.qinv = new BigInt(data.get("qinv"), 16);
    }
    */

    public RsaPrivateKey(int bits) {
        this.e = new BigInt(65537);

        do {
            System.out.println("Generating P");
            this.p = MathUtils.generateProbablePrime(bits / 2);
            System.out.println("Generating Q");
            this.q = MathUtils.generateProbablePrime(bits / 2);

            System.out.println("Generated Primes");

            this.n = this.p.multiply(q);
            this.phi_n = this.p.subtract(new BigInt(1)).multiply(this.q.subtract(new BigInt(1)));

        } while (!MathUtils.gcd(this.e, this.phi_n).equals(new BigInt(1)));

        this.d = this.e.modInverse(this.phi_n);
        this.dp = this.d.mod(this.p.subtract(new BigInt(1)));
        this.dq = this.d.mod(this.q.subtract(new BigInt(1)));
        this.qinv = this.q.modInverse(this.p);
    }

    public BigInt privateExponentation(BigInt val) {
        return val.powMod(this.d, this.n);
    }

    public BigInt fastPrivateExponentation(BigInt val) {
        BigInt m1 = val.powMod(this.dp, this.p);
        BigInt m2 = val.powMod(this.dq, this.q);

        BigInt h = this.qinv.multiply(m1.subtract(m2)).mod(this.p);

        return m2.add(h.multiply(this.q));
    }

    /*
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
    */
}
