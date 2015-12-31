package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;

public class RsaProviderTests {
    @Test
    public void constructor() {
        try {
            new RsaProvider(null);
            fail("A NullPointerException should have been thrown");
        } catch (NullPointerException ex) {}
    }
}

