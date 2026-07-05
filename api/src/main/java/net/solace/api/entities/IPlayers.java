package net.solace.api.entities;

import net.solace.api.domain.actors.IPlayer;
import net.solace.api.entities.SceneEntityProvider;

public interface IPlayers
extends SceneEntityProvider<IPlayer> {
    public IPlayer get(int var1);

    public IPlayer getLocal();

    public IPlayer getHintArrowed();
}

