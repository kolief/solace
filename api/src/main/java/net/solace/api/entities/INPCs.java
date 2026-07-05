package net.solace.api.entities;

import net.solace.api.domain.actors.INPC;
import net.solace.api.entities.SceneEntityProvider;

public interface INPCs
extends SceneEntityProvider<INPC> {
    public INPC getHintArrowed();

    public INPC get(int var1);
}

