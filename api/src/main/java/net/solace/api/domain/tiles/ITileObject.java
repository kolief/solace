package net.solace.api.domain.tiles;

import javax.annotation.Nullable;
import net.runelite.api.EntityOps;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.solace.api.domain.RuneLiteWrapper;
import net.solace.api.domain.Transformable;
import net.solace.api.domain.tiles.TileEntity;

public interface ITileObject
extends TileObject,
TileEntity,
Transformable<ObjectComposition>,
RuneLiteWrapper<TileObject> {
    public Point menuPoint();

    public void updateComposition();

    @Nullable
    public EntityOps getOps();
}

