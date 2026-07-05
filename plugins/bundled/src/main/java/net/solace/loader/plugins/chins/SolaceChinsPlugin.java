package net.solace.loader.plugins.chins;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.ui.ColorScheme;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.events.ConfigButtonClicked;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileItems;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.entities.Tiles;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.Skills;
import net.solace.sdk.game.Vars;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(name = "Solace Chins")
public class SolaceChinsPlugin extends LoopedPlugin {
    private static final int GUAM_ANIMATION = 5249;
    private static final List<Integer> TRANSITION_IDS = List.of(
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_BLACK_N,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_BLACK_E,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_BLACK_S,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_BLACK_W,

            ObjectID.HUNTING_BOXTRAP_FAILING,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_BIG_N,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_BIG_E,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_BIG_S,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_BIG_W,

            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_N,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_E,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_S,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_CHINCHOMPA_W,

            ObjectID.HUNTING_BOXTRAP_TRAPPING_FERRET_N,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_FERRET_S,
            ObjectID.HUNTING_BOXTRAP_TRAPPING_FERRET_W
    );

    private static final Map<WorldPoint, HunterTrap> MY_TRAPS = new HashMap<>();
    private WorldPoint emptySpot;
    private boolean interacting = false;
    private Instant lastAction = Instant.now();
    private WorldPoint lastTickLocalPlayerLocation = null;
    private boolean selectingTiles = false;

    @Inject
    private SolaceChinsConfig config;

    @Inject
    private BreakHandler breakHandler;

    @Override
    public void startUp() throws Exception {
        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
    }

    @Override
    public void shutDown() throws Exception {
        breakHandler.unregisterPlugin(this);
        breakHandler.stopPlugin(this);
    }

    public int getMaxTraps() {

        var wildernessBonus = Vars.getBit(VarbitID.INSIDE_WILDERNESS);
        if (Skills.getLevel(Skill.HUNTER) >= 80) {
            return 5 + wildernessBonus;
        }

        if (Skills.getLevel(Skill.HUNTER) >= 60) {
            return 4 + wildernessBonus;
        }

        if (Skills.getLevel(Skill.HUNTER) >= 40) {
            return 3 + wildernessBonus;
        }

        if (Skills.getLevel(Skill.HUNTER) >= 20) {
            return 2 + wildernessBonus;
        }

        return 1 + wildernessBonus;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn() || breakHandler.isBreakActive(this)) {
            return -1;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return -1;
        }

        var local = Players.getLocal();
        var maxTraps = getMaxTraps();
        var item = Inventory.getFirst("Box trap");

        if (getTiles().isEmpty() || selectingTiles) {
            return -1;
        }

        if (interacting) {
            return -1;
        }

        var fallen = getFallenTraps().stream()
                .findAny()
                .orElse(null);
        if (fallen != null && !local.isAnimating()) {
            fallen.interact("Take");
            return -1;
        }

        emptySpot = getEmptySpot();
        if (MY_TRAPS.size() < maxTraps
                && emptySpot != null
                && emptySpot.distanceTo(local.getWorldLocation()) < 10
                && item != null
        ) {
            if (interacting) {
                return -1;
            }

            if (local.getAnimation() != GUAM_ANIMATION
                    && (local.isMoving() || emptySpot.equals(local.getWorldLocation()))
                    && tickManip()
            ) {
                return -1;
            }

            if (emptySpot.distanceTo(local.getWorldLocation()) > 0) {
                Movement.walk(emptySpot);
                return -1;
            }

            item.interact("Lay");
            log.info("laying trap at {}", emptySpot);
            interacting = true;
            lastAction = Instant.now();
            return -1;
        }


        var finished = MY_TRAPS.values().stream()
                .filter(x -> x.getState() == HunterTrap.State.FULL || x.getState() == HunterTrap.State.EMPTY)
                .findFirst()
                .orElse(null);
        if (finished != null) {
            if (interacting) {
                return -1;
            }

            TileObjects.getFirstAt(finished.getWorldLocation(), x -> x.getId() == finished.getObjectId()).interact(0);
            log.info("resetting trap at {}", finished.getWorldLocation());
            interacting = true;
            lastAction = Instant.now();
            return -1;
        }

        var transitioningTrap = MY_TRAPS.values().stream()
                .filter(x -> x.getState() == HunterTrap.State.TRANSITION)
                .findFirst()
                .orElse(null);
        if (transitioningTrap != null && transitioningTrap.getWorldLocation().distanceTo(local.getWorldLocation()) > 0) {
            Movement.walk(transitioningTrap.getWorldLocation());
            return -2;
        }

        return -1;
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        var gameObject = event.getGameObject();
        var trapLocation = gameObject.getWorldLocation();
        var myTrap = MY_TRAPS.get(trapLocation);

