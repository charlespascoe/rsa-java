package uk.co.cpascoe.rsa;

public abstract class MathUtils {
    public static BigInt gcd(BigInt a, BigInt b) {
        if (a.equals(new BigInt(0))) return b;
        if (b.equals(new BigInt(0))) return a;
        if (a.greaterThan(b)) return MathUtils.gcd(a.mod(b), b);
        return MathUtils.gcd(a, b.mod(a));
    }
}

