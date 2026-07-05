package net.solace.impl.movement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.solace.api.commons.Rand;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IVars;
import net.solace.api.movement.IMovement;
import net.solace.api.movement.IWalker;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.WalkOptions;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.api.widgets.IWidgets;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Slf4j
public class MovementImpl implements IMovement {
    private static final int MAX_MIN_ENERGY = 50;
    private static final int MAX_BOOSTED_ENERGY = 10;
    private static final int MIN_ENERGY = 5;
    private static final int RUN_VARP = 173;
    private static final int STAMINA_VARBIT = 25;

    private final SolaceConfig solaceConfig;
    private final IClient client;
    private final IWalker walker;
    private final IVars vars;
    private final IWidgets widgets;

    @Override
    public void setDestination(int sceneX, int sceneY) {
        client.setSelectedSceneTileX(sceneX);
        client.setSelectedSceneTileY(sceneY);
        client.setViewportWalking(true);
    }

    @Override
    @Nullable
    public WorldPoint getDestination() {
        var destination = client.getLocalDestinationLocation();
        if (destination == null || destination.getSceneX() == 0 && destination.getSceneY() == 0) {
            return null;
        }

        return WorldPoint.fromScene(
                client.getWrapped().getWorldView(destination.getWorldView()),
                destination.getSceneX(),
                destination.getSceneY(),
                client.getPlane()
        );
    }

    @Override
    public boolean isWalking() {
        var local = client.getLocalPlayer();
        if (local == null) {
            return false;
        }

        WorldPoint destination = getDestination();
        return local.isMoving()
                && destination != null
                && destination.distanceTo(local.getWorldLocation()) > 4;
    }

    @Override
    public void walk(WorldPoint worldPoint) {
        walk(worldPoint, WalkOptions.builder().build());
    }

    @Override
    public void walk(WorldPoint worldPoint, WalkOptions walkOptions) {
        walker.walk(worldPoint, walkOptions);
    }

    @Override
    public boolean walkTo(WorldPoint worldPoint) {
        return walkTo(worldPoint.toWorldArea());
    }

    @Override
    public boolean walkTo(WorldArea worldArea) {
        return walkTo(worldArea, WalkOptions.builder().build());
    }

    @Override
    public boolean walkTo(WorldArea worldArea, WalkOptions walkOptions) {
        if (walkOptions.isToggleRun() && !isRunEnabled()) {
            var maxEnergy = isStaminaBoosted() ? MAX_BOOSTED_ENERGY : MAX_MIN_ENERGY;
            if (getRunEnergy() >= Rand.nextInt(MIN_ENERGY, maxEnergy)) {
                toggleRun();
                return true;
            }
        }

        return walker.walkTo(worldArea, walkOptions);
    }

    @Override
    public boolean walkTo(WorldArea worldArea, CollisionMap collisionMap, boolean useTeleports) {
        if (solaceConfig.toggleRun() && !isRunEnabled()) {
            var maxEnergy = isStaminaBoosted() ? MAX_BOOSTED_ENERGY : MAX_MIN_ENERGY;
            if (getRunEnergy() >= Rand.nextInt(MIN_ENERGY, maxEnergy)) {
                toggleRun();
                return true;
            }
        }

        return walker.walkTo(worldArea, collisionMap, useTeleports);
    }

    @Override
    public boolean isRunEnabled() {
        return vars.getVarp(RUN_VARP) == 1;
    }

    @Override
    public void toggleRun() {
        var widget = widgets.get(InterfaceID.Orbs.RUNBUTTON);
        if (widget != null) {
            widget.interact("Toggle Run");
        }
    }

    @Override
    public boolean isStaminaBoosted() {
        return vars.getBit(STAMINA_VARBIT) == 1;
    }

    @Override
    public int getRunEnergy() {
        return client.getEnergy() / 100;
    }

    @Override
    public TilePath getPath(Collection<WorldPoint> collection, WorldArea worldArea, CollisionMap collisionMap, boolean b, boolean b1, HashMap<WorldPoint, Teleport> collection2) {
        return walker.buildPath(collection, worldArea, collisionMap, solaceConfig.avoidWilderness(), b, b1, collection2);
    }

    @Override
    public WorldPoint getNearestWalkableTile(WorldPoint source, CollisionMap collisionMap, Predicate<WorldPoint> filter) {
        return walker.getNearestWalkableTile(source, collisionMap, filter);
    }

    @Override
    public TilePath getPath(Collection<WorldPoint> collection, WorldArea worldArea, WalkOptions walkOptions, HashMap<WorldPoint, Teleport> hashMap) {
        return getPath(collection, worldArea, walkOptions.getCollisionMap(), walkOptions.isUseCache(), walkOptions.isUseTransports(), hashMap);
    }
}
