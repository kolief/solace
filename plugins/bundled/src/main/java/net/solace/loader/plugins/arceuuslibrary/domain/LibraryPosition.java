package net.solace.loader.plugins.arceuuslibrary.domain;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.coords.Area;

public class LibraryPosition {
    private final static int REGION = 6459;
    private static final WorldArea LIBRARY_GROUND_FLOOR = Area.fromCorners(new WorldPoint(1604, 3782, 0), new WorldPoint(1659, 3833, 0));
    private static final WorldArea LIBRARY_FIRST_FLOOR = Area.fromCorners(new WorldPoint(1604, 3782, 1), new WorldPoint(1659, 3833, 1));
    private static final WorldArea LIBRARY_SECOND_FLOOR = Area.fromCorners(new WorldPoint(1604, 3782, 2), new WorldPoint(1659, 3833, 2));

    public static int getRegion() {
        return REGION;
    }

    public static int getRegion(WorldPoint pos) {
        return ((pos.getX() >> 6) << 8) | (pos.getY() >> 6);
    }

    public static WorldArea getArea(int floor) {
        switch (floor) {
            case 0:
                return LIBRARY_GROUND_FLOOR;
            case 1:
                return LIBRARY_FIRST_FLOOR;
            case 2:
                return LIBRARY_SECOND_FLOOR;
        }
        return null;
    }
}
