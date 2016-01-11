package uk.co.cpascoe.rsa.maths;

import org.junit.Test;
import static org.junit.Assert.*;

public class BigIntTests {
    @Test
    public void createFromInt() {
        assertArrayEquals(new int[] {0}, new BigInt(0).exportToIntArray());
        assertArrayEquals(new int[] {1}, new BigInt(1).exportToIntArray());
        assertArrayEquals(new int[] {2048}, new BigInt(2048).exportToIntArray());
        assertArrayEquals("int Constructor should ignore sign", new int[] {2048}, new BigInt(-2048).exportToIntArray());
    }

    @Test
    public void createFromIntArray() {
        assertArrayEquals(new int[] {0}, new BigInt(new int[] {0}).exportToIntArray());
        assertArrayEquals(new int[] {1}, new BigInt(new int[] {1}).exportToIntArray());
        assertArrayEquals(new int[] {256}, new BigInt(new int[] {256}).exportToIntArray());
        assertArrayEquals(new int[] {(int)4294967295L}, new BigInt(new int[] {(int)4294967295L}).exportToIntArray());
        assertArrayEquals(new int[] {0,1}, new BigInt(new int[] {0,1}).exportToIntArray());
        assertArrayEquals(new int[] {0,(int)4294967295L}, new BigInt(new int[] {0,(int)4294967295L}).exportToIntArray());
        assertArrayEquals(new int[] {1,2,3,4}, new BigInt(new int[] {1,2,3,4}).exportToIntArray());
    }

    @Test
    public void createFromByteArray() {
        assertArrayEquals(new int[] {0}, new BigInt(new byte[] {0}).exportToIntArray());
        assertArrayEquals(new int[] {1}, new BigInt(new byte[] {1,0}).exportToIntArray());
        assertArrayEquals(new int[] {256}, new BigInt(new byte[] {0,1}).exportToIntArray());
        assertArrayEquals(new int[] {(int)4294967295L}, new BigInt(new byte[] {(byte)255,(byte)255,(byte)255,(byte)255}).exportToIntArray());
        assertArrayEquals(new int[] {0,1}, new BigInt(new byte[] {0,0,0,0,1}).exportToIntArray());
        assertArrayEquals(new int[] {0,(int)4294967295L}, new BigInt(new byte[] {0,0,0,0,(byte)255,(byte)255,(byte)255,(byte)255}).exportToIntArray());
        assertArrayEquals(new int[] {1,2,3,4}, new BigInt(new byte[] {1,0,0,0,2,0,0,0,3,0,0,0,4,0,0,0}).exportToIntArray());
    }

    @Test
    public void digitCount() {
        assertEquals(1, new BigInt(0).digitCount());
        assertEquals(1, new BigInt((int)4294967295L).digitCount());
        assertEquals(2, new BigInt(new int[] {0,1}).digitCount());
        assertEquals(2, new BigInt(new int[] {0,(int)4294967295L}).digitCount());

        BigInt x = new BigInt(1);
        x.setDigit(2, 1000);
        assertEquals(1001, x.digitCount());
        x.setDigit(0, 1000);
        assertEquals(1, x.digitCount());
    }

    @Test
    public void byteCount() {
        assertEquals(0, new BigInt(0).byteCount());
        assertEquals(1, new BigInt(1).byteCount());
        assertEquals(1, new BigInt(255).byteCount());
        assertEquals(2, new BigInt(256).byteCount());
    }

    @Test
    public void bitCount() {
        assertEquals(0, new BigInt(0).bitCount());
        assertEquals(1, new BigInt(1).bitCount());
        assertEquals(2, new BigInt(2).bitCount());
        assertEquals(2, new BigInt(3).bitCount());
        assertEquals(8, new BigInt(128).bitCount());

        byte[] b = new byte[256];
        b[255] = (byte)255;
        assertEquals(2048, new BigInt(b).bitCount());
    }

    @Test
    public void getLowestSetBit() {
        assertEquals(-1, new BigInt(0).getLowestSetBit());
        assertEquals(0, new BigInt(1).getLowestSetBit());
        assertEquals(1, new BigInt(2).getLowestSetBit());
        assertEquals(0, new BigInt(3).getLowestSetBit());
        assertEquals(7, new BigInt(128).getLowestSetBit());
    }

