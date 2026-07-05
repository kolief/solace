package net.solace.api.domain.tiles;

import java.util.List;
import net.runelite.api.Tile;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.RuneLiteWrapper;
import net.solace.api.domain.tiles.IDecorativeObject;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.IGroundObject;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.domain.tiles.IWallObject;

public interface ITile
extends Tile,
Locatable,
RuneLiteWrapper<Tile> {
    public IWallObject getWallObject();

    public IGroundObject getGroundObject();

    public IDecorativeObject getDecorativeObject();

    public List<IGameObject> getIGameObjects();

    public List<ITileItem> getIGroundItems();

    public boolean isObstructed();

    public boolean isEmpty();

    public boolean hasLineOfSightTo(ITile var1);

    public List<ITile> pathTo(ITile var1);

    public List<ITileObject> getTileObjects();

    public ITile getBridge();
}

