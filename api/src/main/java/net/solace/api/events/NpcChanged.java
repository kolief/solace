package net.solace.api.events;

import net.runelite.api.NPCComposition;
import net.solace.api.domain.actors.INPC;

public final class NpcChanged {
    private final INPC npc;
    private final NPCComposition old;

    public NpcChanged(INPC npc, NPCComposition old) {
        this.npc = npc;
        this.old = old;
    }

    public INPC getNpc() {
        return this.npc;
    }

    public NPCComposition getOld() {
        return this.old;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NpcChanged)) {
            return false;
        }
        NpcChanged other = (NpcChanged)o;
        INPC this$npc = this.getNpc();
        INPC other$npc = other.getNpc();
        if (this$npc == null ? other$npc != null : !this$npc.equals(other$npc)) {
            return false;
        }
        NPCComposition this$old = this.getOld();
        NPCComposition other$old = other.getOld();
        return !(this$old == null ? other$old != null : !this$old.equals(other$old));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        INPC $npc = this.getNpc();
        result = result * 59 + ($npc == null ? 43 : $npc.hashCode());
        NPCComposition $old = this.getOld();
        result = result * 59 + ($old == null ? 43 : $old.hashCode());
        return result;
    }

    public String toString() {
        return "NpcChanged(npc=" + String.valueOf(this.getNpc()) + ", old=" + String.valueOf(this.getOld()) + ")";
    }
}

