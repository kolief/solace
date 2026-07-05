package net.solace.api.events;

import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileItem;

public final class ItemDespawned {
    private final ITile tile;
    private final ITileItem item;

    public ItemDespawned(ITile tile, ITileItem item) {
        this.tile = tile;
        this.item = item;
    }

    public ITile getTile() {
        return this.tile;
    }

    public ITileItem getItem() {
        return this.item;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemDespawned)) {
            return false;
        }
        ItemDespawned other = (ItemDespawned)o;
        ITile this$tile = this.getTile();
        ITile other$tile = other.getTile();
        if (this$tile == null ? other$tile != null : !this$tile.equals(other$tile)) {
            return false;
        }
        ITileItem this$item = this.getItem();
        ITileItem other$item = other.getItem();
        return !(this$item == null ? other$item != null : !this$item.equals(other$item));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ITile $tile = this.getTile();
        result = result * 59 + ($tile == null ? 43 : $tile.hashCode());
        ITileItem $item = this.getItem();
        result = result * 59 + ($item == null ? 43 : $item.hashCode());
        return result;
    }

    public String toString() {
        return "ItemDespawned(tile=" + String.valueOf(this.getTile()) + ", item=" + String.valueOf(this.getItem()) + ")";
    }
}

