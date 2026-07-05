package net.solace.rscache.map.engine.model.map;

public record Chunk(int packed) {
    private static final int SIZE = 8;

    public Chunk(int x, int y, int plane) {
        this((x & 0x7FF) | ((y & 0x7FF) << 11) | (plane << 22));
    }

    int x() {
        return packed & 0x7FF;
    }

    int y() {
        return (packed >> 11) & 0x7FF;
    }

    int plane() {
        return (packed >> 22) & 0x3;
    }
}
