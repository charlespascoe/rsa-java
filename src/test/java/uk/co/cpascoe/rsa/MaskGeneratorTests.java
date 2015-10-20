package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;
import java.security.NoSuchAlgorithmException;

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

}
