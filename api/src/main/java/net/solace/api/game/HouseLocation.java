package net.solace.api.game;

import net.runelite.api.coords.WorldPoint;

public enum HouseLocation {
    RIMMINGTON(new WorldPoint(2953, 3224, 0), 1),
    TAVERLY(new WorldPoint(2893, 3465, 0), 2),
    POLLNIVEACH(new WorldPoint(3339, 3004, 0), 3),
    RELLEKKA(new WorldPoint(2668, 3632, 0), 4),
    BRIMHAVEN(new WorldPoint(2756, 3176, 0), 5),
    YANILLE(new WorldPoint(2544, 3097, 0), 6),
    HOSIDIUS(new WorldPoint(1741, 3518, 0), 8),
    PRIFDDINAS(new WorldPoint(3239, 6077, 0), 9),
    ALDARIN(new WorldPoint(1422, 2963, 0), 13);

    private final WorldPoint location;
    private final int index;

    private HouseLocation(WorldPoint location, int index) {
        this.location = location;
        this.index = index;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public int getIndex() {
        return this.index;
    }
}

