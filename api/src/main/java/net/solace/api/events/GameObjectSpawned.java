package net.solace.api.events;

import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.ITile;

public class GameObjectSpawned {
    private ITile tile;
    private IGameObject gameObject;

    public ITile getTile() {
        return this.tile;
    }

    public IGameObject getGameObject() {
        return this.gameObject;
    }

    public void setTile(ITile tile) {
        this.tile = tile;
    }

    public void setGameObject(IGameObject gameObject) {
        this.gameObject = gameObject;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GameObjectSpawned)) {
            return false;
        }
        GameObjectSpawned other = (GameObjectSpawned)o;
        if (!other.canEqual(this)) {
            return false;
        }
        ITile this$tile = this.getTile();
        ITile other$tile = other.getTile();
        if (this$tile == null ? other$tile != null : !this$tile.equals(other$tile)) {
            return false;
        }
        IGameObject this$gameObject = this.getGameObject();
        IGameObject other$gameObject = other.getGameObject();
        return !(this$gameObject == null ? other$gameObject != null : !this$gameObject.equals(other$gameObject));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GameObjectSpawned;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ITile $tile = this.getTile();
        result = result * 59 + ($tile == null ? 43 : $tile.hashCode());
        IGameObject $gameObject = this.getGameObject();
        result = result * 59 + ($gameObject == null ? 43 : $gameObject.hashCode());
        return result;
    }

    public String toString() {
        return "GameObjectSpawned(tile=" + String.valueOf(this.getTile()) + ", gameObject=" + String.valueOf(this.getGameObject()) + ")";
    }
}

