package uk.co.cpascoe.rsa;

import java.math.BigInteger;
import java.util.Random;

public class RsaPrivateKey extends RsaKey {
    protected BigInteger p;
    protected BigInteger q;
    protected BigInteger phi_n;
    protected BigInteger d;

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
    }

    public BitInteger privateExponentation(BigInteger val) {
        return val.modPow(this.d, this.n);
    }
}
