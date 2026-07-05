package net.solace.api.containers;

import java.util.Collection;
import net.runelite.api.Player;
import net.solace.api.domain.actors.IPlayer;

public interface PlayerContainer {
    public IPlayer get(int var1);

    public Collection<IPlayer> getAll();

    public IPlayer getHintArrowed();

    public IPlayer getLocalPlayer();

    public IPlayer create(Player var1);
}

