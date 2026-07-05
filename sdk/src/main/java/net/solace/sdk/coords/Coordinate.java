package net.solace.sdk.coords;

import net.runelite.api.Point;

public class Coordinate {
    public static Point offsetFrom(Point point, int x, int y) {
        return new Point(point.getX() + x, point.getY() + y);
    }
}

