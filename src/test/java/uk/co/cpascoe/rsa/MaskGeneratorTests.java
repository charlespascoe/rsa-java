package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MaskGeneratorTests {

    public MaskGenerator createMaskGen() {
        MaskGenerator mg = null;

        try {
            mg = new MaskGenerator("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            fail("Failed to create Mask Generator");
        }

        return mg;
    }

    @Test
    public void shouldReturnCorrectDigestLength() throws NoSuchAlgorithmException {
        MaskGenerator mg = new MaskGenerator("SHA-256");
        assertEquals(mg.getDigestLength(), 32);

        mg = new MaskGenerator("SHA-384");
        assertEquals(mg.getDigestLength(), 48);

        mg = new MaskGenerator("MD5");
        assertEquals(mg.getDigestLength(), 16);
    }

    @Test
    public void shouldThrowErrorOnInvalidDigestName() {
        try {
            MaskGenerator mg = new MaskGenerator("NOTAHASH-65536");
            fail("Exception not thrown");
        } catch (NoSuchAlgorithmException ex) {
        }
    }

    @Test
    public void shouldGenerateCorrectLengthOfData() throws NoSuchAlgorithmException {
        MaskGenerator mg = this.createMaskGen();

        for (int i = 0; i <= 128; i++) {
            assertEquals(mg.generateMask(new byte[0], i).length, i);
        }
    }

    @Test
    public void shouldBeDeterministic() throws NoSuchAlgorithmException {
        MaskGenerator mg = this.createMaskGen();

        byte[] seed1 = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
        byte[] seed2 = new byte[] {8, 7, 6, 5, 4, 3, 2, 1};

        assertArrayEquals(mg.generateMask(seed1, 128), mg.generateMask(seed1, 128));
        assertArrayEquals(mg.generateMask(seed2, 128), mg.generateMask(seed2, 128));
        assertFalse(Arrays.equals(mg.generateMask(seed1, 128), mg.generateMask(seed2, 128)));

    }

}