    @Test
    public void getBitAt() {
        assertEquals(0, new BigInt(0).getBitAt(0));
        assertEquals(1, new BigInt(1).getBitAt(0));
        assertEquals(0, new BigInt(2).getBitAt(0));
        assertEquals(1, new BigInt(2).getBitAt(1));
        assertEquals(1, new BigInt(256).getBitAt(8));
        assertEquals(0, new BigInt(1).getBitAt(1000));
    }

    @Test
    public void setBitAt() {
        BigInt x = new BigInt(2);
        x.setBitAt(1, 0);
        assertArrayEquals(new int[] {3}, x.exportToIntArray());

        x.setBitAt(0, 1);
        assertArrayEquals(new int[] {1}, x.exportToIntArray());

        x.setBitAt(1, 16);
        assertArrayEquals(new int[] {65537}, x.exportToIntArray());
        x.setBitAt(1, 16);
        assertArrayEquals("Setting a set bit should not change the value", new int[] {65537}, x.exportToIntArray());
    }

    @Test
    public void shiftBitsUp() {
        BigInt x = new BigInt(1);
        x.shiftBitsUp();
        assertArrayEquals(new int[] {2}, x.exportToIntArray());
        x = new BigInt(3);
        x.shiftBitsUp();
        assertArrayEquals(new int[] {6}, x.exportToIntArray());
        x = new BigInt(new int[] {1,2,3,4});
        x.shiftBitsUp();
        assertArrayEquals(new int[] {2,4,6,8}, x.exportToIntArray());
        x = new BigInt(new int[] {(int)4294967295L});
        x.shiftBitsUp();
        assertArrayEquals(new int[] {(int)4294967294L,1}, x.exportToIntArray());
    }

    @Test
    public void shiftBitsDown() {
        BigInt x = new BigInt(1);
        x.shiftBitsDown();
        assertArrayEquals(new int[] {0}, x.exportToIntArray());
        x = new BigInt(2);
        x.shiftBitsDown();
        assertArrayEquals(new int[] {1}, x.exportToIntArray());
        x = new BigInt(new int[] {3,7});
        x.shiftBitsDown();
        assertArrayEquals(new int[] {Constants.BIT_MASKS[31] + 1, 3}, x.exportToIntArray());
        x = new BigInt(new int[] {(int)4294967295L});
        x.shiftBitsDown();
        assertArrayEquals(new int[] {Constants.BIT_MASKS[31] - 1}, x.exportToIntArray());
    }

    @Test
    public void shiftBits() {
        assertArrayEquals(new int[] {2}, new BigInt(1).shiftBits(1).exportToIntArray());
        assertArrayEquals(new int[] {8}, new BigInt(1).shiftBits(3).exportToIntArray());
        assertArrayEquals(new int[] {24}, new BigInt(3).shiftBits(3).exportToIntArray());
        assertArrayEquals(new int[] {3}, new BigInt(24).shiftBits(-3).exportToIntArray());
        assertArrayEquals(new int[] {0,6}, new BigInt(3).shiftBits(33).exportToIntArray());
        assertArrayEquals(new int[] {3}, new BigInt(new int[] {0,6}).shiftBits(-33).exportToIntArray());
    }

    @Test
    public void maskLowerBits() {
        assertArrayEquals(new int[] {0}, new BigInt(1).maskLowerBits(0).exportToIntArray());
        assertArrayEquals(new int[] {0}, new BigInt(2).maskLowerBits(1).exportToIntArray());
        assertArrayEquals(new int[] {1}, new BigInt(3).maskLowerBits(1).exportToIntArray());
        assertArrayEquals(new int[] {3}, new BigInt(7).maskLowerBits(2).exportToIntArray());
        assertArrayEquals(new int[] {123,4}, new BigInt(new int[] {123,12}).maskLowerBits(35).exportToIntArray());
    }

    @Test
    public void getDigit() {
        assertEquals("It should return 0 for an out-of-range index", 0, new BigInt(1).getDigit(1024));
        assertEquals("It should return the digit for a valid index", 1, new BigInt(1).getDigit(0));
    }

