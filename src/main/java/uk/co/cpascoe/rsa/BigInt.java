package uk.co.cpascoe.rsa;

import java.util.Random;
import java.security.SecureRandom;

public class BigInt implements Comparable<BigInt> {
    public BigInt(int value) {
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
}

