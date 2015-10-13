package uk.co.cpascoe.rsa;

import java.math.BigInteger;

public class RsaKey {
    protected BigInteger n;
    protected BigInteger e;

    public BigInteger publicExponentation(BigInteger val) {
        return val.modPow(this.e, this.n);
    }

    public byte[] exportDer() {
        return new byte[0];
    }
}
