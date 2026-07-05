package net.solace.rscache.map.engine.model.obj;

import net.runelite.cache.definitions.ObjectDefinition;
import net.solace.rscache.map.engine.model.map.Tile;

public record GameObject(
        ObjectDefinition data,
        Tile tile,
        int attributes
) {
    public GameObject(
            ObjectDefinition data,
            Tile tile,
            int shape,
            int rotation
    ) {
        this(data, tile, (shape << 2) | rotation);
    }

    public int id() {
        return data.getId();
    }

    public int shape() {
        return attributes >> 2;
    }

    public int rotation() {
        return attributes & 0x3;
    }
}
