package net.solace.sdk.movement;

import java.util.List;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.movement.IReachable;

public class Reachable {
    private static final IReachable REACHABLE = Static.getReachable();

    public static boolean check(int flag, int checkFlag) {
        return REACHABLE.check(flag, checkFlag);
    }

    public static boolean isObstacle(int endFlag) {
        return REACHABLE.isObstacle(endFlag);
    }

    public static boolean isObstacle(WorldPoint worldPoint) {
        return REACHABLE.isObstacle(worldPoint);
    }

    public static int getCollisionFlag(WorldPoint point) {
        return REACHABLE.getCollisionFlag(point);
    }

    public static boolean isWalled(Direction direction, int startFlag) {
        return REACHABLE.isWalled(direction, startFlag);
    }

    public static boolean isWalled(WorldPoint source, WorldPoint destination) {
        return REACHABLE.isWalled(source, destination);
    }

    public static boolean isWalled(ITile source, ITile destination) {
        return REACHABLE.isWalled(source, destination);
    }

    public static boolean hasDoor(WorldPoint source, Direction direction) {
        return REACHABLE.hasDoor(source, direction);
    }

    public static boolean hasDoor(ITile source, Direction direction) {
        return REACHABLE.hasDoor(source, direction);
    }

    public static boolean isDoored(ITile source, ITile destination) {
        return REACHABLE.isDoored(source, destination);
    }

    public static boolean canWalk(Direction direction, int startFlag, int endFlag) {
        return REACHABLE.canWalk(direction, startFlag, endFlag);
    }

    public static WorldPoint getNeighbour(Direction direction, WorldPoint source) {
        return REACHABLE.getNeighbour(direction, source);
    }

    public static List<WorldPoint> getVisitedTiles(Locatable locatable) {
        return REACHABLE.getVisitedTiles(locatable);
    }

    public static List<WorldPoint> getVisitedTiles(WorldPoint worldPoint) {
        return REACHABLE.getVisitedTiles(worldPoint);
    }

    public static boolean isInteractable(Locatable locatable) {
        return REACHABLE.isInteractable(locatable);
    }

    public static boolean isWalkable(WorldPoint worldPoint) {
        return REACHABLE.isWalkable(worldPoint);
    }
}

