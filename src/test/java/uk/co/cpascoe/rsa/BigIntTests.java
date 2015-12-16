package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;

public class BigIntTests {
    @Test
    public void createFromInt() {
        assertArrayEquals(new int[] {0}, new BigInt(0).exportToIntArray());
        assertArrayEquals(new int[] {1}, new BigInt(1).exportToIntArray());
        assertArrayEquals(new int[] {2048}, new BigInt(2048).exportToIntArray());
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
    public void getBitAt() {
        assertEquals(0, new BigInt(0).getBitAt(0));
        assertEquals(1, new BigInt(1).getBitAt(0));
        assertEquals(0, new BigInt(2).getBitAt(0));
        assertEquals(1, new BigInt(2).getBitAt(1));
        assertEquals(1, new BigInt(256).getBitAt(8));
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
        BigInt z = new BigInt((int)4294967295L);

        assertArrayEquals("Correctly adds two single-digit BigInts", new int[] {3}, x.add(y).exportToIntArray());
        assertArrayEquals("Correctly adds two BigInts, resulting in a carry", new int[] {0,1}, x.add(z).exportToIntArray());
    }

    @Test
    public void subtract() {
        assertArrayEquals(new int[] {5}, new BigInt(8).subtract(new BigInt(3)).exportToIntArray());

        BigInt x = new BigInt(new int[] {0,10});
        BigInt y = new BigInt(new int[] {1,8});
        assertArrayEquals(new int[] {(int)4294967295L,1}, x.subtract(y).exportToIntArray());
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
}

