package net.solace.api.containers;

import net.solace.api.domain.tiles.ITile;

public interface TileContainer {
    public ITile getAt(int var1, int var2);

    public ITile getAt(int var1, int var2, int var3);

    public ITile[][] getAll();

    public ITile[][][] getAllFloors();

    public int getSizeX();

    public int getSizeY();

    public int getWorldViewId();
}