        switch (gameObject.getId()) {
            case ObjectID.HUNTING_BOXTRAP_EMPTY:
                if (lastTickLocalPlayerLocation != null && trapLocation.distanceTo(lastTickLocalPlayerLocation) == 0) {
                    MY_TRAPS.put(trapLocation, new HunterTrap(gameObject));
                    log.info("trap spawned at {}", trapLocation);
                    interacting = false;
                }

                break;

            case ObjectID.HUNTING_BOXTRAP_FULL_CHINCHOMPA_BLACK:
            case ObjectID.HUNTING_BOXTRAP_FULL_CHINCHOMPA:
            case ObjectID.HUNTING_BOXTRAP_FULL_CHINCHOMPA_BIG:
                if (myTrap != null) {
                    myTrap.setObjectId(gameObject.getId());
                    myTrap.setState(HunterTrap.State.FULL);
                    myTrap.resetTimer();
                }

                break;

            case ObjectID.HUNTING_BOXTRAP_FAILED:
                if (myTrap != null) {
                    myTrap.setObjectId(gameObject.getId());
                    myTrap.setState(HunterTrap.State.EMPTY);
                    myTrap.resetTimer();
                }

                break;

            default:
                if (TRANSITION_IDS.contains(gameObject.getId()) && myTrap != null) {
                    myTrap.setObjectId(gameObject.getId());
                    myTrap.setState(HunterTrap.State.TRANSITION);
                }
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (lastAction.plusMillis(3_000).isBefore(Instant.now()) && interacting) {
            log.info("resetting laying");
            interacting = false;
        }

        var it = MY_TRAPS.entrySet().iterator();
        var expire = Instant.now().minus(HunterTrap.TRAP_TIME.multipliedBy(2));

        while (it.hasNext()) {
            var entry = it.next();
            var trap = entry.getValue();
            var world = entry.getKey();
            var local = LocalPoint.fromWorld(Client.getWrapped(), world);

            if (local == null) {
                if (trap.getPlacedOn().isBefore(expire)) {
                    it.remove();
                }
            }

            var objects = TileObjects.getAt(world, x -> x.hasAction("Dismantle", "Reset") || TRANSITION_IDS.contains(x.getId()));
            if (objects.isEmpty()) {
                log.info("removing trap at {}", world);
                interacting = false;
                it.remove();
            }
        }

        lastTickLocalPlayerLocation = Players.getLocal().getWorldLocation();
    }

    @Provides
    SolaceChinsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceChinsConfig.class);
    }

    public WorldPoint getEmptySpot() {
        return getTiles().stream()
                .filter(x -> !MY_TRAPS.containsKey(x))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public List<ITileItem> getFallenTraps() {
        return getTiles().stream()
                .map(tile -> TileItems.getFirstAt(tile, obj -> obj.hasAction("Lay")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Subscribe
    private void onMenuOpened(MenuOpened e) {
        if (selectingTiles) {
            var parentMenu = Client.getWrapped().createMenuEntry(1)
                    .setTarget(ColorScheme.brandCol("Solace Chins"))
                    .setType(MenuAction.RUNELITE);

            var subMenu = parentMenu.createSubMenu();

            var tile = Tiles.getHoveredTile();
            if (tile != null) {
                if (getTiles().contains(tile.getWorldLocation())) {
                    subMenu.createMenuEntry(-1)
                            .setOption("Deselect tile")
                            .setType(MenuAction.RUNELITE)
                            .onClick(menu -> removeTile(tile.getWorldLocation()));
                } else {
                    subMenu.createMenuEntry(0)
                            .setOption("Select tile")
                            .setType(MenuAction.RUNELITE)
                            .onClick(menu -> addTile(tile.getWorldLocation()));
                }
            }

            subMenu.createMenuEntry(0)
                    .setOption("Finish selecting")
                    .setType(MenuAction.RUNELITE)
                    .onClick(menu -> selectingTiles = false);
        }
    }

    @Subscribe
    private void onConfigButtonClicked(ConfigButtonClicked event) {
        if (event.getGroup().equals("solacechins")) {
            switch (event.getKey()) {
                case "selectTile":
                    selectingTiles = true;
                    break;

                case "resetTiles":
                    clearTiles();
                    break;
            }
        }
    }

    private List<WorldPoint> getTiles() {
        return config.tiles();
    }

    private void clearTiles() {
        config.tiles(new ArrayList<>());
    }

    private void addTile(WorldPoint tile) {
        var tiles = getTiles();
        tiles.add(tile);
        config.tiles(tiles);
    }

    private void removeTile(WorldPoint tile) {
        var tiles = getTiles();
        tiles.remove(tile);
        config.tiles(tiles);
    }

    private boolean tickManip() {
        var herb = Inventory.getFirst("Guam leaf", "Marrentill", "Tarromin", "Harralander");
        var pestle = Inventory.getFirst("Pestle and mortar");
        var swampTar = Inventory.getFirst("Swamp tar");
        if (herb != null && pestle != null && swampTar != null && swampTar.getQuantity() >= 15) {
            herb.useOn(swampTar);
            return true;
        }

        return false;
    }
}
