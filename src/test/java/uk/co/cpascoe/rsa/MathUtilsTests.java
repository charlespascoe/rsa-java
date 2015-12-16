package uk.co.cpascoe.rsa;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;

public class MathUtilsTests {
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
