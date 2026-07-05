package net.solace.api.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.IWallObject;

public class Calculations {
    private static final int MAX_ATTEMPTED_TILES = 4096;
    private static final Direction[] DIRECTIONS = Direction.values();

    public static boolean check(int flag, int checkFlag) {
        return (flag & checkFlag) != 0;
    }

    public static boolean isObstacle(int endFlag) {
        return Calculations.check(endFlag, 2359552);
    }

    public static boolean isObstacle(Client client, WorldPoint worldPoint) {
        return Calculations.isObstacle(Calculations.getCollisionFlag(client, worldPoint));
    }

    public static int getCollisionFlag(Client client, WorldPoint point) {
        CollisionData[] collisionMaps = client.getCollisionMaps();
        if (collisionMaps == null) {
            throw new IllegalStateException("Collision maps are not loaded");
        }
        CollisionData collisionData = collisionMaps[client.getPlane()];
        if (collisionData == null) {
            throw new IllegalStateException("Collision data is not loaded for this plane");
        }
        LocalPoint localPoint = LocalPoint.fromWorld((Client)client, (WorldPoint)point);
        if (localPoint == null) {
            return 2359552;
        }
        return collisionData.getFlags()[localPoint.getSceneX()][localPoint.getSceneY()];
    }

    public static boolean isWalled(Direction direction, int startFlag) {
        switch (direction) {
            case NORTH: {
                return Calculations.check(startFlag, 2);
            }
            case SOUTH: {
                return Calculations.check(startFlag, 32);
            }
            case WEST: {
                return Calculations.check(startFlag, 128);
            }
            case EAST: {
                return Calculations.check(startFlag, 8);
            }
        }
        throw new IllegalArgumentException();
    }

    public static boolean isWalled(ITile source, ITile destination) {
        IWallObject wall = source.getWallObject();
        if (wall == null) {
            return false;
        }
        WorldPoint a = source.getWorldLocation();
        WorldPoint b = destination.getWorldLocation();
        switch (wall.getOrientationA()) {
            case 1: {
                return a.dx(-1).equals((Object)b) || a.dx(-1).dy(1).equals((Object)b) || a.dx(-1).dy(-1).equals((Object)b);
            }
            case 2: {
                return a.dy(1).equals((Object)b) || a.dx(-1).dy(1).equals((Object)b) || a.dx(1).dy(1).equals((Object)b);
            }
            case 4: {
                return a.dx(1).equals((Object)b) || a.dx(1).dy(1).equals((Object)b) || a.dx(1).dy(-1).equals((Object)b);
            }
            case 8: {
                return a.dy(-1).equals((Object)b) || a.dx(-1).dy(-1).equals((Object)b) || a.dx(-1).dy(1).equals((Object)b);
            }
        }
        return false;
    }

    public static boolean isDoored(ITile source, ITile destination) {
        IWallObject wall = source.getWallObject();
        if (wall == null) {
            return false;
        }
        return Calculations.isWalled(source, destination) && wall.hasAction(new String[]{"Open"});
    }

    public static boolean canWalk(Direction direction, int startFlag, int endFlag) {
        if (Calculations.isObstacle(endFlag)) {
            return false;
        }
        return !Calculations.isWalled(direction, startFlag);
    }

    public static WorldPoint getNeighbour(Direction direction, WorldPoint source) {
        switch (direction) {
            case NORTH: {
                return source.dy(1);
            }
            case SOUTH: {
                return source.dy(-1);
            }
            case WEST: {
                return source.dx(-1);
            }
            case EAST: {
                return source.dx(1);
            }
        }
        throw new IllegalArgumentException();
    }

    public static List<WorldPoint> getNeighbours(Client client, WorldPoint source, Locatable locatableDestination) {
        int sourceCollisionFlag = Calculations.getCollisionFlag(client, source);
        ArrayList<WorldPoint> out = new ArrayList<WorldPoint>(4);
        for (Direction dir : DIRECTIONS) {
            WorldPoint neighbour = Calculations.getNeighbour(dir, source);
            if (!neighbour.isInScene(client)) continue;
            if (locatableDestination != null) {
                boolean containsPoint;
                if (locatableDestination instanceof IGameObject) {
                    IGameObject gameObject = (IGameObject)locatableDestination;
                    if (gameObject.getWorldArea() == null) continue;
                    containsPoint = gameObject.getWorldArea().contains(neighbour);
                } else {
                    containsPoint = locatableDestination.getWorldLocation().equals((Object)neighbour);
                }
                if (containsPoint && (!Calculations.isWalled(dir, sourceCollisionFlag) || locatableDestination instanceof IWallObject)) {
                    out.add(neighbour);
                    continue;
                }
            }
            if (!Calculations.canWalk(dir, sourceCollisionFlag, Calculations.getCollisionFlag(client, neighbour))) continue;
            out.add(neighbour);
        }
        return out;
    }

    public static List<WorldPoint> getVisitedTiles(Client client, WorldPoint source, WorldPoint destination, Locatable locatableDestination) {
        WorldPoint dest;
        WorldPoint worldPoint = dest = locatableDestination != null ? locatableDestination.getWorldLocation() : destination;
        if (!dest.isInScene(client) || source.getPlane() != dest.getPlane()) {
            return Collections.emptyList();
        }
        ArrayList<WorldPoint> visitedTiles = new ArrayList<WorldPoint>(4096);
        HashSet<WorldPoint> visitedSet = new HashSet<WorldPoint>(256);
        LinkedList<WorldPoint> queue = new LinkedList<WorldPoint>();
        queue.add(source);
        visitedSet.add(source);
        while (!queue.isEmpty() && visitedTiles.size() <= 4096) {
            WorldPoint current = (WorldPoint)queue.poll();
            visitedTiles.add(current);
            if (current.equals((Object)dest)) break;
            for (WorldPoint neighbor : Calculations.getNeighbours(client, current, locatableDestination)) {
                if (visitedSet.contains(neighbor)) continue;
                queue.add(neighbor);
                visitedSet.add(neighbor);
            }
        }
        return visitedTiles;
    }

    public static List<WorldPoint> getVisitedTiles(Client client, WorldPoint source, Locatable locatable) {
        return Calculations.getVisitedTiles(client, source, null, locatable);
    }

    public static List<WorldPoint> getVisitedTiles(Client client, WorldPoint source, WorldPoint worldPoint) {
        return Calculations.getVisitedTiles(client, source, worldPoint, null);
    }

    public static boolean isInteractable(Client client, WorldPoint source, Locatable locatable) {
        return Calculations.getVisitedTiles(client, source, locatable).contains(locatable.getWorldLocation());
    }

    public static boolean isWalkable(Client client, WorldPoint source, WorldPoint worldPoint) {
        return Calculations.getVisitedTiles(client, source, worldPoint).contains(worldPoint);
    }
}

