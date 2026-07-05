package net.solace.rscache.map.engine.model.map;

public record Scene(int packed) {
    private static final int SIZE = 104;

    public Scene(int x, int y) {
        this((x & 0xFFFF) | ((y & 0xFFFF) << 16));
    }

    public int x() {
        return packed & 0xFFFF;
    }

    public int y() {
        return (packed >> 16) & 0xFFFF;
    }
}
