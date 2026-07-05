package net.solace.api.game;

import net.runelite.api.coords.WorldPoint;
import net.solace.api.game.HouseLocation;

public interface IHouse {
    public HouseLocation getLocation();

    public WorldPoint getOutsideLocation();

    public boolean isInside();

    public boolean canEnter();

    public void enter();
}

