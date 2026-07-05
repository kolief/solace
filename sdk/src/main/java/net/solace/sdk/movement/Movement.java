package net.solace.sdk.movement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.Locatable;
import net.solace.api.movement.IMovement;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.WalkOptions;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.sdk.entities.Players;
import net.solace.sdk.game.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Movement {
    private static final Logger log = LoggerFactory.getLogger(Movement.class);
    private static final IMovement MOVEMENT = Static.getMovement();

    public static void setDestination(int sceneX, int sceneY) {
        MOVEMENT.setDestination(sceneX, sceneY);
    }

    @Nullable
    public static WorldPoint getDestination() {
        return MOVEMENT.getDestination();
    }

    public static boolean isWalking() {
        return MOVEMENT.isWalking();
    }

    public static void walk(WorldPoint worldPoint) {
        MOVEMENT.walk(worldPoint);
    }

    public static boolean walkTo(WorldPoint worldPoint, CollisionMap collisionMap) {
        return Movement.walkTo(worldPoint.toWorldArea(), collisionMap, (Boolean)true);
    }

    public static boolean walkTo(WorldPoint worldPoint, CollisionMap collisionMap, boolean useTeleports) {
        return Movement.walkTo(worldPoint.toWorldArea(), collisionMap, (Boolean)useTeleports);
    }

    public static boolean walkTo(WorldPoint worldPoint, boolean useTeleports) {
        return Movement.walkTo(worldPoint.toWorldArea(), (CollisionMap)Static.getGlobalCollisionMap(), (Boolean)useTeleports);
    }

    public static boolean walkTo(WorldArea worldArea, CollisionMap collisionMap) {
        return Movement.walkTo(worldArea, collisionMap, (Boolean)true);
    }

    public static boolean walkTo(WorldArea worldArea, Boolean useTeleports) {
        return Movement.walkTo(worldArea, (CollisionMap)Static.getGlobalCollisionMap(), useTeleports);
    }

    public static boolean walkTo(WorldArea worldArea, CollisionMap collisionMap, Boolean useTeleports) {
        return MOVEMENT.walkTo(worldArea, collisionMap, useTeleports.booleanValue());
    }

    public static boolean walkTo(WorldArea worldArea) {
        return MOVEMENT.walkTo(worldArea);
    }

    public static void walk(Locatable locatable) {
        Movement.walk(locatable.getWorldLocation());
    }

    public static boolean walkTo(WorldPoint worldPoint) {
        return MOVEMENT.walkTo(worldPoint);
    }

    public static boolean walkTo(Locatable locatable) {
        return Movement.walkTo(locatable.getWorldLocation());
    }

    public static boolean walkTo(BankLocation bankLocation) {
        return Movement.walkTo(bankLocation.getArea());
    }

    public static boolean walkTo(int x, int y) {
        return Movement.walkTo(x, y, Client.getPlane());
    }

    public static boolean walkTo(int x, int y, int plane) {
        return Movement.walkTo(new WorldPoint(x, y, plane));
    }

    public static boolean walkTo(WorldArea destination, WalkOptions options) {
        return MOVEMENT.walkTo(destination, options);
    }

    public static boolean walkTo(WorldPoint destination, WalkOptions options) {
        return Movement.walkTo(destination.toWorldArea(), options);
    }

    public static boolean isRunEnabled() {
        return MOVEMENT.isRunEnabled();
    }

    public static void toggleRun() {
        MOVEMENT.toggleRun();
    }

    public static boolean isStaminaBoosted() {
        return MOVEMENT.isStaminaBoosted();
    }

    public static int getRunEnergy() {
        return MOVEMENT.getRunEnergy();
    }

    public static int calculateDistance(WorldArea destination) {
        return Movement.getPath(destination).size();
    }

    public static int calculateDistance(WorldPoint start, WorldArea destination) {
        return Movement.calculateDistance(List.of(start), destination);
    }

    public static int calculateDistance(List<WorldPoint> start, WorldArea destination) {
        return Movement.getPath(start, destination).size();
    }

    public static int calculateDistance(WorldPoint destination) {
        return Movement.calculateDistance(destination.toWorldArea());
    }

    public static int calculateDistance(WorldPoint start, WorldPoint destination) {
        return Movement.calculateDistance(start, destination.toWorldArea());
    }

    public static int calculateDistance(List<WorldPoint> start, WorldPoint destination) {
        return Movement.calculateDistance(start, destination.toWorldArea());
    }

    public static TilePath getPath(WorldPoint destination) {
        return Movement.getPath(List.of(Players.getLocal().getWorldLocation()), destination);
    }

    public static TilePath getPath(WorldPoint destination, CollisionMap collisionMap) {
        return Movement.getPath(List.of(Players.getLocal().getWorldLocation()), destination, collisionMap);
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldPoint destination) {
        return Movement.getPath(startPoints, destination, (CollisionMap)Static.getGlobalCollisionMap());
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldPoint destination, CollisionMap collisionMap) {
        return Movement.getPath(startPoints, destination.toWorldArea(), collisionMap);
    }

    public static TilePath getPath(WorldArea destination) {
        return Movement.getPath(List.of(Players.getLocal().getWorldLocation()), destination);
    }

    public static TilePath getPath(WorldArea destination, CollisionMap collisionMap) {
        return Movement.getPath(List.of(Players.getLocal().getWorldLocation()), destination, collisionMap);
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination) {
        return Movement.getPath(startPoints, destination, (CollisionMap)Static.getGlobalCollisionMap());
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap) {
        return Movement.getPath(startPoints, destination, collisionMap, false);
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, boolean useCache) {
        return Movement.getPath(startPoints, destination, (CollisionMap)Static.getGlobalCollisionMap(), useCache);
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean useCache) {
        return Movement.getPath(startPoints, destination, collisionMap, useCache, true);
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean useCache, boolean useTransports) {
        return Movement.getPath(startPoints, destination, collisionMap, useCache, useTransports, new HashMap<WorldPoint, Teleport>());
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, CollisionMap collisionMap, boolean useCache, boolean useTransports, HashMap<WorldPoint, Teleport> teleports) {
        return MOVEMENT.getPath(startPoints, destination, collisionMap, useCache, useTransports, teleports);
    }

    public static TilePath getPath(Collection<WorldPoint> startPoints, WorldArea destination, WalkOptions options, HashMap<WorldPoint, Teleport> teleports) {
        return MOVEMENT.getPath(startPoints, destination, options, teleports);
    }

    public static WorldPoint getNearestWalkableTile(WorldPoint source, Predicate<WorldPoint> filter) {
        return Movement.getNearestWalkableTile(source, (CollisionMap)Static.getGlobalCollisionMap(), filter);
    }

    public static WorldPoint getNearestWalkableTile(WorldPoint source, CollisionMap collisionMap, Predicate<WorldPoint> filter) {
        return MOVEMENT.getNearestWalkableTile(source, collisionMap, filter);
    }

    public static WorldPoint getNearestWalkableTile(WorldPoint source) {
        return Movement.getNearestWalkableTile(source, (CollisionMap)Static.getGlobalCollisionMap(), x -> true);
    }

    public static WorldPoint getNearestWalkableTile(WorldPoint source, CollisionMap collisionMap) {
        return Movement.getNearestWalkableTile(source, collisionMap, x -> true);
    }
}

