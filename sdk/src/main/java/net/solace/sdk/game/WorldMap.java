package net.solace.sdk.game;

import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.game.IWorldMap;

public class WorldMap {
    private static final IWorldMap WORLD_MAP = Static.getWorldMap();

    public static WorldPoint getMouseLocation() {
        return WORLD_MAP.getMouseLocation();
    }
}

