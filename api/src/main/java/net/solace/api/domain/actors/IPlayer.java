package net.solace.api.domain.actors;

import net.runelite.api.Player;
import net.solace.api.domain.actors.IActor;

public interface IPlayer
extends Player,
IActor {
    public void update(Player var1);
}

