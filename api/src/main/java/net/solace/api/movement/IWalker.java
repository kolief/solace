package net.solace.api.movement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.WalkOptions;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.movement.pathfinder.model.Transport;

public interface IWalker {
    public void walk(WorldPoint var1, WalkOptions var2);

    default public void walk(WorldPoint worldPoint) {
        this.walk(worldPoint, WalkOptions.builder().build());
    }

    public boolean walkTo(WorldArea var1, CollisionMap var2, boolean var3);

    public boolean walkTo(WorldArea var1, WalkOptions var2);

    public TilePath getCurrentPath();

    public TilePath getLastPath();

    public void setLastPath(TilePath var1);

    public void setCurrentPath(TilePath var1);

    public TilePath buildPath(Collection<WorldPoint> var1, WorldArea var2, WalkOptions var3, HashMap<WorldPoint, Teleport> var4, Map<WorldPoint, List<Transport>> var5);

    default public TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, WalkOptions options) {
        LinkedHashMap<WorldPoint, Teleport> teleports = this.buildExperimentalTeleportLinks(destination, options);
        return this.buildPath(startPoints, destination, options, teleports, new HashMap<WorldPoint, List<Transport>>());
    }

    default public TilePath buildPath(WorldArea destination, WalkOptions options) {
        IPlayer local = Static.getPlayers().getLocal();
        ArrayList<WorldPoint> startPoints = new ArrayList<WorldPoint>();
        startPoints.add(local.getWorldLocation());
        return this.buildPath(startPoints, destination, options);
    }

    default public TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness, boolean useCache, boolean useTransports, HashMap<WorldPoint, Teleport> teleports) {
        return this.buildPath(startPoints, destination, WalkOptions.builder().avoidWilderness(avoidWilderness).useCache(useCache).useTransports(useTransports).collisionMap(collisionMap).build(), teleports, new HashMap<WorldPoint, List<Transport>>());
    }

    default public TilePath buildPath(WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness) {
        IPlayer local = Static.getPlayers().getLocal();
        LinkedHashMap<WorldPoint, Teleport> teleports = this.buildTeleportLinks(destination);
        ArrayList<WorldPoint> startPoints = new ArrayList<WorldPoint>();
        startPoints.add(local.getWorldLocation());
        return this.buildPath(startPoints, destination, collisionMap, avoidWilderness, false, true, teleports);
    }

    default public TilePath buildPath(WorldArea destination, CollisionMap collisionMap) {
        return this.buildPath(destination, collisionMap, Static.getSolaceConfig().avoidWilderness());
    }

    default public TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap) {
        return this.buildPath(startPoints, destination, collisionMap, Static.getSolaceConfig().avoidWilderness());
    }

    default public TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness) {
        return this.buildPath(startPoints, destination, collisionMap, avoidWilderness, false);
    }

    default public TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness, boolean useCache) {
        return this.buildPath(startPoints, destination, collisionMap, avoidWilderness, useCache, true);
    }

    default public TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness, boolean useCache, boolean useTransports) {
        return this.buildPath(startPoints, destination, collisionMap, avoidWilderness, useCache, useTransports, new HashMap<WorldPoint, Teleport>());
    }

    public boolean walkAlong(WorldArea var1, TilePath var2, Map<WorldPoint, List<Transport>> var3, WalkOptions var4);

    public Map<WorldPoint, List<Transport>> buildTransportLinks(WalkOptions var1);

    default public Map<WorldPoint, List<Transport>> buildTransportLinks() {
        return this.buildTransportLinks(WalkOptions.builder().build());
    }

    public LinkedHashMap<WorldPoint, Teleport> buildTeleportLinks(WorldArea var1, WalkOptions var2);

    public LinkedHashMap<WorldPoint, Teleport> buildExperimentalTeleportLinks(WorldArea var1, WalkOptions var2);

    default public LinkedHashMap<WorldPoint, Teleport> buildTeleportLinks(WorldArea destination) {
        return this.buildExperimentalTeleportLinks(destination, WalkOptions.builder().build());
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

    public TilePath buildPath(Collection<WorldPoint> var1, List<WorldArea> var2, WalkOptions var3, HashMap<WorldPoint, Teleport> var4, Map<WorldPoint, List<Transport>> var5);

    default public TilePath buildPath(Collection<WorldPoint> startPoints, List<WorldArea> targetAreas, WalkOptions options) {
        Map<WorldPoint, List<Transport>> transports = this.buildTransportLinks(options);
        HashMap<WorldPoint, Teleport> teleports = this.buildUnfilteredTeleportLinks(options);
        return this.buildPath(startPoints, targetAreas, options, teleports, transports);
    }

    default public TilePath buildPath(List<WorldArea> targetAreas, WalkOptions options) {
        IPlayer local = Static.getPlayers().getLocal();
        ArrayList<WorldPoint> startPoints = new ArrayList<WorldPoint>();
        startPoints.add(local.getWorldLocation());
        return this.buildPath(startPoints, targetAreas, options);
    }

    public HashMap<WorldPoint, Teleport> buildUnfilteredTeleportLinks(WalkOptions var1);

    default public HashMap<WorldPoint, Teleport> buildUnfilteredTeleportLinks() {
        return this.buildUnfilteredTeleportLinks(WalkOptions.builder().build());
    }

    public boolean shouldUseCanvasClick(Point var1, LocalPoint var2, WorldPoint var3, Point var4, IPlayer var5, WalkOptions var6);

    public int getTeleportDelay();

    public void setTeleportDelay(int var1);

    public int getTransportDelay();

    public void setTransportDelay(int var1);

    public int getWalkerDelay();

    public void setWalkerDelay(int var1);
}

