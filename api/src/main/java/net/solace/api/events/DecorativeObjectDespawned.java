package net.solace.api.events;

import net.solace.api.domain.tiles.IDecorativeObject;
import net.solace.api.domain.tiles.ITile;

public final class DecorativeObjectDespawned {
    private final ITile tile;
    private final IDecorativeObject decorativeObject;

    public DecorativeObjectDespawned(ITile tile, IDecorativeObject decorativeObject) {
        this.tile = tile;
        this.decorativeObject = decorativeObject;
    }

    public ITile getTile() {
        return this.tile;
    }

    public IDecorativeObject getDecorativeObject() {
        return this.decorativeObject;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DecorativeObjectDespawned)) {
            return false;
        }
        DecorativeObjectDespawned other = (DecorativeObjectDespawned)o;
        ITile this$tile = this.getTile();
        ITile other$tile = other.getTile();
        if (this$tile == null ? other$tile != null : !this$tile.equals(other$tile)) {
            return false;
        }
        IDecorativeObject this$decorativeObject = this.getDecorativeObject();
        IDecorativeObject other$decorativeObject = other.getDecorativeObject();
        return !(this$decorativeObject == null ? other$decorativeObject != null : !this$decorativeObject.equals(other$decorativeObject));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ITile $tile = this.getTile();
        result = result * 59 + ($tile == null ? 43 : $tile.hashCode());
        IDecorativeObject $decorativeObject = this.getDecorativeObject();
        result = result * 59 + ($decorativeObject == null ? 43 : $decorativeObject.hashCode());
        return result;
    }

    public String toString() {
        return "DecorativeObjectDespawned(tile=" + String.valueOf(this.getTile()) + ", decorativeObject=" + String.valueOf(this.getDecorativeObject()) + ")";
    }
}

