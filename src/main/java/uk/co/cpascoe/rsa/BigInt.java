package uk.co.cpascoe.rsa;

import java.util.Random;
import java.security.SecureRandom;
import java.util.Arrays;

public class BigInt implements Comparable<BigInt> {
    private int[] num;

    public BigInt(int value) {
        this.num = new int[] {value};
    }

    /**
     * Creates new new instance of a BigInt using the given little-endian byte array
     */
    public BigInt(byte[] data) {
        int digit = 0;

        this.num = new int[(data.length + 3) / 4];

        for (int i = 0; i < this.num.length; i++) {
            this.num[i] = Utils.littleEndianBytesToInt(Arrays.copyOfRange(data, i * 4, (i + 1) * 4));
        }
    }

    public BigInt(String value) {
        this(value, 10);
    }

    public BigInt(String value, int base) {
    }

    public BigInt(int numBits, Random random) {
    }

    public int bitCount() {
        return 0;
    }

    public int getLowestSetBit() {
        return 0;
    }

    public BigInt add(BigInt y) {
        return null;
    }

    public BigInt subtract(BigInt y) {
        return null;
    }

    public BigInt multiply(BigInt y) {
        return null;
    }

    public BigInt divide(BigInt y) {
        return null;
    }

    public BigInt mod(BigInt y) {
        return null;
    }

    public BigInt pow(BigInt y) {
        return null;
    }

    public BigInt powMod(BigInt y) {
        return null;
    }

    public BigInt gcd(BigInt y) {
        return null;
    }

    public static BigInt randomBigInt(int bits, Random random) {
        return null;
    }

    public static BigInt randomBigInt(BigInt lowerLimit, BigInt upperLimit, Random random) {
        BigInt x;

        do {
            x = BigInt.randomBigInt(upperLimit.bitCount(), random);
        } while (x.compareTo(lowerLimit) < 0 || x.compareTo(upperLimit) > 0);

        return x;
    }

    public static BigInt generateProbablePrime(int bits) {
        return null;
    }

    public boolean isProbablePrime(int certainty) {
        return false;

    }

    @Override
    public String toString() {
        return "0";
    }

    public int compareTo(BigInt other) {
        return 0;
    }

    public int[] exportToIntArray() {
        return Arrays.copyOf(this.num, this.num.length);
    }
}

