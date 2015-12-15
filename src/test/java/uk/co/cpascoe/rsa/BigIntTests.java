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
        // N.B.: Signed -1 int is the same as 4294967295 unsigned int
        assertArrayEquals(new int[] {-1}, new BigInt(new byte[] {(byte)255,(byte)255,(byte)255,(byte)255}).exportToIntArray());
        assertArrayEquals(new int[] {0,1}, new BigInt(new byte[] {0,0,0,0,1}).exportToIntArray());
        assertArrayEquals(new int[] {0,-1}, new BigInt(new byte[] {0,0,0,0,(byte)255,(byte)255,(byte)255,(byte)255}).exportToIntArray());
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
}

