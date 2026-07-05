package net.solace.sdk.movement;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.movement.IWalker;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.WalkOptions;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.movement.pathfinder.model.Transport;

public class Walker {
    private static final IWalker WALKER = Static.getWalker();

    public static boolean walkTo(WorldArea destination, CollisionMap collisionMap, boolean useTeleports) {
        return WALKER.walkTo(destination, collisionMap, useTeleports);
    }

    public static boolean walkTo(WorldArea destination, WalkOptions options) {
        return WALKER.walkTo(destination, options);
    }

    public static boolean walkTo(WorldPoint destination, WalkOptions options) {
        return Walker.walkTo(destination.toWorldArea(), options);
    }

    public static TilePath getCurrentPath() {
        return WALKER.getCurrentPath();
    }

    public static TilePath getLastPath() {
        return WALKER.getLastPath();
    }

    public static void invalidatePath() {
        WALKER.setLastPath(null);
        WALKER.setCurrentPath(null);
    }

    public static boolean walkAlong(WorldArea destination, TilePath path, Map<WorldPoint, List<Transport>> transports, WalkOptions options) {
        return WALKER.walkAlong(destination, path, transports, options);
    }

    @Deprecated
    public static TilePath buildPath(WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness) {
        return WALKER.buildPath(destination, collisionMap, avoidWilderness);
    }

    @Deprecated
    public static TilePath buildPath(WorldArea destination, CollisionMap collisionMap) {
        return WALKER.buildPath(destination, collisionMap);
    }

    @Deprecated
    public static TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap) {
        return WALKER.buildPath(startPoints, destination, collisionMap);
    }

    @Deprecated
    public static TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness) {
        return WALKER.buildPath(startPoints, destination, collisionMap, avoidWilderness);
    }

    @Deprecated
    public static TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness, boolean useCache) {
        return WALKER.buildPath(startPoints, destination, collisionMap, avoidWilderness, useCache);
    }

    @Deprecated
    public static TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness, boolean useCache, boolean useTransports) {
        return WALKER.buildPath(startPoints, destination, collisionMap, avoidWilderness, useCache, useTransports);
    }

    @Deprecated
    public static TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean avoidWilderness, boolean useCache, boolean useTransports, HashMap<WorldPoint, Teleport> teleports) {
        return WALKER.buildPath(startPoints, destination, collisionMap, avoidWilderness, useCache, useTransports, teleports);
    }

    public static TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, WalkOptions options, HashMap<WorldPoint, Teleport> teleports, HashMap<WorldPoint, List<Transport>> transports) {
        return WALKER.buildPath(startPoints, destination, options, teleports, transports);
    }

    public static TilePath buildPath(Collection<WorldPoint> startPoints, WorldArea destination, WalkOptions options) {
        return WALKER.buildPath(startPoints, destination, options);
    }

    public static TilePath buildPath(WorldArea destination, WalkOptions options) {
        return WALKER.buildPath(destination, options);
    }

    public static Map<WorldPoint, List<Transport>> buildTransportLinks(WalkOptions options) {
        return WALKER.buildTransportLinks(options);
    }

    public static Map<WorldPoint, List<Transport>> buildTransportLinks() {
        return WALKER.buildTransportLinks();
    }

    public static LinkedHashMap<WorldPoint, Teleport> buildTeleportLinks(WorldArea destination, WalkOptions options) {
        return WALKER.buildTeleportLinks(destination, options);
    }

    public static LinkedHashMap<WorldPoint, Teleport> buildTeleportLinks(WorldArea destination) {
        return WALKER.buildTeleportLinks(destination);
    }

    @Deprecated
    public static LinkedHashMap<WorldPoint, Teleport> buildExperimentalTeleportLinks(WorldArea destination, WalkOptions options) {
        return WALKER.buildExperimentalTeleportLinks(destination, options);
    }
}

