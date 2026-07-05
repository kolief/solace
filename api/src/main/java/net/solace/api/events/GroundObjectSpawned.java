package net.solace.api.events;

import net.solace.api.domain.tiles.IGroundObject;
import net.solace.api.domain.tiles.ITile;

public final class GroundObjectSpawned {
    private final ITile tile;
    private final IGroundObject groundObject;

    public GroundObjectSpawned(ITile tile, IGroundObject groundObject) {
        this.tile = tile;
        this.groundObject = groundObject;
    }

    public ITile getTile() {
        return this.tile;
    }

    public IGroundObject getGroundObject() {
        return this.groundObject;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GroundObjectSpawned)) {
            return false;
        }
        GroundObjectSpawned other = (GroundObjectSpawned)o;
        ITile this$tile = this.getTile();
        ITile other$tile = other.getTile();
        if (this$tile == null ? other$tile != null : !this$tile.equals(other$tile)) {
            return false;
        }
        IGroundObject this$groundObject = this.getGroundObject();
        IGroundObject other$groundObject = other.getGroundObject();
        return !(this$groundObject == null ? other$groundObject != null : !this$groundObject.equals(other$groundObject));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ITile $tile = this.getTile();
        result = result * 59 + ($tile == null ? 43 : $tile.hashCode());
        IGroundObject $groundObject = this.getGroundObject();
        result = result * 59 + ($groundObject == null ? 43 : $groundObject.hashCode());
        return result;
    }

    public String toString() {
        return "GroundObjectSpawned(tile=" + String.valueOf(this.getTile()) + ", groundObject=" + String.valueOf(this.getGroundObject()) + ")";
    }
}

