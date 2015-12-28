package uk.co.cpascoe.rsa;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class RsaKey {
    protected BigInt n;
    protected BigInt e;

    public RsaKey() {

    }

    public RsaKey(Map<String, String> data) throws Exception {
        if (!data.containsKey("n") || !data.containsKey("e")) {
            throw new Exception("Keys missing from data object");
        }

        if (!Utils.isValidHex(data.get("n")) || !Utils.isValidHex(data.get("e"))) {
            throw new Exception("Invalid hex values");
        }

        this.n = new BigInt(Utils.base64ToBytes(data.get("n")));
        this.e = new BigInt(Utils.base64ToBytes(data.get("e")));
    }

    public BigInt publicExponentation(BigInt val) {
        return val.powMod(this.e, this.n);
    }

    public byte[] exportDer() {
        return new byte[0];
    }

    public Map<String, String> exportToMap() {
        Map<String, String> data = new HashMap<String, String>();

        data.put("n", Utils.bytesToBase64(this.n.exportToByteArray()));
        data.put("e", Utils.bytesToBase64(this.e.exportToByteArray()));

        return data;
    }

    public String exportToJson() {
        return new Gson().toJson(this.exportToMap());
    }

    public static RsaKey importFromJson(String json) throws Exception {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String,String>>(){}.getType();

        Map<String, String> data = gson.fromJson(json, type);

        return new RsaKey(data);
    }
}
