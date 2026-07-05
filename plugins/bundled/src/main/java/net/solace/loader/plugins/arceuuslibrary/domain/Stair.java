package net.solace.loader.plugins.arceuuslibrary.domain;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public enum Stair {
    BNW_UP(true, new WorldPoint(1614, 3825, 0)),
    MNW_UP(true, new WorldPoint(1612, 3818, 1)),
    TNW_DOWN(false, new WorldPoint(1609, 3818, 2)),
    MNW_DOWN(false, new WorldPoint(1611, 3827, 1)),
    BNE_UP(true, new WorldPoint(1643, 3819, 0)),
    MNE_UP(true, new WorldPoint(1644, 3828, 1)),
    TNE_DOWN(false, new WorldPoint(1646, 3828, 2)),
    MNE_DOWN(false, new WorldPoint(1643, 3821, 1)),
    BSW_UP(true, new WorldPoint(1614, 3796, 0)),
    MSW_UP(true, new WorldPoint(1621, 3792, 1)),
    TSW_DOWN(false, new WorldPoint(1621, 3794, 2)),
    MSW_DOWN(false, new WorldPoint(1611, 3794, 1)),
    MC_UP(true, new WorldPoint(1638, 3807, 1)),
    TC_DOWN(true, new WorldPoint(1638, 3804, 2));

    private final boolean isGoingUp;
    @Getter
    private final WorldPoint WorldPoint;

    Stair(boolean isGoingUp, WorldPoint WorldPoint) {
        this.isGoingUp = isGoingUp;
        this.WorldPoint = WorldPoint;
    }

    public boolean isGoingUp() {
        return isGoingUp;
    }

}
