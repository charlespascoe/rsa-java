package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;

public class MathUtilsTests {
    @Test
    public void intToBigEndianBytes() {
        assertArrayEquals(new byte[] {0,0,0,0}, MathUtils.intToBigEndianBytes(0));
        assertArrayEquals(new byte[] {0,0,0,1}, MathUtils.intToBigEndianBytes(1));
        assertArrayEquals(new byte[] {0,0,1,0}, MathUtils.intToBigEndianBytes(256));
        assertArrayEquals(new byte[] {0,0,1,1}, MathUtils.intToBigEndianBytes(257));
        assertArrayEquals(new byte[] {0,0,2,0}, MathUtils.intToBigEndianBytes(512));
        assertArrayEquals(new byte[] {0,0,(byte)255,(byte)255}, MathUtils.intToBigEndianBytes(65535));
        assertArrayEquals(new byte[] {1,0,0,0}, MathUtils.intToBigEndianBytes(16777216));
        assertArrayEquals(new byte[] {(byte)255,(byte)255,(byte)255,(byte)255}, MathUtils.intToBigEndianBytes(-1));
    }

    @Test
    public void unsignedByte() {
        assertEquals(0, MathUtils.unsignedByte((byte)0));
        assertEquals(1, MathUtils.unsignedByte((byte)1));
        assertEquals(127, MathUtils.unsignedByte((byte)127));
        assertEquals(128, MathUtils.unsignedByte((byte)128));
        assertEquals(255, MathUtils.unsignedByte((byte)255));
    }

    @Test
    public void bigEndianBytesToInt() {
        assertEquals(0, MathUtils.bigEndianBytesToInt(new byte[] {0,0,0,0}));
        assertEquals(1, MathUtils.bigEndianBytesToInt(new byte[] {0,0,0,1}));
        assertEquals(255, MathUtils.bigEndianBytesToInt(new byte[] {0,0,0,(byte)255}));
        assertEquals(256, MathUtils.bigEndianBytesToInt(new byte[] {0,0,1,0}));
        assertEquals(257, MathUtils.bigEndianBytesToInt(new byte[] {0,0,1,1}));
        assertEquals(16909060, MathUtils.bigEndianBytesToInt(new byte[] {1,2,3,4}));
    }

    @Test
    public void littleEndianBytesToInt() {
        assertEquals(0, MathUtils.littleEndianBytesToInt(new byte[] {0,0,0,0}));
        assertEquals(1, MathUtils.littleEndianBytesToInt(new byte[] {1,0,0,0}));
        assertEquals(255, MathUtils.littleEndianBytesToInt(new byte[] {(byte)255,0,0,0}));
        assertEquals(256, MathUtils.littleEndianBytesToInt(new byte[] {0,1,0,0}));
        assertEquals(257, MathUtils.littleEndianBytesToInt(new byte[] {1,1,0,0}));
        assertEquals(16909060, MathUtils.littleEndianBytesToInt(new byte[] {4,3,2,1}));
    }

    @Test
    public void gcd() {
        assertTrue(MathUtils.gcd(new BigInt(5), new BigInt(0)).equals(new BigInt(5)));
        assertTrue(MathUtils.gcd(new BigInt(0), new BigInt(123)).equals(new BigInt(123)));
        assertTrue(MathUtils.gcd(new BigInt(12), new BigInt(5)).equals(new BigInt(1)));
        assertTrue(MathUtils.gcd(new BigInt(12), new BigInt(16)).equals(new BigInt(4)));
    }

    @Test
    public void randomBigInt() {
        BigInt limit = new BigInt(2).pow(new BigInt(1000));
        Random r = new Random();
        for (int i = 0; i < 2000; i++) {
            assertTrue(MathUtils.randomBigInt(1000, r).lessThan(limit));
        }
    }

    @Test
    public void randomBigIntBetweenRange() {
        BigInt lowerLimit = new BigInt(2).pow(new BigInt(100));
        BigInt upperLimit = new BigInt(3).pow(new BigInt(150));
        Random r = new Random();

        for (int i = 0; i < 2000; i++) {
            BigInt x = MathUtils.randomBigInt(lowerLimit, upperLimit, r);
            assertTrue(x.lessThanOrEqual(upperLimit));
            assertTrue(x.greaterThanOrEqual(lowerLimit));
        }
    }
}
