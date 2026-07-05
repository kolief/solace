package net.solace.api.movement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.WalkOptions;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.model.Teleport;

public interface IMovement {
    public void setDestination(int var1, int var2);

    @Nullable
    public WorldPoint getDestination();

    public boolean isWalking();

    public void walk(WorldPoint var1, WalkOptions var2);

    default public void walk(WorldPoint worldPoint) {
        this.walk(worldPoint, WalkOptions.builder().build());
    }

    public boolean walkTo(WorldPoint var1);

    public boolean walkTo(WorldArea var1);

    public boolean walkTo(WorldArea var1, CollisionMap var2, boolean var3);

    public boolean walkTo(WorldArea var1, WalkOptions var2);

    public boolean isRunEnabled();

    public void toggleRun();

    public boolean isStaminaBoosted();

    public int getRunEnergy();

    public TilePath getPath(Collection<WorldPoint> var1, WorldArea var2, CollisionMap var3, boolean var4, boolean var5, HashMap<WorldPoint, Teleport> var6);

    public TilePath getPath(Collection<WorldPoint> var1, WorldArea var2, WalkOptions var3, HashMap<WorldPoint, Teleport> var4);

    default public TilePath getPath(WorldPoint destination) {
        return this.getPath(List.of(Static.getPlayers().getLocal().getWorldLocation()), destination);
    }

    default public TilePath getPath(WorldPoint destination, CollisionMap collisionMap) {
        return this.getPath(List.of(Static.getPlayers().getLocal().getWorldLocation()), destination, collisionMap);
    }

    default public TilePath getPath(Collection<WorldPoint> startPoints, WorldPoint destination) {
        return this.getPath(startPoints, destination, (CollisionMap)Static.getGlobalCollisionMap());
    }

    default public TilePath getPath(Collection<WorldPoint> startPoints, WorldPoint destination, CollisionMap collisionMap) {
        return this.getPath(startPoints, destination.toWorldArea(), collisionMap);
    }

    default public TilePath getPath(WorldArea destination) {
        return this.getPath(List.of(Static.getPlayers().getLocal().getWorldLocation()), destination);
    }

    default public TilePath getPath(WorldArea destination, CollisionMap collisionMap) {
        return this.getPath(List.of(Static.getPlayers().getLocal().getWorldLocation()), destination, collisionMap);
    }

    default public TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination) {
        return this.getPath(startPoints, destination, (CollisionMap)Static.getGlobalCollisionMap());
    }

    default public TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap) {
        return this.getPath(startPoints, destination, collisionMap, false);
    }

    default public TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, boolean useCache) {
        return this.getPath(startPoints, destination, Static.getGlobalCollisionMap(), useCache);
    }

    default public TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean useCache) {
        return this.getPath(startPoints, destination, collisionMap, useCache, true, new HashMap<WorldPoint, Teleport>());
    }

    public WorldPoint getNearestWalkableTile(WorldPoint var1, CollisionMap var2, Predicate<WorldPoint> var3);

    default public WorldPoint getNearestWalkableTile(WorldPoint source, Predicate<WorldPoint> filter) {
        return this.getNearestWalkableTile(source, Static.getGlobalCollisionMap(), filter);
    }

    default public WorldPoint getNearestWalkableTile(WorldPoint source) {
        return this.getNearestWalkableTile(source, Static.getGlobalCollisionMap(), x -> true);
    }

    default public WorldPoint getNearestWalkableTile(WorldPoint source, CollisionMap collisionMap) {
        return this.getNearestWalkableTile(source, collisionMap, x -> true);
    }
}

