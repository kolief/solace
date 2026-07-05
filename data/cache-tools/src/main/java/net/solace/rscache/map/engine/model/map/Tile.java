package net.solace.rscache.map.engine.model.map;

public record Tile(int packed) {
    private static final int SIZE = 1;

    public Tile(int x, int y, int plane) {
        this((x & 0x7FFF) | ((y & 0x7FFF) << 15) | (plane << 30));
    }

    public int x() {
        return packed & 0x7FFF;
    }

    public int y() {
        return (packed >> 15) & 0x7FFF;
    }

    public int plane() {
        return (packed >> 30) & 0x3;
    }

    public Tile translate(int x, int y, int plane) {
        return new Tile(x + x(), y + y(), plane + plane());
    }

    public Tile translate(int x, int y) {
        return new Tile(x + x(), y + y(), plane());
    }

    @Override
    public String toString() {
        return "TILE[x: %s, y: %s, plane: %s".formatted(x(), y(), plane());
    }
}
