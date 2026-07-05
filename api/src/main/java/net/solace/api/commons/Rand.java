package net.solace.api.commons;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Rand {
    private static final SecureRandom secureRandom;

    public static synchronized int nextInt(int min, int max) {
        return secureRandom.nextInt(max - min + 1) + min;
    }

    public static synchronized int nextInt() {
        return secureRandom.nextInt();
    }

    public static synchronized boolean nextBool() {
        return secureRandom.nextBoolean();
    }

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

