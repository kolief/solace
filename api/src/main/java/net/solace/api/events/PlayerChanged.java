package net.solace.api.events;

import net.solace.api.domain.actors.IPlayer;

public final class PlayerChanged {
    private final IPlayer player;

    public PlayerChanged(IPlayer player) {
        this.player = player;
    }

    public IPlayer getPlayer() {
        return this.player;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerChanged)) {
            return false;
        }
        PlayerChanged other = (PlayerChanged)o;
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
        return "PlayerChanged(player=" + String.valueOf(this.getPlayer()) + ")";
    }
}

