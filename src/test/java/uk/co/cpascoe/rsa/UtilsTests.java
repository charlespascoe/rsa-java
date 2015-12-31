package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UtilsTests {
    @Test
    public void isValidHexShouldReturnTrueForValidHex() {
        assertTrue(Utils.isValidHex("00"));
        assertTrue(Utils.isValidHex("01"));
        assertTrue(Utils.isValidHex("99"));
        assertTrue(Utils.isValidHex("AA"));
        assertTrue(Utils.isValidHex("aA"));
        assertTrue(Utils.isValidHex("FF"));
        assertTrue(Utils.isValidHex("ff"));
        assertTrue(Utils.isValidHex("AF"));
        assertTrue(Utils.isValidHex("Fa"));
        assertTrue(Utils.isValidHex("12ab"));
        assertTrue(Utils.isValidHex("abc123"));
    }

    @Test
    public void isValidHexShouldReturnFalseForInvalidHex() {
        assertFalse(Utils.isValidHex(null));
        assertFalse(Utils.isValidHex(""));
        assertFalse("Single-digit hex not valid", Utils.isValidHex("0"));
        assertFalse(Utils.isValidHex("!?!?!?"));
        assertFalse(Utils.isValidHex("123z"));
        assertFalse(Utils.isValidHex("-1 + 3"));
        assertFalse(Utils.isValidHex("ggggggge"));
    }

    @Test
    public void xorBytes() {
        assertArrayEquals(new byte[] {}, Utils.xorBytes(new byte[] {}, new byte[] {}));
        assertArrayEquals(new byte[] {0}, Utils.xorBytes(new byte[] {0}, new byte[] {0}));
        assertArrayEquals(new byte[] {1}, Utils.xorBytes(new byte[] {0}, new byte[] {1}));
        assertArrayEquals(new byte[] {0}, Utils.xorBytes(new byte[] {1}, new byte[] {1}));
        assertArrayEquals(new byte[] {(byte)255}, Utils.xorBytes(new byte[] {(byte)170}, new byte[] {85}));
        assertArrayEquals(new byte[] {(byte)170,85}, Utils.xorBytes(new byte[] {(byte)255,(byte)255}, new byte[] {85,(byte)170}));
        assertArrayEquals(new byte[] {(byte)170,85}, Utils.xorBytes(new byte[] {(byte)255,(byte)255}, new byte[] {85,(byte)170}));
        assertArrayEquals("It should handle arrays of different lengths", new byte[] {(byte)170}, Utils.xorBytes(new byte[] {(byte)255,123}, new byte[] {85}));
    }

    @Test
    public void concat() {
        // Can handle 0 to 3 arguments
        assertArrayEquals(new byte[0], Utils.concat());
        assertArrayEquals(new byte[0], Utils.concat(new byte[0]));
        assertArrayEquals(new byte[0], Utils.concat(new byte[0], new byte[0]));
        assertArrayEquals(new byte[0], Utils.concat(new byte[0], new byte[0], new byte[0]));

        // Check ordering
        assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, Utils.concat(new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 }, new byte[] { 7, 8, 9 }));
    }

    @Test
    public void takeBytes() {
        assertArrayEquals(new byte[0], Utils.takeBytes(new byte[0], 0));
        assertArrayEquals(new byte[0], Utils.takeBytes(new byte[16], 0));
        assertArrayEquals(new byte[8], Utils.takeBytes(new byte[16], 8));
        assertArrayEquals(new byte[] {1,2,3,4}, Utils.takeBytes(new byte[] {1,2,3,4,5,6,7,8}, 4));
        assertArrayEquals(new byte[8], Utils.takeBytes(new byte[4], 8));
    }

    @Test
    public void removeBytes() {
        assertArrayEquals(new byte[0], Utils.removeBytes(new byte[0], 0));
        assertArrayEquals(new byte[] {4,5,6,7,8}, Utils.removeBytes(new byte[] {1,2,3,4,5,6,7,8}, 3));
    }

    @Test
    public void pipeStream() {
        ByteArrayInputStream input1 = new ByteArrayInputStream(new byte[] {1,2,3,4,5,6,7,8});
        ByteArrayOutputStream output1 = new ByteArrayOutputStream();

        ByteArrayInputStream input2 = new ByteArrayInputStream(new byte[1000000]);
        ByteArrayOutputStream output2 = new ByteArrayOutputStream();

        try {
            Utils.pipeStream(input1, output1);
            Utils.pipeStream(input2, output2);
        } catch (IOException ex) {
            fail("Unexpected exception: " + ex.toString());
        }

        assertArrayEquals(new byte[] {1,2,3,4,5,6,7,8}, output1.toByteArray());
        assertArrayEquals(new byte[1000000], output2.toByteArray());
    }

    @Test
    public void constantTimeEquals() {
        assertFalse("Arrays of different lengths should not be equal", Utils.constantTimeEquals(new byte[1], new byte[2]));
        assertTrue("Empty arrays should be equal", Utils.constantTimeEquals(new byte[0], new byte[0]));
        assertTrue("Zero-filled arrays should be equal", Utils.constantTimeEquals(new byte[10], new byte[10]));
        assertFalse("Two arrays with a single number different should not be equal", Utils.constantTimeEquals(new byte[] {1,2,3}, new byte[] {1,2,5}));
    }
}

