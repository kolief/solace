package net.solace.impl.movement;

import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.Calculations;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.entities.ITiles;
import net.solace.api.movement.IReachable;

import java.util.List;

@RequiredArgsConstructor
public class ReachableImpl implements IReachable {
    private final IClient client;
    private final ITiles tiles;

    public boolean check(int flag, int checkFlag) {
        return Calculations.check(flag, checkFlag);
    }

    public boolean isObstacle(int endFlag) {
        return Calculations.isObstacle(endFlag);
    }

    public boolean isObstacle(WorldPoint worldPoint) {
        return Calculations.isObstacle(client.getWrapped(), worldPoint);
    }

    public int getCollisionFlag(WorldPoint point) {
        return Calculations.getCollisionFlag(client.getWrapped(), point);
    }

    public boolean isWalled(Direction direction, int startFlag) {
        return Calculations.isWalled(direction, startFlag);
    }

    public boolean isWalled(WorldPoint source, WorldPoint destination) {
        var sourceTile = tiles.getAt(source);
        var destTile = tiles.getAt(destination);

        if (sourceTile == null || destTile == null) {
            return true;
        }

        return isWalled(sourceTile, destTile);
    }

    public boolean isWalled(ITile source, ITile destination) {
        return Calculations.isWalled(source, destination);
    }

    public boolean hasDoor(WorldPoint source, Direction direction) {
        var tile = tiles.getAt(source);
        if (tile == null) {
            return false;
        }

        return hasDoor(tile, direction);
    }

    public boolean hasDoor(ITile source, Direction direction) {
        var wall = source.getWallObject();
        if (wall == null) {
            return false;
        }

        return isWalled(direction, getCollisionFlag(source.getWorldLocation())) && wall.hasAction("Open", "Close");
    }

    public boolean isDoored(ITile source, ITile destination) {
        var wall = source.getWallObject();
        if (wall == null) {
            return false;
        }

        return isWalled(source, destination) && wall.hasAction("Open", "Pass");
    }

    public boolean canWalk(Direction direction, int startFlag, int endFlag) {
        return Calculations.canWalk(direction, startFlag, endFlag);
    }

    public WorldPoint getNeighbour(Direction direction, WorldPoint source) {
        return Calculations.getNeighbour(direction, source);
    }

    public List<WorldPoint> getVisitedTiles(Locatable locatable) {
        return Calculations.getVisitedTiles(client.getWrapped(), client.getLocalPlayer().getWorldLocation(), locatable);
    }

    public List<WorldPoint> getVisitedTiles(WorldPoint worldPoint) {
        return Calculations.getVisitedTiles(client.getWrapped(), client.getLocalPlayer().getWorldLocation(), worldPoint);
    }

    public boolean isInteractable(Locatable locatable) {
        return Calculations.isInteractable(client.getWrapped(), client.getLocalPlayer().getWorldLocation(), locatable);
    }

    public boolean isWalkable(WorldPoint worldPoint) {
        return Calculations.isWalkable(client.getWrapped(), client.getLocalPlayer().getWorldLocation(), worldPoint);
    }
}
