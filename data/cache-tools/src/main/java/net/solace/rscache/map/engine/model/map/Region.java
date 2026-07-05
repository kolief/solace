package net.solace.rscache.map.engine.model.map;

public record Region(int id) {
    private static final int SIZE = 64;

    public Region(int x, int y) {
        this((x << 8) | y);
    }

    public int x() {
        return id >> 8;
    }

    public int y() {
        return id & 0xFF;
    }

    public Tile toTile(int plane) {
        return new Tile(x() * SIZE, y() * SIZE, plane);
    }
}
