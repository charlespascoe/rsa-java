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

    private OaepProvider createOaepProviderWithFakeHash() {
        OaepProvider o = null;

        try {
            o = new OaepProvider(new MaskGenerator("SHA-256") {
                @Override
                public int getDigestLength() { return 4; }
            });
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }

        return o;
    }

    @Test
    public void buildDataBlockWithValidInput() {
        byte[] labelHash = new byte[] {1,2,3,4};
        byte[] msg = new byte[] {5,6,7,8};
        int maxMessageLength = 7;

        try {
            assertArrayEquals("It should return the correct output", new byte[] {1,2,3,4,0,0,0,1,5,6,7,8},  OaepProvider.buildDataBlock(labelHash, msg, maxMessageLength));
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
    }

    @Test
    public void buildDataBlockWithInvalidInput() {
        byte[] labelHash = new byte[] {1,2,3,4};
        int maxMessageLength = 7;

        try {
            OaepProvider.buildDataBlock(labelHash, new byte[8], maxMessageLength);
            fail("An exception should be thrown when the message is too long");
        } catch (Exception ex) {
        }
    }

   @Test
    public void encodeShouldThrowExceptionWhenMessageIsTooLong() {
        OaepProvider o = this.createOaepProviderWithFakeHash();
        int maxMessageLength = o.maxMessageLength(16);
        byte[] label = new byte[o.getDigestLength()];
        byte[] seed = new byte[o.getDigestLength()];
        byte[] msg = new byte[maxMessageLength + 1];

        try {
            o.encode(msg, 16, label, true, seed);
            fail("An exception should be thrown when the message is too long");
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    @Test
    public void encodeShouldThrowExceptionWhenLabelIsIncorrectLength() {
        OaepProvider o = this.createOaepProviderWithFakeHash();
        int maxMessageLength = o.maxMessageLength(16);
        byte[] label = new byte[o.getDigestLength() - 1];
        byte[] seed = new byte[o.getDigestLength()];
        byte[] msg = new byte[1];

        try {
            o.encode(msg, 16, label, true, seed);
            fail("An exception should be thrown when the label is the incorrect length");
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    @Test
    public void encodeShouldReturnCorrectOutput() {
        OaepProvider o = this.createOaepProvider();

        int keySize = 256;
        byte[] msg = new byte[] {9,8,7,6};
        byte[] labelHash = new byte[o.getDigestLength()];
        byte[] seed = new byte[o.getDigestLength()];
        byte[] output = new byte[] {-30, 69, 75, 27, -72, 85, -27, -77, -17, -55, -61, 31, 1, -19, 47, 58, -23, 21, 93, -14, 19, -38, 120, 91, -117, 121, -17, 37, -74, -100, 4, -104, 109, -74, 95, -43, -97, -45, 86, -10, 114, -111, 64, 87, 27, 91, -51, 107, -77, -72, 52, -110, -95, 110, 27, -16, -93, -120, 68, 66, -4, 60, -118, 14, 33, 88, -88, -112, 109, 94, 44, 43, -32, 1, -70, -55, 67, -85, -100, -85, 64, 99, 83, 110, 28, 84, 107, 64, 34, 31, -33, -115, -80, 49, -92, -69, -31, 95, 55, 68, 35, 99, 55, 1, -32, 79, -31, 124, 29, 100, 11, 52, -14, -30, 123, -113, 106, -20, 0, -30, 79, 29, -49, 80, -83, 9, 32, -77, -76, -29, -60, -6, 13, -109, -3, -118, -37, 14, -120, -41, 31, 70, 20, -48, 24, 20, -102, 89, -10, -91, -83, -65, -70, 27, -48, 0, -48, -25, -60, -102, -43, -124, -50, -105, -18, -50, 96, -113, -32, -19, 127, 73, 49, 98, -92, -87, -53, -4, -51, -109, 93, -96, -8, -61, -127, 96, -112, 102, 21, -105, -84, -21, 85, 74, 103, -54, -97, 82, -89, 113, -46, 107, 121, -69, 11, 88, -120, 44, -108, -5, 55, -38, 95, 28, -50, -84, -68, -19, 67, 114, 22, -123, 35, -12, -88, 72, 43, -93, -120, 122, 74, -70, 3, -38, -48, 68, 8, 81, -23, -34, -121, -72, 3, 123, 126, -115, 55, 56, -96, 83, 0, -8, 126, 68, -52};

        try {
            assertArrayEquals("Encode should return correct output", output, o.encode(msg, keySize, labelHash, true, seed));
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
    }

    @Test
    public void removePaddedShouldReturnCorrectOutput() {
        assertArrayEquals(new byte[] {1,0,2,4}, OaepProvider.removePadding(new byte[] {0,0,0,0,1,1,0,2,4}));
    }

    @Test
    public void decodeShouldThrowExceptionWhenCipherblockIsIncorrectLength() {
        OaepProvider o = this.createOaepProvider();
        try {
            o.decode(new byte[256], 256, new byte[o.getDigestLength()], true);
            fail("An exception should be thrown when the encoded message block is the incorrect length");
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    @Test
    public void decodeShouldThrowExceptionWhenKeySizeIsTooSmall() {
        OaepProvider o = this.createOaepProvider();
        try {
            o.decode(new byte[3], 4, new byte[o.getDigestLength()], true);
            fail("An exception should be thrown when the key size is too small");
        } catch (Exception ex) {}
    }

    @Test
    public void decodeShouldThrowExceptionWhenLabelHashDoesNotMatch() {
        OaepProvider o = this.createOaepProvider();
        byte[] msg = new byte[] {1,2,3,4};
        byte[] label = new byte[] {5,6,7,8};

        try {
            byte[] encodedDataBlock = o.encode(msg, 256, label);
            o.decode(encodedDataBlock, 256, new byte[] {5,6,7,9});
            fail("A DecodingException should have been thrown");
        } catch (OaepProvider.EncodingException ex) {
            fail("Unexpected EncodingException: " + ex.toString());
        } catch (OaepProvider.DecodingException ex) {}
    }

    @Test
    public void decodeShouldReturnCorrectOutput() {
        OaepProvider o = this.createOaepProvider();

        int keySize = 256;
        byte[] msg = new byte[] {9,8,7,6};
        byte[] labelHash = new byte[o.getDigestLength()];
        byte[] output = new byte[] {-30, 69, 75, 27, -72, 85, -27, -77, -17, -55, -61, 31, 1, -19, 47, 58, -23, 21, 93, -14, 19, -38, 120, 91, -117, 121, -17, 37, -74, -100, 4, -104, 109, -74, 95, -43, -97, -45, 86, -10, 114, -111, 64, 87, 27, 91, -51, 107, -77, -72, 52, -110, -95, 110, 27, -16, -93, -120, 68, 66, -4, 60, -118, 14, 33, 88, -88, -112, 109, 94, 44, 43, -32, 1, -70, -55, 67, -85, -100, -85, 64, 99, 83, 110, 28, 84, 107, 64, 34, 31, -33, -115, -80, 49, -92, -69, -31, 95, 55, 68, 35, 99, 55, 1, -32, 79, -31, 124, 29, 100, 11, 52, -14, -30, 123, -113, 106, -20, 0, -30, 79, 29, -49, 80, -83, 9, 32, -77, -76, -29, -60, -6, 13, -109, -3, -118, -37, 14, -120, -41, 31, 70, 20, -48, 24, 20, -102, 89, -10, -91, -83, -65, -70, 27, -48, 0, -48, -25, -60, -102, -43, -124, -50, -105, -18, -50, 96, -113, -32, -19, 127, 73, 49, 98, -92, -87, -53, -4, -51, -109, 93, -96, -8, -61, -127, 96, -112, 102, 21, -105, -84, -21, 85, 74, 103, -54, -97, 82, -89, 113, -46, 107, 121, -69, 11, 88, -120, 44, -108, -5, 55, -38, 95, 28, -50, -84, -68, -19, 67, 114, 22, -123, 35, -12, -88, 72, 43, -93, -120, 122, 74, -70, 3, -38, -48, 68, 8, 81, -23, -34, -121, -72, 3, 123, 126, -115, 55, 56, -96, 83, 0, -8, 126, 68, -52};


        try {
            assertArrayEquals(msg, o.decode(output, keySize, labelHash, true));
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.toString());
        }
    }
}

