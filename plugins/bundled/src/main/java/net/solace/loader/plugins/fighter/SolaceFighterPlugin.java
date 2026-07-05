package net.solace.loader.plugins.fighter;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuOpened;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import net.solace.api.ui.ColorScheme;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.events.ActorDeath;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.Task;
import net.solace.api.plugins.TaskPlugin;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.fighter.data.PrayerPotions;
import net.solace.loader.plugins.fighter.tasks.HandleStopTrigger;
import net.solace.loader.plugins.fighter.tasks.IdleTask;
import net.solace.loader.plugins.fighter.tasks.StartBreak;
import net.solace.loader.plugins.fighter.tasks.combat.AttackMonster;
import net.solace.loader.plugins.fighter.tasks.combat.EquipBracelet;
import net.solace.loader.plugins.fighter.tasks.combat.HandleCannon;
import net.solace.loader.plugins.fighter.tasks.combat.HandleSpecial;
import net.solace.loader.plugins.fighter.tasks.combat.ReturnToCenter;
import net.solace.loader.plugins.fighter.tasks.consumables.DrinkAntifire;
import net.solace.loader.plugins.fighter.tasks.consumables.DrinkAntivenom;
import net.solace.loader.plugins.fighter.tasks.consumables.DrinkBoost;
import net.solace.loader.plugins.fighter.tasks.consumables.DrinkGoading;
import net.solace.loader.plugins.fighter.tasks.consumables.EatFood;
import net.solace.loader.plugins.fighter.tasks.consumables.RestorePrayer;
import net.solace.loader.plugins.fighter.tasks.loot.BuryBones;
import net.solace.loader.plugins.fighter.tasks.loot.DropJunk;
import net.solace.loader.plugins.fighter.tasks.loot.HandleAlch;
import net.solace.loader.plugins.fighter.tasks.loot.HandleHerbsack;
import net.solace.loader.plugins.fighter.tasks.loot.HandleSoulbearer;
import net.solace.loader.plugins.fighter.tasks.loot.LootItems;
import net.solace.loader.plugins.fighter.tasks.misc.PrayAltar;
import net.solace.loader.plugins.fighter.tasks.misc.WaitForStart;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Combat;
import net.solace.sdk.game.Game;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.widgets.Prayers;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Solace Fighter",
        description = "A simple auto fighter"
)
@Slf4j
public class SolaceFighterPlugin extends TaskPlugin {
    private final Task[] tasks = {
            new WaitForStart(this),
            new StartBreak(this),
            new HandleStopTrigger(this),
            new EatFood(this),
            new RestorePrayer(this),
            new DrinkAntivenom(this),
            new DrinkAntifire(this),
            new DrinkBoost(this),
            new DrinkGoading(this),
            new BuryBones(this),
            new DropJunk(this),
            new LootItems(this),
            new HandleHerbsack(this),
            new HandleSoulbearer(this),
            new HandleCannon(this),
            new HandleAlch(this),
            new EquipBracelet(this),
            new PrayAltar(this),
            new HandleSpecial(this),
            new AttackMonster(this),
            new ReturnToCenter(this),
            new IdleTask(this)
    };

    @Getter
    @Setter
    private boolean shouldStop = false;

    @Getter
    @Setter
    private String currentTaskName = "Idle";

    @Getter
    @Setter
    private int consumableCooldown = 0;

    @Getter
    @Setter
    private int offerCooldown = 0;

    @Getter
    @Setter
    private int alchCooldown = 0;

    @Getter
    @Setter
    private int logoutTimer = 5;

    @Inject
    @Getter
    private SolaceFighterConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    @Getter
    private BreakHandler breakHandler;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private FighterOverlay fighterOverlay;

    @Inject
    private SolaceFighterOverlay solaceFighterOverlay;

    @Override
    public void startUp() throws Exception {
        super.startUp();

        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);

