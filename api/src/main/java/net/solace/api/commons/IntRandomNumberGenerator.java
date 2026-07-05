package net.solace.api.commons;

import java.util.PrimitiveIterator;
import java.util.Random;

public final class IntRandomNumberGenerator {
    private final PrimitiveIterator.OfInt randomIterator;

    public IntRandomNumberGenerator(int min, int max) {
        this.randomIterator = new Random().ints(min, max + 1).iterator();
    }

    public int nextInt() {
        return this.randomIterator.nextInt();
    }
}

