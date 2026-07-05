package net.solace.impl.entities;

import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.entities.ITileItems;
import net.solace.api.entities.ITiles;

import java.util.ArrayList;
import java.util.List;

public class TileItemsImpl extends TileEntitiesImpl<ITileItem> implements ITileItems {
    public TileItemsImpl(ITiles tiles, IClient client) {
        super(tiles, client, TileItemsImpl::getTileItems);
    }

    private static List<ITileItem> getTileItems(ITile tile) {
        var out = new ArrayList<ITileItem>();
        if (tile == null) {
            return out;
        }

        var groundItems = tile.getIGroundItems();
        for (var groundItem : groundItems) {
            if (groundItem != null && groundItem.getId() != -1) {
                out.add(groundItem);
            }
        }

        return out;
    }
}
