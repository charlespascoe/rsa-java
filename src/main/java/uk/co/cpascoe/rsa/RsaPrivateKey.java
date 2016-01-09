package uk.co.cpascoe.rsa;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class RsaPrivateKey extends RsaKey {
    protected BigInt p;
    protected BigInt q;
    protected BigInt phi_n;
    protected BigInt d;
    protected BigInt dp;
    protected BigInt dq;
    protected BigInt qinv;

    /**
     * Creates a new RSA public key from the given data
     * @param data A map containing the modulus and public exponent for the key
     * @throws Exception
     */
    protected RsaPrivateKey(Map<String, String> data) throws Exception {
        super(data);

        if (!data.containsKey("p") ||
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

        this.p = new BigInt(Utils.hexToBytes(data.get("p")));
        this.q = new BigInt(Utils.hexToBytes(data.get("q")));
        this.phi_n = new BigInt(Utils.hexToBytes(data.get("phi_n")));
        this.d = new BigInt(Utils.hexToBytes(data.get("d")));
        this.dp = new BigInt(Utils.hexToBytes(data.get("dp")));
        this.dq = new BigInt(Utils.hexToBytes(data.get("dq")));
        this.qinv = new BigInt(Utils.hexToBytes(data.get("qinv")));
    }

    /**
     * Generates a new RSA key pair
     * @param bits The desired number of bits in the modulus
     */
    public RsaPrivateKey(int bits) {
        this.e = new BigInt(65537);

        do {
            this.p = MathUtils.generateProbablePrime(bits / 2);
            this.q = MathUtils.generateProbablePrime(bits / 2);

            this.n = this.p.multiply(q);
            this.phi_n = this.p.subtract(1).multiply(this.q.subtract(1));
        } while (!MathUtils.gcd(this.e, this.phi_n).equals(0));

        this.d = this.e.modInverse(this.phi_n);
        this.dp = this.d.mod(this.p.subtract(1));
        this.dq = this.d.mod(this.q.subtract(1));
        this.qinv = this.q.modInverse(this.p);
    }

    /**
     * Computes the value of (val ^ d) mod n
     */
    public BigInt privateExponentation(BigInt val) {
        return val.powMod(this.d, this.n);
    }

    /**
     * Efficiently computes the value of (val ^ d) mod n
     */
    public BigInt fastPrivateExponentation(BigInt val) {
        BigInt m1 = val.powMod(this.dp, this.p);
        BigInt m2 = val.powMod(this.dq, this.q);

        BigInt h = this.qinv.multiply(m1.subtract(m2)).mod(this.p);

        return m2.add(h.multiply(this.q));
    }

    /**
     * Exports this key to a map
     */
    @Override
    public Map<String, String> exportToMap() {
        Map<String, String> data = super.exportToMap();

        data.put("p", Utils.bytesToHex(this.p.exportToByteArray()));
        data.put("q", Utils.bytesToHex(this.q.exportToByteArray()));
        data.put("phi_n", Utils.bytesToHex(this.phi_n.exportToByteArray()));
        data.put("d", Utils.bytesToHex(this.d.exportToByteArray()));
        data.put("dp", Utils.bytesToHex(this.dp.exportToByteArray()));
        data.put("dq", Utils.bytesToHex(this.dq.exportToByteArray()));
        data.put("qinv", Utils.bytesToHex(this.qinv.exportToByteArray()));

        return data;
    }

    /**
     * Exports the public key information for this key
     */
    public RsaKey exportPublicKey() {
        return new RsaKey(this.n, this.e);
    }

    /**
     * Creates a new RSA public key from the given JSON string
     * @throws Exception
     */
    public static RsaPrivateKey importFromJson(String json) throws Exception {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String,String>>(){}.getType();

        Map<String, String> data = gson.fromJson(json, type);

        return new RsaPrivateKey(data);
    }
}
