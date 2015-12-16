package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;

public class MathUtilsTests {
    @Test
    public void gcd() {
        assertTrue(MathUtils.gcd(new BigInt(5), new BigInt(0)).equals(new BigInt(5)));
        assertTrue(MathUtils.gcd(new BigInt(0), new BigInt(123)).equals(new BigInt(123)));
        assertTrue(MathUtils.gcd(new BigInt(12), new BigInt(5)).equals(new BigInt(1)));
        assertTrue(MathUtils.gcd(new BigInt(12), new BigInt(16)).equals(new BigInt(4)));
    }
}
