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
    public void getBitAt() {
        assertEquals(0, new BigInt(0).getBitAt(0));
        assertEquals(1, new BigInt(1).getBitAt(0));
        assertEquals(0, new BigInt(2).getBitAt(0));
        assertEquals(1, new BigInt(2).getBitAt(1));
        assertEquals(1, new BigInt(256).getBitAt(8));
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
    public void addToDigit() {
        BigInt x = new BigInt(1);
        x.addToDigit(2, 0);
        assertEquals("Correctly increments a single digit", 3, x.getDigit(0));

        x.setDigit((int)4294967295L, 0);
        x.addToDigit(1, 0);
        assertArrayEquals("Carries 1 on overflow", new int[] {0,1}, x.exportToIntArray());
    }
}

