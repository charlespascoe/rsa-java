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
}

