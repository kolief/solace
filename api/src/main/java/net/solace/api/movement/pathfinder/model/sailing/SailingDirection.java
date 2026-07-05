package net.solace.api.movement.pathfinder.model.sailing;

import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.WorldViewUtil;

public enum SailingDirection {
    SOUTH(0),
    SOUTH_SOUTH_WEST(1),
    SOUTH_WEST(2),
    WEST_SOUTH_WEST(3),
    WEST(4),
    WEST_NORTH_WEST(5),
    NORTH_WEST(6),
    NORTH_NORTH_WEST(7),
    NORTH(8),
    NORTH_NORTH_EAST(9),
    NORTH_EAST(10),
    EAST_NORTH_EAST(11),
    EAST(12),
    EAST_SOUTH_EAST(13),
    SOUTH_EAST(14),
    SOUTH_SOUTH_EAST(15);

    private final int code;

    public static SailingDirection fromCode(int code) {
        for (SailingDirection d : SailingDirection.values()) {
            if (d.code != code) continue;
            return d;
        }
        return null;
    }

    public static SailingDirection fromAngle(int angle) {
        int code = angle / 128;
        return SailingDirection.fromCode(code);
    }

    public static SailingDirection getOptimalHeading(WorldPoint from, WorldPoint target) {
        double headingDegrees;
        int deltaX = target.getX() - from.getX();
        int deltaY = target.getY() - from.getY();
        double angleRadians = Math.atan2(deltaY, deltaX);
        double angleDegrees = Math.toDegrees(angleRadians);
        for (headingDegrees = 270.0 - angleDegrees; headingDegrees < 0.0; headingDegrees += 360.0) {
        }
        while (headingDegrees >= 360.0) {
            headingDegrees -= 360.0;
        }
        int headingValue = (int)((headingDegrees + 11.25) / 22.5) % 16;
        for (SailingDirection heading : SailingDirection.values()) {
            if (heading.getCode() != headingValue) continue;
            return heading;
        }
        return SOUTH;
    }

    public static SailingDirection getOptimalHeading(WorldPoint target) {
        return SailingDirection.getOptimalHeading(WorldViewUtil.getTopWorldLocation(), target);
    }

    public int getCode() {
        return this.code;
    }

    private SailingDirection(int code) {
        this.code = code;
    }
}

