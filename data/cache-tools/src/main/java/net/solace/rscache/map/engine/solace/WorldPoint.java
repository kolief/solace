package net.solace.rscache.map.engine.solace;

import net.solace.rscache.map.engine.model.map.Tile;

public record WorldPoint(int x, int y, int plane) {

    public Tile toTile() {
        return new Tile(x, y, plane);
    }
}
