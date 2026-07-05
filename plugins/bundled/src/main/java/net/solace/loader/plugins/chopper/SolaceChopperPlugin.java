package net.solace.loader.plugins.chopper;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.solace.api.Static;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.commons.Rand;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.events.AnimationChanged;
import net.solace.api.events.ConfigChanged;
import net.solace.api.events.ExperienceGained;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileItems;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.entities.Tiles;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.Skills;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.movement.Reachable;
import net.solace.sdk.widgets.Production;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Solace Chopper",
        description = "Chops trees"
)
@Slf4j
public class SolaceChopperPlugin extends LoopedPlugin {
    @Inject
    private SolaceChopperConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ChopperOverlay chopperOverlay;

    @Inject
    private BreakHandler breakHandler;

    private boolean shouldDrop;
    private int fmCooldown = 0;
    private List<ITile> fireArea;
    private boolean shouldLightFires;
    private WorldPoint startingLocation;

    @Override
    public void startUp() {
        if (Game.isLoggedIn()) {
            startingLocation = Players.getLocal().getWorldLocation();

            chopperOverlay.setStartingLocation(startingLocation);

            fireArea = generateFireArea();

            chopperOverlay.setFireArea(fireArea);
        }

        overlayManager.add(chopperOverlay);

        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
    }

