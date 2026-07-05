package net.solace.api.events;

import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.actors.INPC;

public final class NpcSpawned {
    private final INPC npc;

    public IActor getActor() {
        return this.npc;
    }

    public NpcSpawned(INPC npc) {
        this.npc = npc;
    }

    public INPC getNpc() {
        return this.npc;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NpcSpawned)) {
            return false;
        }
        NpcSpawned other = (NpcSpawned)o;
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
        return "NpcSpawned(npc=" + String.valueOf(this.getNpc()) + ")";
    }
}

