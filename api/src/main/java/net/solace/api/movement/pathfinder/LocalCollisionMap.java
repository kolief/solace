package net.solace.api.movement.pathfinder;

import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.entities.ITiles;
import net.solace.api.movement.IReachable;
import net.solace.api.movement.pathfinder.CollisionMap;

public class LocalCollisionMap
implements CollisionMap {
    private final IReachable reachable = Static.getReachable();
    private final ITiles tiles = Static.getTiles();
    private final boolean blockDoors;

    @Override
    public boolean n(int x, int y, int z) {
        WorldPoint current = new WorldPoint(x, y, z);
        if (this.reachable.isObstacle(current)) {
            return false;
        }
        ITile currentTile = this.tiles.getAt(current);
        ITile destinationTile = this.tiles.getAt(current.dy(1));
        if (currentTile == null || destinationTile == null) {
            return false;
        }
        if (!this.blockDoors && (this.reachable.isDoored(currentTile, destinationTile) || this.reachable.isDoored(destinationTile, currentTile))) {
            return !this.reachable.isObstacle(destinationTile.getWorldLocation());
        }
        return this.reachable.canWalk(Direction.NORTH, this.reachable.getCollisionFlag(current), this.reachable.getCollisionFlag(current.dy(1)));
    }

    @Override
    public boolean e(int x, int y, int z) {
        WorldPoint current = new WorldPoint(x, y, z);
        if (this.reachable.isObstacle(current)) {
            return false;
        }
        ITile currentTile = this.tiles.getAt(current);
        ITile destinationTile = this.tiles.getAt(current.dx(1));
        if (currentTile == null || destinationTile == null) {
            return false;
        }
        if (!this.blockDoors && (this.reachable.isDoored(currentTile, destinationTile) || this.reachable.isDoored(destinationTile, currentTile))) {
            return !this.reachable.isObstacle(destinationTile.getWorldLocation());
        }
        return this.reachable.canWalk(Direction.EAST, this.reachable.getCollisionFlag(current), this.reachable.getCollisionFlag(current.dx(1)));
    }

    public LocalCollisionMap(boolean blockDoors) {
        this.blockDoors = blockDoors;
    }
}