    @Override
    public void shutDown() {
        breakHandler.unregisterPlugin(this);
        breakHandler.stopPlugin(this);

        overlayManager.remove(chopperOverlay);
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

        if (Skills.getLevel(Skill.WOODCUTTING) >= config.stopAtLevel()) {
            throw new PluginStoppedException("Reached target level");
        }

        if (fmCooldown > 0) {
            return -1;
        }

        if (startingLocation == null) {
            log.info("Waiting for start location to be set");
            return -1;
        }

        if (!config.ignoreNests()) {
            final var nest = TileItems.getAllMine(item ->
                    item.getName().contains(" nest")
                            && item.distanceTo(startingLocation) <= config.radius()
            ).stream().findFirst().orElse(null);

            if (nest != null && Inventory.getFreeSlots() > 1) {
                nest.interact("Take");
                return -1;
            }

            var nestInv = Inventory.getFirst(x -> x.getName().contains(" nest") && x.hasAction("Open", "Search"));
            if (nestInv != null) {
                nestInv.interact("Open", "Search");
                return -1;
            }
        }

        final var local = Players.getLocal();
        final var treeToCut = TileObjects.getNearest(tree ->
                Arrays.stream(config.tree().getNames()).anyMatch(anotherString -> tree.getName() != null && tree.getName().equalsIgnoreCase(anotherString))
                        && tree.distanceTo(startingLocation) <= config.radius()
                        && Reachable.isInteractable(tree)
                        && tree.hasAction("Chop down", "Cut", "Chop")
        );

        if ((Inventory.isFull() || shouldDrop) || shouldLightFires || (Bank.isOpen() && Inventory.contains(x -> !x.getName().toLowerCase().contains("axe")))) {
            var logs = Inventory.getFirst(x -> x.getName().toLowerCase(Locale.ROOT).contains("logs"));

            if (shouldDrop) {
                List<IInventoryItem> itemsToDrop;
                if (config.inventoryMode() == InventoryMode.FLETCH) {
                    itemsToDrop = config.fletchMode().getItems();
                } else {
                    itemsToDrop = Inventory.getAll(x -> x.getName().toLowerCase(Locale.ROOT).contains("logs"));
                }

                if (!itemsToDrop.isEmpty()) {
                    for (var item : itemsToDrop) {
                        item.interact("Drop");
                        Static.getClient().sleep(Rand.nextInt(2, 5));
                    }
                    return -6;
                }

                shouldDrop = false;
                return -1;
            }

            switch (config.inventoryMode()) {
                case FIRE:
                    shouldLightFires = true;
                    var tinderbox = Inventory.getFirst("Tinderbox");
                    if (logs != null && tinderbox != null) {
                        if (fireArea.isEmpty()) {
                            fireArea = generateFireArea();
                            chopperOverlay.setFireArea(fireArea);
                            log.debug("Re-Generating fire area");
                            return 1000;
                        }

                        var emptyTile = fireArea.stream()
                                .filter(t ->
                                {
                                    var tile = Tiles.getAt(t.getWorldLocation());
                                    return tile != null && tile.isEmpty();
                                })
                                .min(Comparator.comparingInt(wp -> wp.distanceTo(local)))
                                .orElse(null);

                        if (emptyTile != null) {
                            if (!emptyTile.getWorldLocation().equals(local.getWorldLocation())) {
                                if (local.isMoving()) {
                                    return -1;
                                }

                                Movement.walkTo(emptyTile);
                                return 1000;
                            }

                            if (local.isAnimating()) {
                                return -1;
                            }

                            fmCooldown = 4;
                            tinderbox.useOn(logs);
                            return -1;
                        }
                    } else {
                        shouldDrop = true;
                    }
                    return -1;

                case FLETCH:
                    var knife = Inventory.getFirst("Knife");
                    if (logs != null && knife != null) {
                        if (local.isAnimating()) {
                            return Rand.nextInt(1200, 1800);
                        }

                        if (Production.isOpen()) {
                            Production.chooseOption(config.fletchMode().getName());
                            return Rand.nextInt(1200, 1800);
                        }

                        knife.useOn(logs);
                        return Rand.nextInt(1200, 1800);
                    } else {
                        shouldDrop = true;
                    }
                    return -1;

                case DROP:
                    shouldDrop = true;
                    return -1;

                case BANK:
                    if (Bank.isOpen()) {
                        Bank.depositAllExcept(x -> x.getName().toLowerCase().contains("axe"));
                    } else {
                        Bank.open(config.bankLocation());
                    }
                    return -1;
            }
        }

        if (Movement.isWalking()) {
            return -1;
        }

        if (treeToCut == null) {
            log.debug("Could not find any trees to cut");

            if (local.distanceTo(startingLocation) > config.radius()) {
                Movement.walkTo(startingLocation);
            }
            return -1;
        }

        if (local.distanceTo(treeToCut) >= 15) {
            Movement.walkTo(treeToCut);
            return -1;
        }

        if (Players.getLocal().isMoving()) {
            return -3;
        }

        if (Players.getLocal().isAnimating()) {
            return -1;
        }

        treeToCut.interact("Chop down", "Cut", "Chop");
        return -2;
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if (fmCooldown > 0) {
            fmCooldown--;
        }

        if (shouldLightFires && !Inventory.contains(x -> x.getName().toLowerCase(Locale.ROOT).contains("logs"))) {
            shouldLightFires = false;
        }

        if (startingLocation == null) {
            startingLocation = Players.getLocal().getWorldLocation();
        }
    }

    @Subscribe
    private void onExperienceGained(ExperienceGained e) {
        if (e.getSkill() == Skill.FIREMAKING) {
            fmCooldown = 0;
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged e) {
        if (e.getActor().equals(Players.getLocal()) && e.getActor().getAnimation() == 733) {
            fmCooldown = 4;
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged e) {
        if (e.getGroup().equals("solacechopper") && e.getKey().equals("radius")) {
            fireArea = generateFireArea();
            chopperOverlay.setFireArea(fireArea);
        }
    }

    @Provides
    SolaceChopperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceChopperConfig.class);
    }

    private List<ITile> generateFireArea() {
        if (startingLocation == null) {
            return List.of();
        }

        return Tiles.getAll(tile -> tile.distanceTo(startingLocation) <= config.radius()).stream()
                .filter(tile -> tile.isEmpty() && !tile.isObstructed() && Reachable.isWalkable(tile.getWorldLocation()))
                .collect(Collectors.toUnmodifiableList());
    }
}