        overlayManager.add(fighterOverlay);
        overlayManager.add(solaceFighterOverlay);
    }

    @Override
    public void shutDown() {
        breakHandler.unregisterPlugin(this);
        breakHandler.stopPlugin(this);
        overlayManager.remove(fighterOverlay);
        overlayManager.remove(solaceFighterOverlay);
        shouldStop = false;
    }

    @Override
    public Task[] getTasks() {
        return tasks;
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (getConsumableCooldown() > 0) {
            setConsumableCooldown(getConsumableCooldown() - 1);
        }

        if (getOfferCooldown() > 0) {
            setOfferCooldown(getOfferCooldown() - 1);
        }

        if (getAlchCooldown() > 0) {
            setAlchCooldown(getAlchCooldown() - 1);
        }

        if (getLogoutTimer() > 0) {
            setLogoutTimer(getLogoutTimer() - 1);
        }

        if (Prayers.getPoints() > 0 && config.quickPrayer()) {
            if (config.flick()) {
                if (Prayers.isQuickPrayerEnabled()) {
                    Prayers.toggleQuickPrayer();
                }

                Prayers.toggleQuickPrayer();
            } else if (!Prayers.isQuickPrayerEnabled()) {
                Prayers.toggleQuickPrayer();
            }
        }
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn() || breakHandler.isBreakActive(this)) {
            return 1000;
        }

        if (getCenter() == null) {
            if (Game.isLoggedIn()) {
                setCenter(Players.getLocal().getWorldLocation());
            }
            return -1;
        }

        if (Game.isLoggedIn() && !isShouldStop()) {
            if (config.disableWhenNoPrayer()
                    && Prayers.getPoints() == 0
                    && getPrayerRestore() == null) {
                setShouldStop(true);
            }

            if (config.disableWhenNoFood() && getFood() == null) {
                setShouldStop(true);
            }
        }

        return super.loop();
    }

    @Provides
    public SolaceFighterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceFighterConfig.class);
    }

    @Subscribe
    private void onMenuOpened(MenuOpened e) {
        var tile = Client.getSelectedSceneTile();
        var parentMenu = Client.getWrapped().getMenu().createMenuEntry(-1)
                .setOption(ColorScheme.brandCol("Solace Fighter"))
                .setType(MenuAction.RUNELITE);

        var subMenu = parentMenu.createSubMenu();
        subMenu.createMenuEntry(0)
                .setOption("Set Center")
                .setType(MenuAction.RUNELITE)
                .onClick(menu ->
                {
                    if (tile != null) {
                        var worldLocation = tile.getWorldLocation();
                        setCenter(worldLocation);
                    }
                });

        if (config.enableSafespot()) {
            subMenu.createMenuEntry(1)
                    .setOption("Set Safespot")
                    .setType(MenuAction.RUNELITE)
                    .onClick(menu ->
                    {
                        if (tile != null) {
                            var worldLocation = tile.getWorldLocation();
                            config.safespotTile(String.format("%s %s %s", worldLocation.getX(), worldLocation.getY(), worldLocation.getPlane()));
                        }
                    });

        }
    }

    @Subscribe
    private void onActorDeath(ActorDeath event) {
        if (event.getActor() == Players.getLocal()) {
            config.enabled(false);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            if (isShouldStop()) {
                config.enabled(false);
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage e) {
        var message = e.getMessage();
        if (config.disableAfterSlayerTask() && message.contains("You have completed your task!")) {
            config.enabled(false);
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied e) {
        if (e.getActor() == Players.getLocal().getWrapped()) {
            setLogoutTimer(10);
        }
    }

    public ITileObject getCannon() {
        var cannonPosition = Combat.getCannonWorldPoint();

        if (cannonPosition == null || (cannonPosition.getX() == 1 && cannonPosition.getY() == 0)) {
            return null;
        }

        return TileObjects.getFirstAt(cannonPosition, x -> x != null && x.hasAction("Fire", "Repair"));
    }

    public WorldPoint getCenter() {
        return parseTile(getConfig().centerTile());
    }

    public WorldPoint getSafespot() {
        return parseTile(getConfig().safespotTile());
    }

    private WorldPoint parseTile(String coords) {
        var WORLD_POINT_PATTERN = Pattern.compile("^\\d{4,5} \\d{4,5} \\d$");

        if (coords.isBlank() || !WORLD_POINT_PATTERN.matcher(coords).matches()) {
            return null;
        }

        var split = Arrays.stream(coords.split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return new WorldPoint(split.get(0), split.get(1), split.get(2));
    }

    private void setCenter(WorldPoint worldPoint) {
        configManager.setConfiguration(
                "solacefighter",
                "centerTile",
                String.format("%s %s %s", worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane())
        );
    }

    public IItem getFood() {
        var foods = Text.fromCSV(getConfig().foods());

        return Inventory.getFirst(x -> (x.getName() != null && foods.stream().anyMatch(a -> x.getName().contains(a)))
                || (foods.contains("Any") && x.hasAction("Eat") && !x.getName().contains("Dwarven rock cake")));
    }

    public IItem getPrayerRestore() {
        return PrayerPotions.getPrayerRestore();
    }
}