    @Test
    public void setDigit() {
        BigInt x = new BigInt(0);
        x.setDigit(123, 0);
        assertEquals(123, x.getDigit(0));

        x.setDigit(456, 1);
        assertEquals(456, x.getDigit(1));

        x.setDigit(7, 1000);
        assertEquals(7, x.getDigit(1000));
    }

    @Test
    public void shiftDigits() {
        assertArrayEquals(new int[] {0,1}, new BigInt(1).shiftDigits(1).exportToIntArray());
        assertArrayEquals(new int[] {0,0,1}, new BigInt(1).shiftDigits(2).exportToIntArray());
        assertArrayEquals(new int[] {1}, new BigInt(new int[] {0,1}).shiftDigits(-1).exportToIntArray());
        assertArrayEquals(new int[] {0,1}, new BigInt(new int[] {0,0,0,1}).shiftDigits(-2).exportToIntArray());
    }

    @Test
    public void addToDigit() {
        BigInt x = new BigInt(1);
        x.addToDigit(2, 0);
        assertEquals("Correctly increments a single digit", 3, x.getDigit(0));

        x.setDigit((int)4294967295L, 0);
        x.addToDigit(1, 0);
        assertArrayEquals("Carries 1 on overflow", new int[] {0,1}, x.exportToIntArray());
    }

    @Test
    public void subtractFromDigit() {
        BigInt x = new BigInt(3);
        x.subtractFromDigit(1, 0);
        assertArrayEquals("Correctly subtracts from a single digit", new int[] {2}, x.exportToIntArray());

        BigInt y = new BigInt(new int[] {0,0,3});
        y.subtractFromDigit(1, 1);
        assertArrayEquals("Subtracts 1 on underflow", new int[] {0,(int)4294967295L,2}, y.exportToIntArray());
    }

    @Test
    public void add() {
        BigInt x = new BigInt(1);
        BigInt y = new BigInt(2);
        BigInt z = new BigInt(new int[] {(int)4294967295L});

        assertArrayEquals("Correctly adds two single-digit BigInts", new int[] {3}, x.add(y).exportToIntArray());
        assertArrayEquals("Correctly adds two BigInts, resulting in a carry", new int[] {0,1}, x.add(z).exportToIntArray());
    }

    @Test
    public void addInt() {
        BigInt x = new BigInt(1);
        assertArrayEquals(new int[] {3}, x.add(2).exportToIntArray());
        assertArrayEquals("x should not have changed", new int[] {1}, x.exportToIntArray());

        BigInt y = new BigInt(10);
        assertArrayEquals(new int[] {4}, y.add(-6).exportToIntArray());
        assertArrayEquals("y should not have changed", new int[] {10}, y.exportToIntArray());

        BigInt z = new BigInt(new int[] {(int)4294967295L});
        assertArrayEquals("Correctly takes carry into account", new int[] {2, 1}, z.add(3).exportToIntArray());
        assertArrayEquals("z should not have changed", new int[] {(int)4294967295L}, z.exportToIntArray());
    }

    @Test
    public void subtract() {
        BigInt a = new BigInt(8);
        assertArrayEquals(new int[] {5}, a.subtract(new BigInt(3)).exportToIntArray());
        assertArrayEquals("a should not have changed", new int[] {8}, a.exportToIntArray());

        BigInt x = new BigInt(new int[] {0,10});
        BigInt y = new BigInt(new int[] {1,8});
        assertArrayEquals(new int[] {(int)4294967295L,1}, x.subtract(y).exportToIntArray());
        assertArrayEquals("x should not have changed", new int[] {0, 10}, x.exportToIntArray());
    }

    @Test
    public void subtractInt() {
        BigInt x = new BigInt(123);
        assertArrayEquals(new int[] {103}, x.subtract(20).exportToIntArray());
        assertArrayEquals("x should not have changed", new int[] {123}, x.exportToIntArray());
        assertArrayEquals(new int[] {143}, x.subtract(-20).exportToIntArray());
        assertArrayEquals("x should not have changed", new int[] {123}, x.exportToIntArray());

        BigInt y = new BigInt(new int[] {2, 1});
        assertArrayEquals("Correctly takes carry into account", new int[] {(int)4294967295L}, y.subtract(3).exportToIntArray());
        assertArrayEquals("y should not have changed", new int[] {2, 1}, y.exportToIntArray());
    }

