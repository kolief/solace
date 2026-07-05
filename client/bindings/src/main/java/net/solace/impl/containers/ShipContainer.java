package net.solace.impl.containers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WorldEntityDespawned;
import net.runelite.api.events.WorldEntitySpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.game.IClient;
import net.solace.api.sailing.Ship;
import net.solace.impl.sailing.ShipImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
public class ShipContainer {
    // Double-check if these are correct
    private static final int MAX_NPCS_ON_SHIP = 8;
    private static final int MAX_PLAYERS_ON_SHIP = 8;
    private static final int TOP_LEVEL_WV_ID = -1;

    @Getter
    private final Map<Integer, Ship> ships = new ConcurrentHashMap<>();

    private final IClient client;
    private final EventBus eventBus;

    @Subscribe
    private void onWorldEntitySpawned(WorldEntitySpawned e) {
        var worldEntity = e.getWorldEntity();
        var worldView = worldEntity.getWorldView();
        var worldViewId = worldView.getId();

        if (worldViewId == TOP_LEVEL_WV_ID) {
            return;
        }

        var sizeX = worldView.getSizeX();
        var sizeY = worldView.getSizeY();
        var tileContainer = new TileContainerImpl(client, worldViewId, sizeX, sizeY);
        var npcContainer = new NpcContainerImpl(client, worldViewId);
        var playerContainer = new PlayerContainerImpl(client, worldViewId);

        var ship = new ShipImpl(eventBus, client, tileContainer, npcContainer, playerContainer, worldEntity);

        ships.put(worldViewId, ship);
    }

    @Subscribe
    private void onWorldEntityDespawned(WorldEntityDespawned e) {
        var worldEntity = e.getWorldEntity();
        var worldView = worldEntity.getWorldView();
        var worldViewId = worldView.getId();

        if (worldViewId == TOP_LEVEL_WV_ID) {
            return;
        }

        var ship = ships.remove(worldViewId);
        if (ship == null) {
            return;
        }

        ship.destroy(eventBus);
    }

    @Subscribe(priority = Integer.MAX_VALUE)
    private void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() != GameState.LOGGED_IN && e.getGameState() != GameState.LOADING) {
            ships.values().forEach(ship -> ship.destroy(eventBus));
            ships.clear();
        }
    }
}
