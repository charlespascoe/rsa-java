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

    /**
     * The array of base 2^32 digits that represents the value of this BigInt
     */
    private int[] digits;

    /**
     * The memoised products of this BigInt and increasing powers of 2, for binary long division
     */
    private BigInt[] powerOf2Multiples = null;

    /**
     * Creates a new instance of a BigInt using the given int - the sign will be ignored
     */
    public BigInt(int value) {
        if (value < 0) value = -value;

        this.digits = new int[] {value};
    }

    /**
     * Creates a new instance of a BigInt using the given little-endian digit array
     */
    public BigInt(int[] digits) {
        this.digits = Arrays.copyOf(digits, digits.length);
    }

    /**
     * Creates a new instance of a BigInt using the given little-endian byte array
     */
    public BigInt(byte[] data) {
        int digit = 0;

        this.digits = new int[MathUtils.divCeil(data.length, 4)];

        for (int i = 0; i < this.digits.length; i++) {
            this.digits[i] = MathUtils.littleEndianBytesToInt(Arrays.copyOfRange(data, i * 4, (i + 1) * 4));
        }
    }

    /**
     * Returns the number of digits (i.e. the position of the highest-order non-zero digit, plus 1)
     */
    public int digitCount() {
        for (int i = this.digits.length - 1; i >= 0; i--) {
            if (this.digits[i] != 0) {
                return i + 1;
            }
        }

        return 1;
    }

    /**
     * Returns the number of bytes needed to represent this number
     */
    public int byteCount() {
        return MathUtils.divCeil(this.bitCount(), 8);
    }

    /**
     * Returns the number of bits (i.e. the position of the highest-order non-zero bit, plus 1)
     */
    public int bitCount() {
        for (int i = (this.digits.length * 32) - 1; i >= 0; i--) {
            if (this.getBitAt(i) > 0) {
                return i + 1;
            }
        }

        return 0;
    }

    /**
     * Returns the index of lowest non-zero bit, or -1 if there are no set bits
     */
    public int getLowestSetBit() {
        for (int i = 0; i < (this.digits.length * 32); i++) {
            if (this.getBitAt(i) > 0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the bit (0 or 1) at the specified zero-based index
     */
    public int getBitAt(int bitIndex) {
        int digitIndex = bitIndex / 32;
        return (this.getDigit(digitIndex) & (1 << (bitIndex % 32))) == 0 ? 0 : 1;
    }


    /**
     * Sets the bit at the specified index
     * @param value The value of the bit - any non-zero value is assumed to be 1
     * @param bitIndex The zero-based index of the bit to set
     */
    protected void setBitAt(int value, int bitIndex) {
        if (value != 0) value = 1;

        int digit = this.getDigit(bitIndex / 32);
        int bit = (digit & Constants.BIT_MASKS[bitIndex % 32]) == 0 ? 0 : 1;

        if (bit != value) {
            this.setDigit(digit ^ Constants.BIT_MASKS[bitIndex % 32], bitIndex / 32);
        }
    }

    /**
     * Moves all bits in this BigInt up by 1 bit position
     */
    protected void shiftBitsUp() {
        boolean carry = false;
        boolean prevCarry = false;

        int digitCount = this.digitCount();

        for (int i = 0; i < digitCount; i++) {
            int digit = this.getDigit(i);

            carry = (digit & Constants.BIT_MASKS[31]) != 0;

            digit <<= 1;

            if (prevCarry) digit++;

            this.setDigit(digit, i);

            prevCarry = carry;
        }

        if (carry) {
            this.setDigit(1, digitCount);
        }
    }

    /**
     * Moves all bits in this BigInt down by 1 bit position
     */
    protected void shiftBitsDown() {
        boolean carry = false;
        boolean prevCarry = false;

        int digitCount = this.digitCount();

        for (int i = digitCount - 1; i >= 0; i--) {
            int digit = this.getDigit(i);

            carry = (digit & 1) != 0;

            digit >>>= 1;

            if (prevCarry) digit += Constants.BIT_MASKS[31];

            this.setDigit(digit, i);

            prevCarry = carry;
        }
    }

    /**
     * Returns a copy of this value, where the bits have been shifted by the given number of bits
     * @param bits The number of bits to move - positive moves bits up, negative down, and 0 is unchanged
     */
    public BigInt shiftBits(int bits) {
        BigInt result = new BigInt(this.digits);

        if (bits / 32 != 0)
            result = result.shiftDigits(bits / 32);

        // N.B.: in Java, -33 mod 32 = -1
        bits = bits % 32;

        if (bits >= 0) {
            for (int i = 0; i < bits; i++) {
                result.shiftBitsUp();
            }
        } else {
            bits = -bits;
            for (int i = 0; i < bits; i++) {
                result.shiftBitsDown();
            }
        }

        return result;
    }

    /**
     * Masks off the lowest-order bits of this BigInt
     * @param bits The number of bits to preserve; for example, 4 will keep the lowest 4 bits as they are and set all other bits to 0
     */
    protected BigInt maskLowerBits(int bits) {
        int[] newDigits = Arrays.copyOf(this.digits, MathUtils.divCeil(bits, 32));

        if (bits % 32 != 0) {
            // If the mask does not align with a digit,
            // then mask off the last bits of the last digit that are not needed
            newDigits[newDigits.length - 1] &= 0xFFFFFFFF >>> (32 - (bits % 32));
        }

        return new BigInt(newDigits);
    }

    /**
     * Gets the base 2^32 digit at the specified index
     * @param index The index of the digit - an out-of-range index will return 0
     */
    public int getDigit(int index) {
        if (index >= 0 && index < this.digits.length) {
            return this.digits[index];
        }

        return 0;
    }

    /**
     * Sets the digit at the specified index
     * @param digit The base 2^32 digit
     * @param index The index of the digit - less than 0 will be ignored, greater than the digit count is automatically handled
     */
    protected void setDigit(int digit, int index) {
        if (index < 0) return;
        if (index >= this.digits.length) {
            this.digits = Arrays.copyOf(this.digits, index + 1);
        }

        this.digits[index] = digit;
    }

    /**
     * Shifts all digits along, ignoring any fractional components
     * @param shift The number of digits to move along (like multiplying by base^shift)
     */
    protected BigInt shiftDigits(int shift) {
        if (shift == 0) return new BigInt(this.digits);
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

    /**
     * Adds the given digit to the digit at ths specified index (handles carry)
     * @param digit The base 2^32 digit to add
     * @param index The index of the digit to add to
     */
    protected void addToDigit(int digit, int index) {
        boolean carry = false;
        long unsignedDigit = digit & Constants.UNSIGNED_INT_MASK;
        long unsignedCurrentDigit = this.getDigit(index) & Constants.UNSIGNED_INT_MASK;
        long sum = unsignedDigit + unsignedCurrentDigit;

        if (sum >= Constants.TWO_POW_32) {
            sum %= Constants.TWO_POW_32;
            carry = true;
        }

        this.setDigit((int)sum, index);

        if (carry) this.addToDigit(1, index + 1);
    }

    /**
     * Subtracts the given digit to the digit at ths specified index (handles carry)
     * @param digit The base 2^32 digit to subtract
     * @param index The index of the digit to subtract from
     */
    protected void subtractFromDigit(int digit, int index) {
        // N.B.: This will run forever if the digit to subtract is larger than the digit at this position
        // and this digit is the highest-order digit
        boolean carry = false;
        long unsignedDigit = digit & Constants.UNSIGNED_INT_MASK;
        long unsignedCurrentDigit = this.getDigit(index) & Constants.UNSIGNED_INT_MASK;
        long diff = unsignedCurrentDigit - unsignedDigit;

        if (diff < 0) {
            diff += Constants.TWO_POW_32;
            carry = true;
        }

        this.setDigit((int)diff, index);

        if (carry) this.subtractFromDigit(1, index + 1);
    }

    /**
     * Returns the result of the addition of this BigInt and the given BigInt
     */
    public BigInt add(BigInt other) {
        BigInt result = new BigInt(this.digits);

        for (int i = 0; i < other.digitCount(); i++) {
            result.addToDigit(other.getDigit(i), i);
        }

        return result;
    }

    /**
     * Returns the result of the addition of this BigInt and the given int
     * @param other The value to add to this BigInt - negative numbers will subtract
     */
    public BigInt add(int other) {
        BigInt result = new BigInt(this.digits);

        if (other >= 0) {
            result.addToDigit(other, 0);
        } else {
            result.subtractFromDigit(-other, 0);
        }

        return result;
    }

    /**
     * Returns the result of this BigInt minus the given BigInt
     * @throws Error If the other BigInt is larger than this BigInt
     */
    public BigInt subtract(BigInt other) {
        if (this.lessThan(other)) throw new Error("Cannot subtract a number from a smaller number (yet)");

        BigInt result = new BigInt(this.digits);

        for (int i = 0; i < other.digitCount(); i++) {
            result.subtractFromDigit(other.getDigit(i), i);
        }

        return result;
    }

    /**
     * Returns the result of this BigInt minus the given int
     * @param other The value to subtract from this BigInt - negative numbers will add
     */
    public BigInt subtract(int other) {
        BigInt result = new BigInt(this.digits);

        if (other >= 0) {
            result.subtractFromDigit(other, 0);
        } else {
            result.addToDigit(-other, 0);
        }

        return result;
    }

    public BigInt subtractMod(BigInt other, BigInt modulus) {
        if (this.greaterThanOrEqual(other)) return this.subtract(other);
        return modulus.subtract(other.subtract(this).mod(modulus));
    }

    /**
     * Returns the result of the multiplication of this BigInt and the given BigInt
     */
    public BigInt multiply(BigInt other) {
        if (this.equals(other)) {
            return this.square();
        } else {
            BigInt result = new BigInt(0);

            long intMask = Constants.TWO_POW_32 - 1;

            for (int i = 0; i < this.digitCount(); i++) {
                long thisDigit = this.getDigit(i) & Constants.UNSIGNED_INT_MASK;
                for (int j = 0; j < other.digitCount(); j++) {
                    long product = thisDigit * (other.getDigit(j) & Constants.UNSIGNED_INT_MASK);
                    if (product == 0) continue;

                    int digit1 = (int)(product & intMask);
                    int digit2 = (int)(product >>> 32);

                    result.addToDigit(digit1, i + j);

                    if (digit2 != 0)
                        result.addToDigit(digit2, i + j + 1);
                }
            }

            return result;
        }
    }

    public BigInt square() {
        BigInt result = new BigInt(0);

        long intMask = Constants.TWO_POW_32 - 1;

        for (int i = 0; i < this.digitCount(); i++) {
            long thisDigit = this.getDigit(i) & Constants.UNSIGNED_INT_MASK;
            for (int j = 0; j < i; j++) {
                long product = thisDigit * (this.getDigit(j) & Constants.UNSIGNED_INT_MASK);
                if (product == 0) continue;

                int digit1 = (int)(product & intMask);
                int digit2 = (int)(product >>> 32);

                result.addToDigit(digit1, i + j);

                if (digit2 != 0)
                    result.addToDigit(digit2, i + j + 1);
            }
        }

        result.shiftBitsUp();

        for (int i = 0; i < this.digitCount(); i++) {
            long product = this.getDigit(i) & Constants.UNSIGNED_INT_MASK;

            if (product == 0) continue;

            product = product * product;

            int digit1 = (int)(product & intMask);
            int digit2 = (int)(product >>> 32);

            result.addToDigit(digit1, 2 * i);

            if (digit2 != 0)
                result.addToDigit(digit2, 2 * i + 1);
        }

        return result;
    }

    protected BigInt[] getPowerOf2Multiples(int n) {
        if (this.powerOf2Multiples == null) {
            this.powerOf2Multiples = new BigInt[] {this};
        }

        if (n > this.powerOf2Multiples.length) {
            int prevLength = this.powerOf2Multiples.length;
            this.powerOf2Multiples = Arrays.copyOf(this.powerOf2Multiples, n);

            for (int i = prevLength; i < this.powerOf2Multiples.length; i++) {
                this.powerOf2Multiples[i] = new BigInt(this.powerOf2Multiples[i - 1].exportToIntArray());
                this.powerOf2Multiples[i].shiftBitsUp();
            }
        }

        return Arrays.copyOf(this.powerOf2Multiples, n);
    }

    /**
     * Returns the DivisionResult (quotient and remainder) of this BigInt divided by the given BigInt divisor
     */
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

    /**
     * Returns the quotient of this BigInt divided by the given BigInt divisor
     */
    public BigInt quotient(BigInt divisor) {
        return this.divide(divisor).quotient();
    }

    /**
     * Returns the remainder of this BigInt modulo the given BigInt modulus
     */
    public BigInt mod(BigInt modulus) {
        return this.divide(modulus).remainder();
    }

    /**
     * Returns the modular multiplicative inverse of this BigInt modulo the given BigInt modulus
     */
    public BigInt modInverse(BigInt modulus) {
        BigInt t = new BigInt(0);
        BigInt r = modulus;

        BigInt nextT = new BigInt(1);
        BigInt nextR = this;

        // Intermediate variable for swapping variables
        BigInt tmp;

        while (!nextR.equals(0)) {
            BigInt.DivisionResult divResult = r.divide(nextR);

            tmp = nextT;
            nextT = t.subtractMod(divResult.quotient().multiply(nextT), modulus);
            t = tmp;

            r = nextR;
            nextR = divResult.remainder();
        }

        // r > 1 => No inverse
        if (r.greaterThan(new BigInt(1))) return null;

        return t;
    }

    /**
     * Computes the value of this BigInt raised to the power of the given BigInt exponent
     */
    public BigInt pow(BigInt exponent) {
        BigInt result = new BigInt(1);
        BigInt base = this;

        for (int i = 0; i < exponent.bitCount(); i++) {
            if (exponent.getBitAt(i) == 1) {
                result = result.multiply(base);
            }
            base = base.square();
        }

        return result;
    }

    /**
     * Computes the value of this BigInt raised to the power of the given BigInt exponent, modulo the given BigInt modulus
     * @return The value of (this ^ exponent) mod modulus
     */
    public BigInt powMod(BigInt exponent, BigInt modulus) {
        if (modulus.getBitAt(0) == 1)
            return this.montgomeryPowMod(exponent, modulus);

        BigInt result = new BigInt(1);
        BigInt base = this;

        for (int i = 0; i < exponent.bitCount(); i++) {
            if (exponent.getBitAt(i) == 1) {
                result = result.multiply(base).mod(modulus);
            }
            base = base.square().mod(modulus);
        }

        return result;
    }

    /**
     * Computes the value of this BigInt raised to the power of the given BigInt exponent, modulo the given BigInt modulus, using the Montgomery Multiplication method
     *
     * @param modulus The modulus, which must be odd
     * @return The value of (this ^ exponent) mod modulus
     *
     * @throws Error If the modulus is not odd
     */
    protected BigInt montgomeryPowMod(BigInt exponent, BigInt modulus) {
        if (modulus.getBitAt(0) != 1) throw new Error("Modulus must be odd for Montgomery Exponentation");

        // r = 2^k and m mod 2 = 1, therefore gcd(r, m) = 1
        int k = modulus.bitCount();
        BigInt r = new BigInt(1).shiftBits(k);

        BigInt rInverse = r.modInverse(modulus);
        BigInt modulusDash = r.subtract(modulus.modInverse(r));

        // baseR = base * r mod modulus ('this' is the base)
        BigInt baseR = this.shiftBits(k).mod(modulus);
        // resultR = result * r (result initially 1)
        BigInt resultR = r.mod(modulus);

        for (int i = 0; i < exponent.bitCount(); i++) {
            if (exponent.getBitAt(i) == 1) {
                resultR = BigInt.montgomeryMultiplication(resultR, baseR, modulus, modulusDash, k);
            }

            baseR = BigInt.montgomeryMultiplication(baseR, baseR, modulus, modulusDash, k);
        }

        // result * r * r^-1 mod modulus = result
        return resultR.multiply(rInverse).mod(modulus);
    }

    /**
     * Performs Montgomery multiplication using the given values
     *
     * @param arModm The Montgomery form of a, which is (a * r) mod m
     * @param brModm The Montgomery form of b, which is (b * r) mod m
     * @param m The modulus
     * @param mDash The the inverse of m that satisfies (m * mDash) mod r = r - 1
     * @param k The number of bits in r, i.e. r = 2^k
     *
     * @return The Montgomery form of (a * b), which is (a * b * r) mod m
     */
    protected static BigInt montgomeryMultiplication(BigInt arModm, BigInt brModm, BigInt m, BigInt mDash, int k) {
        BigInt t = arModm.multiply(brModm);

        BigInt u = t.add(t.multiply(mDash).maskLowerBits(k).multiply(m)).shiftBits(-k);

        if (u.greaterThanOrEqual(m)) u = u.subtract(m);

        return u;
    }

    /**
     * Uses the Miller-Rabin primality test to check whether or not this number is probably prime with 10 trials
     * @return True if probably prime, false if definitely not prime
     */
    public boolean isProbablePrime() {
        return this.isProbablePrime(10);
    }

    /**
     * Uses the Miller-Rabin primality test to check whether or not this number is probably prime
     * @param certainty The number of trials (more trials, more certainty)
     * @return True if probably prime, false if definitely not prime
     */
    public boolean isProbablePrime(int certainty) {
        for (int i = 0; i < 256; i++) {
            BigInt p = Constants.SMALL_PRIMES[i];

            if (p.greaterThanOrEqual(this)) break;

            if (MathUtils.gcd(this, p).equals(p)) {
                return false;
            }
        }

        BigInt nMinusOne = this.subtract(1);

        // n - 1 = 2^s * d
        int s = nMinusOne.getLowestSetBit();
        BigInt d = nMinusOne.quotient(new BigInt(2).pow(new BigInt(s)));

        Random r = new SecureRandom();

        for (int i = 0; i < certainty; i++) {
            BigInt a = MathUtils.randomBigInt(new BigInt(2), this.subtract(2), r);

            BigInt x = a.powMod(d, this);

            if (!x.equals(1) && !x.equals(nMinusOne)) {
                boolean solutionFound = false;

                for (int j = 0; j < s; j++) {
                    x = x.multiply(x).mod(this);

                    if (x.equals(1)) {
                        return false;
                    }

                    if (x.equals(nMinusOne)) {
                        solutionFound = true;
                        break;
                    }
                }

                if (!solutionFound) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Compares this BigInt with another BigInt, to compare values
     *
     * @return A number less than 0 if this BigInt is less than the provided one, a number greater than 0 if this BigInt is greater than the provided one, or 0 if they are equal
     */
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

    public boolean equals(int other) {
        return other >= 0 && this.digitCount() == 1 && this.digits[0] == other;
    }

    /**
     * Returns a little-endian array of integers that represent this BigInt
     */
    public int[] exportToIntArray() {
        // Copying array prevents external modification
        // and reduces it to the minimum length without any leading high-order zeros
        return Arrays.copyOf(this.digits, this.digitCount());
    }

    /**
     * Returns a little-endian array of bytes that represent this BigInt
     */
    public byte[] exportToByteArray() {
        int [] digits = this.exportToIntArray();
        byte[] output = new byte[digits.length * 4];

        for (int i = 0; i < digits.length; i++) {
            for (int j = 0; j < 4; j++) {
                output[4*i + j] = (byte)((digits[i] >> (8 * j)) & 255);
            }
        }

        // Resize it to remove any leading high-order zeros
        return Arrays.copyOf(output, this.byteCount());
    }
}