    @Test
    public void subtractMod() {
        assertArrayEquals(new int[] {9}, new BigInt(3).subtractMod(new BigInt(24), new BigInt(10)).exportToIntArray());
        assertArrayEquals(new int[] {(int)4294967295L,(int)4294967295L}, new BigInt(3).subtractMod(new BigInt(4), new BigInt(new int[] {0,0,1})).exportToIntArray());
    }

    @Test
    public void multiply() {
        BigInt x = new BigInt(2);
        BigInt y = new BigInt(3);

        assertArrayEquals("Single-digit product should be correct", new int[] {6}, x.multiply(y).exportToIntArray());

        BigInt a = new BigInt(new int[] {0,2});
        BigInt b = new BigInt(new int[] {0,5});
        BigInt c = new BigInt(new int[] {1,2});

        assertArrayEquals("Multi-digit product should be correct", new int[] {0,0,10}, a.multiply(b).exportToIntArray());
        assertArrayEquals("Multi-digit product should be correct", new int[] {0,2,4}, a.multiply(c).exportToIntArray());
    }

    @Test
    public void square() {
        assertArrayEquals("Single-digit squaring should be correct", new int[] {144}, new BigInt(12).square().exportToIntArray());
        assertArrayEquals("Multi-digit squaring should be correct", new int[] {4,12,25,24,16}, new BigInt(new int[] {2,3,4}).square().exportToIntArray());
        assertArrayEquals("Multi-digit squaring with carry should be correct", new int[] {1,(int)4294967294L}, new BigInt(new int[] {(int)4294967295L}).square().exportToIntArray());
    }

    @Test
    public void getPowerOf2Multiples() {
        BigInt x = new BigInt(new int[] {1,2,3});
        BigInt[] pow2Multiples = x.getPowerOf2Multiples(10);

        int p2 = 1;
        for (int i = 0; i < pow2Multiples.length; i++) {
            assertTrue(pow2Multiples[i].equals(x.multiply(new BigInt(p2))));
            p2 *= 2;
        }
    }

    @Test
    public void divide() {
        // BigInt.quotient and BigInt.mod both depend on BigInt.divide,
        // and so tests for quotient and mod aren't needed.

        BigInt.DivisionResult r = new BigInt(20).divide(new BigInt(2));
        assertArrayEquals("Single-digit division with no remainder", new int[] {10}, r.quotient().exportToIntArray());
        assertArrayEquals("Single-digit division with no remainder", new int[] {0}, r.remainder().exportToIntArray());

        BigInt.DivisionResult r2 = new BigInt(23).divide(new BigInt(4));
        assertArrayEquals("Single-digit division with remainder", new int[] {5}, r2.quotient().exportToIntArray());
        assertArrayEquals("Single-digit division with remainder", new int[] {3}, r2.remainder().exportToIntArray());

        BigInt.DivisionResult r3 = new BigInt(new int[] {0,0,1}).divide(new BigInt(new int[] {0,3}));
        assertArrayEquals("Multi-digit division with remainder", new int[] {1431655765}, r3.quotient().exportToIntArray());
        assertArrayEquals("Multi-digit division with remainder", new int[] {0,1}, r3.remainder().exportToIntArray());
    }

    @Test
    public void modInverse() {
        assertArrayEquals("Correct single-digit inverse", new int[] {7}, new BigInt(3).modInverse(new BigInt(10)).exportToIntArray());

        BigInt primeModulus = new BigInt(2).pow(new BigInt(127)).subtract(new BigInt(1));
        BigInt x = new BigInt(new int[] {1,2,3,4,5,6});
        BigInt xInverse = x.modInverse(primeModulus);

        assertArrayEquals("Correct multi-digit inverse, even if x > modulus", new int[] {1}, x.multiply(xInverse).mod(primeModulus).exportToIntArray());
    }

