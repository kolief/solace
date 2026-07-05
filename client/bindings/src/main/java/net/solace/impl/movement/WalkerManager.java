package net.solace.impl.movement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.entities.IPlayers;
import net.solace.api.events.ConfigChanged;
import net.solace.api.game.IGame;
import net.solace.api.movement.pathfinder.ITeleportLoader;
import net.solace.api.movement.pathfinder.ITransportLoader;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.api.widgets.IWidgets;

import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class WalkerManager {
    private static GameState prevState;
    private static final Set<Integer> REFRESH_WIDGET_IDS = Set.of(
            InterfaceID.Questscroll.QUEST_TITLE,
            InterfaceID.LevelupDisplay.TEXT2
    );
    private static final Set<String> pathfinderConfigKeys = Set.of(
            "useTransports",
            "useTeleports",
            "avoidWilderness",
            "usePoh",
            "useHomeTeleports",
            "hasMountedGlory",
            "hasMountedDigsitePendant",
            "hasMountedMythicalCape",
            "hasMountedXericsTalisman",
            "hasJewelryBox",
            "housePortals",
            "spiritFairyTree",
            "useEquipmentJewellery",
            "experimental",
            "useMinigameTeleports",
            "fairyRings",
            "useCharterShips",
            "useGnomeGliders",
            "useMagicCarpets",
            "spiritTrees",
            "magicMushtrees"
    );

    private static final Set<Integer> REFRESH_VARBS = Set.of(
            // Static
            VarbitID.FALADOR_MEDIUM_REWARD,
            VarbitID.LUMBRIDGE_MEDIUM_REWARD,
            VarbitID.DESERT_HARD_REWARD,
            VarbitID.DARKM_SHORTCUT_INNER,
            VarbitID.DARKM_SHORTCUT_OUTER,
            // Hardcoded
            VarbitID.VM_KUDOS,
            VarbitID.ZEAH_PLAYERHASVISITED,
            VarbitID.CLUEQUEST
    );

    private static boolean REFRESH_PATH = false;
    private static boolean INITIAL_LOGIN = true;

    @Getter
    @Setter
    private int teleportDelay = 0;
    @Getter
    @Setter
    private int transportDelay = 0;
    @Getter
    @Setter
    private int walkerDelay = 0;
    @Getter
    @Setter
    private boolean isTeleblocked = false;

    private final ConfigManager configManager;
    private final SolaceConfig solaceConfig;
    private final IWidgets widgets;
    private final IPlayers players;
    private final IGame game;
    private final ITransportLoader transportLoader;
    private final ITeleportLoader teleportLoader;
    private final IClient client;

    public void init() {
        initTransportLoader();
    }

    @Subscribe(priority = 1)
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case UNKNOWN:
            case STARTING:
            case LOGIN_SCREEN:
            case LOGIN_SCREEN_AUTHENTICATOR:
            case CONNECTION_LOST:
            case HOPPING:
            case LOADING:
                INITIAL_LOGIN = true;
                break;
            case LOGGED_IN:
                if (INITIAL_LOGIN) {
                    REFRESH_PATH = true;
                    INITIAL_LOGIN = false;
                }
                break;
        }
        prevState = event.getGameState();
    }

    @Subscribe(priority = 1)
    public void onGameTick(GameTick e) {
        var startMs = System.currentTimeMillis();
        if (isHouseLoading()) {
            log.warn("Delaying path refresh due to house loading");
            return;
        }

        if (walkerDelay > 0) {
            walkerDelay--;
        }

        if (transportDelay > 0) {
            transportDelay--;
        }

        if (teleportDelay > 0) {
            teleportDelay--;
        }

        if (REFRESH_PATH) {
            refresh();
            REFRESH_PATH = false;
        }

        log.trace("[WalkerManager] onGameTick took {} ms", System.currentTimeMillis() - startMs);
    }

    @Subscribe(priority = 1)
    public void onWidgetLoaded(WidgetLoaded event) {
        if (game.isLoggedIn() && REFRESH_WIDGET_IDS.contains(event.getGroupId())) {
            REFRESH_PATH = true;
            log.debug("Refreshing teleports and transports because of widget loaded: {}", event.getGroupId());
        }

        if (solaceConfig.proceedWarning()) {
            widgets.getAll(event.getGroupId()).forEach(this::proceedDangerousAreaWidget);
        }
    }

    @Subscribe(priority = 1)
    public void onVarbitChanged(VarbitChanged event) {
        var id = event.getVarbitId();
        var value = event.getValue();
        if (game.isLoggedIn() && REFRESH_VARBS.contains(id)) {
            REFRESH_PATH = true;
            log.debug("Refreshing teleports and transports because varbit: {} changed value: {}", event.getVarpId(), event.getValue());
        }

        if (id == VarbitID.TELEBLOCK_CYCLES) {
            if (!isTeleblocked && value > 0) {
                isTeleblocked = true;
                REFRESH_PATH = true;
                log.debug("Refreshing teleports and transports because teleblock applied.");
            }
            else if (isTeleblocked && value <= 0) {
                isTeleblocked = false;
                REFRESH_PATH = true;
                log.debug("Refreshing teleports and transports because teleblock removed.");
            }
        }
    }

    @Subscribe(priority = 1)
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (game.isLoggedIn() && event.getContainerId() == InventoryID.INV) {
            REFRESH_PATH = true;
            log.debug("Refreshing teleports and transports because container changed {}", event.getContainerId());
        }

        if (game.isLoggedIn() && event.getContainerId() == InventoryID.WORN) {
            REFRESH_PATH = true;
            log.debug("Refreshing teleports and transports because container changed {}", event.getContainerId());
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(SolaceConfig.CONFIG_GROUP)) {
            return;
        }

        if (pathfinderConfigKeys.contains(event.getKey())) {
            if (event.getKey().equals("useTeleports") && Objects.equals(event.getNewValue(), "false")) {
                configManager.setConfiguration(SolaceConfig.CONFIG_GROUP, "usePoh", event.getNewValue());
            }


            if (game.isLoggedIn()) {
                REFRESH_PATH = true;
                log.debug("Refreshing teleports and transports because of config with key: {} changed.", event.getKey());
            }
        }
    }

    private void proceedDangerousAreaWidget(IWidget widget) {
        if (widget == null) {
            return;
        }

        if (widget.getText().equals("Proceed regardless") && widget.hasAction()) {
            client.interact(1, 57, -1, widget.getId());
        }
    }

    private boolean isHouseLoading() {
        return widgets.isVisible(InterfaceID.POH_LOADING, 5);
    }
    private void initTransportLoader() {
        transportLoader.init();
    }

    public void refresh() {
        transportLoader.refreshTransports();
        teleportLoader.refreshTeleports();
    }
}
