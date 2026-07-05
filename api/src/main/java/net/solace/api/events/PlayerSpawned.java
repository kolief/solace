package net.solace.api.events;

import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.actors.IPlayer;

public final class PlayerSpawned {
    private final IPlayer player;

    public IActor getActor() {
        return this.player;
    }

    public PlayerSpawned(IPlayer player) {
        this.player = player;
    }

    public IPlayer getPlayer() {
        return this.player;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerSpawned)) {
            return false;
        }
        PlayerSpawned other = (PlayerSpawned)o;
        IPlayer this$player = this.getPlayer();
        IPlayer other$player = other.getPlayer();
        return !(this$player == null ? other$player != null : !this$player.equals(other$player));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        IPlayer $player = this.getPlayer();
        result = result * 59 + ($player == null ? 43 : $player.hashCode());
        return result;
    }

    public String toString() {
        return "PlayerSpawned(player=" + String.valueOf(this.getPlayer()) + ")";
    }
}

