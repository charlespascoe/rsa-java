package uk.co.cpascoe.rsa;

import java.util.Random;
import java.security.SecureRandom;

public abstract class MathUtils {
    /**
     * Returns the value of Ceiling(x / divisor)
     */
    public static int divCeil(int x, int divisor) {
        return (x + divisor - 1) / divisor;
    }

    /**
     * Returns the big-endian byte representation of the integer
     *
     * @param value The value to convert
     *
     * @return A byte array of length 4
     */
    public static byte[] intToBigEndianBytes(int value) {
        byte[] data = new byte[4];

        int mask = 255;

        for (int i = 3; i >= 0; i--) {
            data[i] = (byte)(mask & value);
            value = value >> 8;
        }

        return data;
    }

    public static int unsignedByte(byte value) {
        return value < 0 ? value + 256 : value;
    }

    public static long unsignedInt(int value) {
        return value < 0 ? value + Constants.TWO_POW_32 : value;
    }

    /**
     * Returns the integer represented by the big-endian byte array passed in
     */
    public static int bigEndianBytesToInt(byte[] data) {
        if (data.length != 4) {
            throw new Error("data must have a length of 4");
        }

        int value = 0;

        for (int i = 0; i < 4; i++) {
            value = (value << 8) + MathUtils.unsignedByte(data[i]);
        }

        return value;
    }

    /**
     * Returns the integer represented by the big-endian byte array passed in
     */
    public static int littleEndianBytesToInt(byte[] data) {
        if (data.length != 4) {
            throw new Error("data must have a length of 4");
        }

        int value = 0;

        for (int i = 3; i >= 0; i--) {
            value = (value << 8) + MathUtils.unsignedByte(data[i]);
        }

        return value;
    }

    public static int unsignedIntCompare(int a, int b) {
        return Long.compare(MathUtils.unsignedInt(a), MathUtils.unsignedInt(b));
    }

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
        Random r = new SecureRandom();
        BigInt randomNumber;

        do {
            randomNumber = MathUtils.randomBigInt(bits, r);

            // Make sure that it has the correct number of bits
            randomNumber.setBitAt(1, bits - 1);

            // Make sure that it is odd; no prime above 2 is even
            randomNumber.setBitAt(1, 0);
        } while (!randomNumber.isProbablePrime());

        return randomNumber;

    }

}

