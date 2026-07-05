package net.solace.api.domain.actors;

import javax.annotation.Nullable;
import net.runelite.api.EntityOps;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.solace.api.domain.Transformable;
import net.solace.api.domain.actors.IActor;

public interface INPC
extends NPC,
IActor,
Transformable<NPCComposition> {
    public HeadIcon[] getOverheadIcons();

    public void update(NPC var1);

    @Nullable
    public EntityOps getOps();

    default public HeadIcon getOverheadIcon() {
        HeadIcon[] icons = this.getOverheadIcons();
        return icons.length > 0 ? icons[0] : null;
    }
}

