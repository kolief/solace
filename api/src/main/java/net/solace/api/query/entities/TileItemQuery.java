package net.solace.api.query.entities;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.query.entities.SceneEntityQuery;
import net.solace.api.query.results.SceneEntityQueryResults;
import org.apache.commons.lang3.ArrayUtils;

public class TileItemQuery
extends SceneEntityQuery<ITileItem, TileItemQuery> {
    private int[] quantities = null;
    private ITile[] tiles = null;
    private Boolean tradable = null;
    private Boolean stackable = null;
    private Boolean noted = null;
    private Boolean members = null;
    private String[] inventoryActions = null;

    public TileItemQuery(Supplier<List<ITileItem>> supplier) {
        super(supplier);
    }

    public TileItemQuery quantities(int ... quantities) {
        this.quantities = quantities;
        return this;
    }

    public TileItemQuery tiles(ITile ... tiles) {
        this.tiles = tiles;
        return this;
    }

    public TileItemQuery tradable(boolean tradable) {
        this.tradable = tradable;
        return this;
    }

    public TileItemQuery stackable(boolean stackable) {
        this.stackable = stackable;
        return this;
    }

    public TileItemQuery noted(boolean noted) {
        this.noted = noted;
        return this;
    }

    public TileItemQuery members(boolean members) {
        this.members = members;
        return this;
    }

    public TileItemQuery inventoryActions(String ... inventoryActions) {
        this.inventoryActions = inventoryActions;
        return this;
    }

    @Override
    protected SceneEntityQueryResults<ITileItem> results(List<ITileItem> list) {
        return new SceneEntityQueryResults<ITileItem>(list);
    }

    @Override
    public boolean test(ITileItem tileItem) {
        if (this.quantities != null && !ArrayUtils.contains((int[])this.quantities, (int)tileItem.getQuantity())) {
            return false;
        }
        if (this.tiles != null && !ArrayUtils.contains((Object[])this.tiles, (Object)tileItem.getTile())) {
            return false;
        }
        if (this.tradable != null && !this.tradable.equals(tileItem.isTradable())) {
            return false;
        }
        if (this.stackable != null && !this.stackable.equals(tileItem.isStackable())) {
            return false;
        }
        if (this.noted != null && !this.noted.equals(tileItem.isNoted())) {
            return false;
        }
        if (this.members != null && !this.members.equals(tileItem.isMembers())) {
            return false;
        }
        if (this.inventoryActions != null && Arrays.stream(this.inventoryActions).noneMatch(Predicates.texts(tileItem.getInventoryActions()))) {
            return false;
        }
        return super.test(tileItem);
    }
}

