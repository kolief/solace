package net.solace.api.containers;

import java.util.Collection;
import net.runelite.api.NPC;
import net.solace.api.domain.actors.INPC;

public interface NpcContainer {
    public INPC get(int var1);

    public Collection<INPC> getAll();

    public INPC getHintArrowed();

    public INPC getFollower();

    public INPC create(NPC var1);
}