    @Test
    public void pow() {
        assertArrayEquals("Exponentiation with single digits", new int[] {128}, new BigInt(2).pow(new BigInt(7)).exportToIntArray());
        assertArrayEquals("Exponentiation with multiple digits", new int[] {1024,25600,288000,1920000,8400000,25200000,52500000,75000000,70312500,39062500,9765625}, new BigInt(new int[] {2,5}).pow(new BigInt(new int[] {10})).exportToIntArray());
    }

    @Test
    public void powMod() {
        assertArrayEquals("Exponentiation with single digits", new int[] {6}, new BigInt(2).powMod(new BigInt(1000000), new BigInt(10)).exportToIntArray());
        assertArrayEquals("Exponentiation with multiple digits", new int[] {1}, new BigInt(new int[] {(int)4294967295L,(int)4294967295L}).powMod(new BigInt(12345678), new BigInt(new int[] {0,0,1})).exportToIntArray());
        assertArrayEquals(new int[] {1}, new BigInt(3).powMod(new BigInt(6), new BigInt(7)).exportToIntArray());
        assertArrayEquals(new int[] {123}, new BigInt(new int[] {123}).powMod(new BigInt(new int[] {1,2,3}), new BigInt(65537)).exportToIntArray());
        assertArrayEquals("Exponentiation with multiple digits", new int[] {1313419847, -576389566, 2}, new BigInt(new int[] {2,5}).powMod(new BigInt(new int[] {1,2,3,4,5,6,7,8,9,10}), new BigInt(new int[] {1,2,3})).exportToIntArray());
    }

    @Test
    public void montgomeryMultiplication() {
        BigInt m = new BigInt(13);
        BigInt r = new BigInt(1).shiftBits(m.bitCount());

        BigInt rInverse = r.modInverse(m);
        BigInt mDash = r.subtract(m.modInverse(r));

        assertTrue(r.equals(16));

        BigInt a = new BigInt(5);
        BigInt b = new BigInt(9);

        BigInt arModM = a.multiply(r).mod(m);
        BigInt brModM = b.multiply(r).mod(m);

        assertArrayEquals(new int[] {5}, BigInt.montgomeryMultiplication(arModM, brModM, m, mDash, m.bitCount()).exportToIntArray());
    }

    @Test
    public void isProbablePrime() {
        assertTrue(new BigInt(37).isProbablePrime(15));
        assertTrue(new BigInt(2).pow(new BigInt(127)).subtract(new BigInt(1)).isProbablePrime(15));

        assertFalse(new BigInt(100).isProbablePrime(15));
        assertFalse(new BigInt(2).pow(new BigInt(127)).add(new BigInt(1)).isProbablePrime(15));
    }

    @Test
    public void compareTo() {
        assertTrue(new BigInt(5).compareTo(new BigInt(6)) < 0);
        assertTrue(new BigInt(5).compareTo(new BigInt(5)) == 0);
        assertTrue(new BigInt(5).compareTo(new BigInt(4)) > 0);
        assertTrue(new BigInt(new int[] {1,2}).compareTo(new BigInt(new int[] {2,2})) < 0);
        assertTrue(new BigInt(new int[] {1,2}).compareTo(new BigInt(new int[] {1,2})) == 0);
        assertTrue(new BigInt(new int[] {1,2}).compareTo(new BigInt(new int[] {0,2})) > 0);
        assertTrue(new BigInt(new int[] {1,2}).compareTo(new BigInt(new int[] {(int)4294967295L,2})) < 0);
        assertTrue(new BigInt(new int[] {0,10}).compareTo(new BigInt(new int[] {1,8})) > 0);
    }

    @Test
    public void equalsInt() {
        assertTrue(new BigInt(5).equals(5));
        assertFalse(new BigInt(new int[] {5,1}).equals(5));
        assertTrue(new BigInt(0).equals(0));
    }

    @Test
    public void exportToByteArray() {
        assertArrayEquals(new byte[] {1}, new BigInt(1).exportToByteArray());
        assertArrayEquals(new byte[] {1,1}, new BigInt(257).exportToByteArray());
        assertArrayEquals(new byte[] {(byte)255,(byte)255}, new BigInt(65535).exportToByteArray());
    }
}

