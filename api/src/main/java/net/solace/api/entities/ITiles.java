package net.solace.api.entities;

import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.entities.EntityProvider;

public interface ITiles
extends EntityProvider<ITile> {
    public ITile getAt(int var1, Point var2);

    public ITile[][] getRaw();

    public ITile[][][] getRawFloors();

    public ITile getAt(WorldPoint var1);

    public int getSizeX();

    public int getSizeY();

    public int getWorldViewId();
}

