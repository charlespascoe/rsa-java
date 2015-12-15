package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class OaepProviderTests {
    private OaepProvider createOaepProvider() {
        OaepProvider o = null;

        try {
            o = new OaepProvider(new MaskGenerator("SHA-256"));
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }

        return o;
    }

    @Test
    public void buildDataBlockTests() {
        byte[] labelHash = new byte[] {1,2,3,4};
        byte[] msg = new byte[] {5,6,7,8};
        int maxMessageLength = 7;

        try {
            assertArrayEquals("It should return the correct output", new byte[] {1,2,3,4,0,0,0,1,5,6,7,8},  OaepProvider.buildDataBlock(labelHash, msg, maxMessageLength));
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }

        try {
            OaepProvider.buildDataBlock(labelHash, new byte[8], maxMessageLength);
            fail("An exception should have been thrown");
        } catch (Exception ex) {
            assertTrue(true);
        }
    }
}

