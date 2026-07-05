package net.solace.api.events;

import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.IWallObject;

public final class WallObjectDespawned {
    private final ITile tile;
    private final IWallObject wallObject;

    public WallObjectDespawned(ITile tile, IWallObject wallObject) {
        this.tile = tile;
        this.wallObject = wallObject;
    }

    public ITile getTile() {
        return this.tile;
    }

    public IWallObject getWallObject() {
        return this.wallObject;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WallObjectDespawned)) {
            return false;
        }
        WallObjectDespawned other = (WallObjectDespawned)o;
        ITile this$tile = this.getTile();
        ITile other$tile = other.getTile();
        if (this$tile == null ? other$tile != null : !this$tile.equals(other$tile)) {
            return false;
        }
        IWallObject this$wallObject = this.getWallObject();
        IWallObject other$wallObject = other.getWallObject();
        return !(this$wallObject == null ? other$wallObject != null : !this$wallObject.equals(other$wallObject));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ITile $tile = this.getTile();
        result = result * 59 + ($tile == null ? 43 : $tile.hashCode());
        IWallObject $wallObject = this.getWallObject();
        result = result * 59 + ($wallObject == null ? 43 : $wallObject.hashCode());
        return result;
    }

    public String toString() {
        return "WallObjectDespawned(tile=" + String.valueOf(this.getTile()) + ", wallObject=" + String.valueOf(this.getWallObject()) + ")";
    }
}

