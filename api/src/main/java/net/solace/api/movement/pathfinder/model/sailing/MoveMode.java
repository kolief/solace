package net.solace.api.movement.pathfinder.model.sailing;

import net.solace.api.Static;

public enum MoveMode {
    NONE(0),
    NORMAL(1),
    FAST(2),
    REVERSE(3),
    STILL_WITH_WIND_CATCHER(4);

    private final int value;

    public boolean isActive() {
        return Static.getVars().getBit(19175) == this.value;
    }

    public static MoveMode getCurrent() {
        int var = Static.getVars().getBit(19175);
        for (MoveMode mode : MoveMode.values()) {
            if (mode.value != var) continue;
            return mode;
        }
        return null;
    }

    private MoveMode(int value) {
        this.value = value;
    }
}

