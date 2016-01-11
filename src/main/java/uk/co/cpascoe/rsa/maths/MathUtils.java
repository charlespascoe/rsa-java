package uk.co.cpascoe.rsa.maths;

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

        int mask = 0xFF;

        for (int i = 3; i >= 0; i--) {
            data[i] = (byte)(mask & value);
            value = value >> 8;
        }

        return data;
    }

    /**
     * Returns the unsigned representation of the given signed byte
     */
    public static int unsignedByte(byte value) {
        return value & 0xFF;
    }

    /**
     * Returns the unsigned representation of the given signed int
     */
    public static long unsignedInt(int value) {
        return value & Constants.UNSIGNED_INT_MASK;
    }

    /**
     * Returns the integer represented by the given big-endian byte array
     * @throws Error If the data does not have a length of 4
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
     * Returns the integer represented by the given little-endian byte array
     * @throws Error If the data does not have a length of 4
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

    /**
     * Compares two signed integers as if they where unsigned integers
     */
    public static int unsignedIntCompare(int a, int b) {
        return Long.compare(MathUtils.unsignedInt(a), MathUtils.unsignedInt(b));
    }

    /**
     * Computes the greatest common divisor of a and b
     */
    public static BigInt gcd(BigInt a, BigInt b) {
        if (a.equals(0)) return b;
        if (b.equals(0)) return a;

        if (a.getBitAt(0) == 0) {
            // a is even

            if (b.getBitAt(0) == 0) {
                return MathUtils.gcd(a.shiftBits(-1), b.shiftBits(-1)).shiftBits(1);
            } else {
                return MathUtils.gcd(a.shiftBits(-1), b);
            }
        } else {
            // a is odd

            if (b.getBitAt(0) == 0) {
                return MathUtils.gcd(a, b.shiftBits(-1));
            } else {
                if (a.greaterThan(b)) {
                    return MathUtils.gcd(a.subtract(b).shiftBits(-1), b);
                } else {
                    return MathUtils.gcd(a, b.subtract(a).shiftBits(-1));
                }
            }
        }
    }

    /**
     * Generates a random BigInt of the given number of bits
     * @param bits The maximum number of bits that the BigInt will have
     * @param random The source of randomness
     */
    public static BigInt randomBigInt(int bits, Random random) {
        byte[] bytes = new byte[MathUtils.divCeil(bits, 8)];

        random.nextBytes(bytes);

        int remainingBits = bits % 8;

        if (remainingBits > 0) {
            // If the number of bits is not a multple of 8,
            // then mask and remove the highest bits of the highest-order byte that are not required
            bytes[bytes.length - 1] = (byte)(bytes[bytes.length - 1] & (0xFF >> (8 - remainingBits)));
        }

        return new BigInt(bytes);
    }

    /**
     * Generates a random BigInt between and including lower and upper limits
     */
    public static BigInt randomBigInt(BigInt lowerLimit, BigInt upperLimit, Random random) {
        BigInt x;

        do {
            x = MathUtils.randomBigInt(upperLimit.bitCount(), random);
        } while (x.lessThan(lowerLimit) || x.greaterThan(upperLimit));

        return x;
    }

    /**
     * Generates a random number which probably prime
     * @param bits The number of bits the number will have (i.e. the number will not be less than 2^bits, and not greater than 2^(bits + 1) - 1)
     */
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

