package uk.co.cpascoe.rsa;

import java.util.Random;
import java.security.SecureRandom;
import java.util.Arrays;

public class BigInt implements Comparable<BigInt> {
    private int[] digits;

    public BigInt(int value) {
        this.digits = new int[] {value};
    }

    public BigInt(int[] digits) {
        this.digits = Arrays.copyOf(digits, digits.length);
    }

    /**
     * Creates new new instance of a BigInt using the given little-endian byte array
     */
    public BigInt(byte[] data) {
        int digit = 0;

        this.digits = new int[(data.length + 3) / 4];

        for (int i = 0; i < this.digits.length; i++) {
            this.digits[i] = Utils.littleEndianBytesToInt(Arrays.copyOfRange(data, i * 4, (i + 1) * 4));
        }
    }

    public BigInt(String value) {
        this(value, 10);
    }

    public BigInt(String value, int base) {
    }

    public BigInt(int numBits, Random random) {
    }

    public int digitCount() {
        // TODO: Needs to look for largest non-zero value
        return this.digits.length;
    }

    public int bitCount() {
        for (int i = (this.digits.length * 32) - 1; i >= 0; i--) {
            if (this.getBitAt(i) > 0) {
                return i + 1;
            }
        }

        return 0;
    }

    public int getLowestSetBit() {
        for (int i = 0; i < (this.digits.length * 32); i++) {
            if (this.getBitAt(i) > 0) {
                return i;
            }
        }

        return -1;
    }

    public int getBitAt(int bit) {
        int digitIndex = bit / 32;
        int bitIndex = bit % 32;
        return (this.digits[digitIndex] & (1 << bitIndex)) == 0 ? 0 : 1;
    }

    public int getDigit(int index) {
        if (index >= 0 && index < this.digits.length) {
            return this.digits[index];
        }

        return 0;
    }

    public void setDigit(int digit, int index) {
        if (index < 0) return;
        if (index >= this.digits.length) {
            this.digits = Arrays.copyOf(this.digits, index + 1);
        }

        this.digits[index] = digit;
    }

    public void addToDigit(int digit, int index) {
        boolean carry = false;
        long unsignedDigit = Utils.unsignedInt(digit);
        long unsignedCurrentDigit = Utils.unsignedInt(this.getDigit(index));
        long sum = unsignedDigit + unsignedCurrentDigit;

        if (sum >= Constants.TWO_POW_32) {
            sum %= Constants.TWO_POW_32;
            carry = true;
        }

        this.setDigit((int)sum, index);

        if (carry) this.addToDigit(1, index + 1);
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
        return Arrays.copyOf(this.digits, this.digits.length);
    }
}

