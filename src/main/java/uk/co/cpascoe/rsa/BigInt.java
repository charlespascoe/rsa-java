package uk.co.cpascoe.rsa;

import java.util.Random;
import java.security.SecureRandom;
import java.util.Arrays;
import java.lang.Math;

public class BigInt implements Comparable<BigInt> {
    public static class DivisionResult {
        private final BigInt quotient;
        private final BigInt remainder;

        public DivisionResult(BigInt q, BigInt r) {
            this.quotient = q;
            this.remainder = r;
        }

        public BigInt quotient() { return this.quotient; }
        public BigInt remainder() { return this.remainder; }
    }

    private int[] digits;
    private BigInt[] powerOf2Multiples = null;

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
            this.digits[i] = MathUtils.littleEndianBytesToInt(Arrays.copyOfRange(data, i * 4, (i + 1) * 4));
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
        for (int i = this.digits.length - 1; i >= 0; i--) {
            if (this.digits[i] != 0) {
                return i + 1;
            }
        }

        return 1;
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

    public int getBitAt(int bitIndex) {
        int digitIndex = bitIndex / 32;
        return (this.getDigit(digitIndex) & (1 << (bitIndex % 32))) == 0 ? 0 : 1;
    }

    public void setBitAt(int value, int bitIndex) {
        if (value != 0) value = 1;

        int digit = this.getDigit(bitIndex / 32);
        int bit = (digit & Constants.BIT_MASKS[bitIndex % 32]) == 0 ? 0 : 1;

        if (bit != value) {
            this.setDigit(digit ^ Constants.BIT_MASKS[bitIndex % 32], bitIndex / 32);
        }
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

    public BigInt shiftDigits(int shift) {
        if (this.digitCount() + shift <= 0) return new BigInt(0);

        int[] newDigits = new int[this.digitCount() + shift];

        if (shift >= 0) {
            for (int i = 0; i < this.digitCount(); i++) {
                newDigits[i + shift] = this.getDigit(i);
            }
        } else {
            for (int i = 0; i < newDigits.length; i++) {
                newDigits[i] = this.getDigit(i - shift);
            }
        }

        return new BigInt(newDigits);
    }

    public void addToDigit(int digit, int index) {
        boolean carry = false;
        long unsignedDigit = MathUtils.unsignedInt(digit);
        long unsignedCurrentDigit = MathUtils.unsignedInt(this.getDigit(index));
        long sum = unsignedDigit + unsignedCurrentDigit;

        if (sum >= Constants.TWO_POW_32) {
            sum %= Constants.TWO_POW_32;
            carry = true;
        }

        this.setDigit((int)sum, index);

        if (carry) this.addToDigit(1, index + 1);
    }

    public void subtractFromDigit(int digit, int index) {
        // N.B.: This will run forever if the digit to subtract is larger than the digit at this position
        // and this digit is the highest-order digit
        boolean carry = false;
        long unsignedDigit = MathUtils.unsignedInt(digit);
        long unsignedCurrentDigit = MathUtils.unsignedInt(this.getDigit(index));
        long diff = unsignedCurrentDigit - unsignedDigit;

        if (diff < 0) {
            diff += Constants.TWO_POW_32;
            carry = true;
        }

        this.setDigit((int)diff, index);

        if (carry) this.subtractFromDigit(1, index + 1);
    }

    public BigInt add(BigInt other) {
        BigInt result = new BigInt(this.digits);

        for (int i = 0; i < other.digitCount(); i++) {
            result.addToDigit(other.getDigit(i), i);
        }

        return result;
    }

    public BigInt subtract(BigInt other) {
        if (this.lessThan(other)) throw new Error("Cannot subtract a number from a smaller number (yet)");

        BigInt result = new BigInt(this.digits);

        for (int i = 0; i < other.digitCount(); i++) {
            result.subtractFromDigit(other.getDigit(i), i);
        }

        return result;
    }

    public BigInt multiply(BigInt other) {
        BigInt result = new BigInt(0);

        long intMask = Constants.TWO_POW_32 - 1;

        for (int i = 0; i < this.digitCount(); i++) {
            for (int j = 0; j < other.digitCount(); j++) {
                long product = MathUtils.unsignedInt(this.getDigit(i)) * MathUtils.unsignedInt(other.getDigit(j));

                int digit1 = (int)(product & intMask);
                int digit2 = (int)((product >> 32) & intMask);

                result.addToDigit(digit1, i + j);

                if (digit2 != 0)
                    result.addToDigit(digit2, i + j + 1);
            }
        }

        return result;
    }

    public BigInt[] getPowerOf2Multiples(int n) {
        if (this.powerOf2Multiples == null) {
            this.powerOf2Multiples = new BigInt[] {this};
        }

        if (n > this.powerOf2Multiples.length) {
            int prevLength = this.powerOf2Multiples.length;
            this.powerOf2Multiples = Arrays.copyOf(this.powerOf2Multiples, n);

            for (int i = prevLength; i < this.powerOf2Multiples.length; i++) {
                this.powerOf2Multiples[i] = this.powerOf2Multiples[i - 1].multiply(new BigInt(2));
            }
        }

        return Arrays.copyOf(this.powerOf2Multiples, n);
    }

    public BigInt.DivisionResult divide(BigInt divisor) {
        if (this.lessThan(divisor)) return new BigInt.DivisionResult(new BigInt(0), this);

        BigInt remainder = new BigInt(this.exportToIntArray());
        BigInt quotient = new BigInt(0);

        BigInt[] pow2Multiples = divisor.getPowerOf2Multiples(this.bitCount() - divisor.bitCount() + 1);

        for (int i = pow2Multiples.length - 1; i >= 0; i--) {
            if (remainder.greaterThanOrEqual(pow2Multiples[i])) {
                remainder = remainder.subtract(pow2Multiples[i]);
                quotient.setBitAt(1, i);
            }
        }

        return new BigInt.DivisionResult(quotient, remainder);
    }

    public BigInt quotient(BigInt divisor) {
        return this.divide(divisor).quotient();
    }

    public BigInt mod(BigInt modulus) {
        return this.divide(modulus).remainder();
    }

    public BigInt pow(BigInt exponent) {
        BigInt result = new BigInt(1);
        BigInt base = this;

        for (int i = 0; i < exponent.bitCount(); i++) {
            if (exponent.getBitAt(i) == 1) {
                result = result.multiply(base);
            }
            base = base.multiply(base);
        }

        return result;
    }

    public BigInt powMod(BigInt exponent, BigInt modulus) {
        BigInt result = new BigInt(1);
        BigInt base = this;

        for (int i = 0; i < exponent.bitCount(); i++) {
            if (exponent.getBitAt(i) == 1) {
                result = result.multiply(base).mod(modulus);
            }
            base = base.multiply(base).mod(modulus);
        }

        return result;
    }

    public boolean isProbablePrime(int certainty) {
        return false;

    }

    @Override
    public String toString() {
        return "0";
    }

    public int compareTo(BigInt other) {
        int diff = this.bitCount() - other.bitCount();

        if (diff != 0) return diff;

        for (int i = this.digitCount() - 1; i >= 0; i--) {
            diff = MathUtils.unsignedIntCompare(this.getDigit(i), other.getDigit(i));
            if (diff != 0) return diff;
        }

        return 0;
    }

    public boolean greaterThan(BigInt other) {
        return this.compareTo(other) > 0;
    }

    public boolean greaterThanOrEqual(BigInt other) {
        return this.compareTo(other) >= 0;
    }

    public boolean lessThan(BigInt other) {
        return this.compareTo(other) < 0;
    }

    public boolean lessThanOrEqual(BigInt other) {
        return this.compareTo(other) <= 0;
    }

    public boolean equals(BigInt other) {
        return this.compareTo(other) == 0;
    }

    public int[] exportToIntArray() {
        return Arrays.copyOf(this.digits, this.digitCount());
    }
}

