package net.solace.sdk.coords;

import net.runelite.api.coords.WorldPoint;

public class Coord {
    public static float distanceToHypotenuse(WorldPoint origin, WorldPoint target) {
        if (origin.getPlane() != target.getPlane()) {
            return Float.MAX_VALUE;
        }
        return Coord.distanceTo2DHypotenuse(origin, target);
    }

    public static float distanceTo2DHypotenuse(WorldPoint origin, WorldPoint target) {
        return (float)Math.hypot(origin.getX() - target.getX(), origin.getY() - target.getY());
    }
}

