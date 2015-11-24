package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;

public class UtilsTests {
    @Test
    public void isValidHexShouldReturnTrueForValidSingleDigitHex() {
        assertTrue("It should return true for a single digit '0'", Utils.isValidHex("0"));
        assertTrue("Another numeric digit", Utils.isValidHex("1"));
        assertTrue("Another numeric digit", Utils.isValidHex("9"));
        assertTrue("It should return true for the uppercase letter 'A'", Utils.isValidHex("A"));
        assertTrue("It should return true for the lowercase letter 'a'", Utils.isValidHex("a"));
        assertTrue(Utils.isValidHex("F"));
        assertTrue(Utils.isValidHex("f"));
    }

    @Test
    public void isValidHexShouldReturnTrueForValidMultiDigitHex() {
        assertTrue(Utils.isValidHex("00"));
        assertTrue(Utils.isValidHex("99"));
        assertTrue(Utils.isValidHex("AF"));
        assertTrue(Utils.isValidHex("Fa"));
        assertTrue(Utils.isValidHex("12a"));
        assertTrue(Utils.isValidHex("abc123"));
    }

    @Test
    public void isValidHexShouldReturnFalseForInvalidHex() {
        assertFalse(Utils.isValidHex(null));
        assertFalse(Utils.isValidHex(""));
        assertFalse(Utils.isValidHex("!?!?!?"));
        assertFalse(Utils.isValidHex("123z"));
        assertFalse(Utils.isValidHex("-1 + 3"));
        assertFalse(Utils.isValidHex("ggggggge"));
    }

    @Test
    public void intToBytes() {
        assertArrayEquals(new byte[] {0,0,0,0}, Utils.intToBytes(0));
        assertArrayEquals(new byte[] {0,0,0,1}, Utils.intToBytes(1));
        assertArrayEquals(new byte[] {0,0,1,0}, Utils.intToBytes(256));
        assertArrayEquals(new byte[] {0,0,1,1}, Utils.intToBytes(257));
        assertArrayEquals(new byte[] {0,0,2,0}, Utils.intToBytes(512));
        assertArrayEquals(new byte[] {0,0,(byte)255,(byte)255}, Utils.intToBytes(65535));
        assertArrayEquals(new byte[] {1,0,0,0}, Utils.intToBytes(16777216));
        assertArrayEquals(new byte[] {(byte)255,(byte)255,(byte)255,(byte)255}, Utils.intToBytes(-1));
    }

    @Test
    public void xorBytesWithValidInput() {
        try {
            assertArrayEquals(new byte[] {}, Utils.xorBytes(new byte[] {}, new byte[] {}));
            assertArrayEquals(new byte[] {0}, Utils.xorBytes(new byte[] {0}, new byte[] {0}));
            assertArrayEquals(new byte[] {1}, Utils.xorBytes(new byte[] {0}, new byte[] {1}));
            assertArrayEquals(new byte[] {0}, Utils.xorBytes(new byte[] {1}, new byte[] {1}));
            assertArrayEquals(new byte[] {(byte)255}, Utils.xorBytes(new byte[] {(byte)170}, new byte[] {85}));
            assertArrayEquals(new byte[] {(byte)170,85}, Utils.xorBytes(new byte[] {(byte)255,(byte)255}, new byte[] {85,(byte)170}));
        } catch (Exception ex) {
            fail("Unexpected Exception thrown: " + ex.toString());
        }
    }

    @Test
    public void xorByteWithInvalidInput() {
        // TODO: Pass in nulls
        try {
            Utils.xorBytes(new byte[] {0}, new byte[] {0,0});
            fail("An exception should have been thrown");
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    @Test
    public void concat() {
        // Can handle 0 to 3 arguments
        assertArrayEquals(Utils.concat(), new byte[0]);
        assertArrayEquals(Utils.concat(new byte[0]), new byte[0]);
        assertArrayEquals(Utils.concat(new byte[0], new byte[0]), new byte[0]);
        assertArrayEquals(Utils.concat(new byte[0], new byte[0], new byte[0]), new byte[0]);

        // Check ordering
        assertArrayEquals(Utils.concat(new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 }, new byte[] { 7, 8, 9 }), new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
    }

}

