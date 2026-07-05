package net.solace.sdk.game;

import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.game.HouseLocation;
import net.solace.api.game.IHouse;

public class House {
    private static final IHouse HOUSE = Static.getHouse();

    public static HouseLocation getLocation() {
        return HOUSE.getLocation();
    }

    public static WorldPoint getOutsideLocation() {
        return HOUSE.getOutsideLocation();
    }

    public static boolean isInside() {
        return HOUSE.isInside();
    }

    public static boolean canEnter() {
        return HOUSE.canEnter();
    }

    public static void enter() {
        HOUSE.enter();
    }
}

