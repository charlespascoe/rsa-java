package uk.co.cpascoe.rsa;

import java.util.Random;

public abstract class MathUtils {
    public static BigInt gcd(BigInt a, BigInt b) {
        if (a.equals(new BigInt(0))) return b;
        if (b.equals(new BigInt(0))) return a;
        if (a.greaterThan(b)) return MathUtils.gcd(a.mod(b), b);
        return MathUtils.gcd(a, b.mod(a));
    }

    public static BigInt randomBigInt(int bits, Random random) {
        byte[] bytes = new byte[(bits + 7) / 8];

        random.nextBytes(bytes);

        int remainingBits = bits % 8;

        if (remainingBits > 0) {
            // If the number of bits is not a multple of 8,
            // then mask and remove the highest bits of the highest-order byte that are not required
            bytes[bytes.length - 1] = (byte)(bytes[bytes.length - 1] & (255 >> (8 - remainingBits)));
        }

        return new BigInt(bytes);
    }

    public static BigInt randomBigInt(BigInt lowerLimit, BigInt upperLimit, Random random) {
        BigInt x;

        do {
            x = MathUtils.randomBigInt(upperLimit.bitCount(), random);
        } while (x.lessThan(lowerLimit) || x.greaterThan(upperLimit));

        return x;
    }

    public static BigInt generateProbablePrime(int bits) {
        return null;
    }

}

