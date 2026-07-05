package net.solace.api.domain.tiles;

import net.solace.api.domain.SceneEntity;
import net.solace.api.domain.tiles.ITile;

public interface TileEntity
extends SceneEntity {
    public ITile getTile();
}

