package net.solace.api.events;

import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.actors.INPC;

public final class NpcDespawned {
    private final INPC npc;

    public IActor getActor() {
        return this.npc;
    }

    public NpcDespawned(INPC npc) {
        this.npc = npc;
    }

    public INPC getNpc() {
        return this.npc;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NpcDespawned)) {
            return false;
        }
        NpcDespawned other = (NpcDespawned)o;
        INPC this$npc = this.getNpc();
        INPC other$npc = other.getNpc();
        return !(this$npc == null ? other$npc != null : !this$npc.equals(other$npc));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        INPC $npc = this.getNpc();
        result = result * 59 + ($npc == null ? 43 : $npc.hashCode());
        return result;
    }

    public String toString() {
        return "NpcDespawned(npc=" + String.valueOf(this.getNpc()) + ")";
    }
}

