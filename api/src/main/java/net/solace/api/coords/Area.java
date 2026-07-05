package net.solace.api.coords;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class Area {
    public static WorldArea offsetFrom(WorldArea area, int offset) {
        return new WorldArea(area.getX() - offset, area.getY() - offset, area.getWidth() + 2 * offset, area.getHeight() + 2 * offset, area.getPlane());
    }

    public static WorldPoint centerOf(WorldArea area) {
        return new WorldPoint(area.getX() + area.getWidth() / 2, area.getY() + area.getHeight() / 2, area.getPlane());
    }

    public static WorldPoint randomPointIn(WorldArea area) {
        return new WorldPoint(area.getX() + (int)(Math.random() * (double)area.getWidth()), area.getY() + (int)(Math.random() * (double)area.getHeight()), area.getPlane());
    }

    public static WorldArea fromCorners(WorldPoint swLocation, WorldPoint neLocation) {
        int x = swLocation.getX();
        int y = swLocation.getY();
        int plane = swLocation.getPlane();
        int width = neLocation.getX() - swLocation.getX();
        int height = neLocation.getY() - swLocation.getY();
        return new WorldArea(x, y, width, height, plane);
    }
}

