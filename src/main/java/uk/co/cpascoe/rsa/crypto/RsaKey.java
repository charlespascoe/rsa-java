package uk.co.cpascoe.rsa.crypto;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import uk.co.cpascoe.rsa.maths.BigInt;
import uk.co.cpascoe.rsa.Utils;

public class RsaKey {
    protected BigInt n;
    protected BigInt e;

    protected RsaKey() { }

    /**
     * Creates a new RSA public key with the given modulus and public exponent
     * @param n The modulus
     * @param e The public exponent
     */
    protected RsaKey(BigInt n, BigInt e) {
        this.n = n;
        this.e = e;
    }

    /**
     * Creates a new RSA public key from the given data
     * @param data A map containing the modulus and public exponent for the key
     * @throws Exception
     */
    protected RsaKey(Map<String, String> data) throws Exception {
        if (!data.containsKey("n") || !data.containsKey("e")) {
            throw new Exception("Keys missing from data object");
        }

        if (!Utils.isValidHex(data.get("n")) || !Utils.isValidHex(data.get("e"))) {
            throw new Exception("Invalid hex values");
        }

        this.n = new BigInt(Utils.hexToBytes(data.get("n")));
        this.e = new BigInt(Utils.hexToBytes(data.get("e")));
    }

    /**
     * The length of the modulus, in bytes
     */
    public int byteCount() {
        return this.n.byteCount();
    }

    /**
     * Computes the value of (val ^ e) mod n
     */
    public BigInt publicExponentation(BigInt val) {
        return val.powMod(this.e, this.n);
    }

    /**
     * Exports this key to a map
     */
    public Map<String, String> exportToMap() {
        Map<String, String> data = new HashMap<String, String>();

        data.put("n", Utils.bytesToHex(this.n.exportToByteArray()));
        data.put("e", Utils.bytesToHex(this.e.exportToByteArray()));

        return data;
    }

    /**
     * Exports this key to a JSON string
     */
    public String exportToJson() {
        return new Gson().toJson(this.exportToMap());
    }

    /**
     * Creates a new RSA public key from the given JSON string
     * @throws Exception
     */
    public static RsaKey importFromJson(String json) throws Exception {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String,String>>(){}.getType();

        Map<String, String> data = gson.fromJson(json, type);

        return new RsaKey(data);
    }
}
