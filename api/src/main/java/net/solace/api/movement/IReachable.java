package net.solace.api.movement;

import java.util.List;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.tiles.ITile;

public interface IReachable {
    public boolean check(int var1, int var2);

    public boolean isObstacle(int var1);

    public boolean isObstacle(WorldPoint var1);

    public int getCollisionFlag(WorldPoint var1);

    public boolean isWalled(Direction var1, int var2);

    public boolean isWalled(WorldPoint var1, WorldPoint var2);

    public boolean isWalled(ITile var1, ITile var2);

    public boolean hasDoor(WorldPoint var1, Direction var2);

    public boolean hasDoor(ITile var1, Direction var2);

    public boolean isDoored(ITile var1, ITile var2);

    public boolean canWalk(Direction var1, int var2, int var3);

    public WorldPoint getNeighbour(Direction var1, WorldPoint var2);

    public List<WorldPoint> getVisitedTiles(Locatable var1);

    public List<WorldPoint> getVisitedTiles(WorldPoint var1);

    public boolean isInteractable(Locatable var1);

    public boolean isWalkable(WorldPoint var1);
}

