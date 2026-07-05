package net.solace.loader.plugins.breakhandler;

import com.google.inject.Provides;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;
import net.solace.api.Static;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.commons.IntRandomNumberGenerator;
import net.solace.api.events.ConfigChanged;
import net.solace.api.game.IGame;
import net.solace.api.game.IWorlds;
import net.solace.api.input.IKeyboard;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.widgets.ITabs;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.InterfaceAddress;
import net.solace.api.widgets.Tab;
import net.solace.loader.plugins.breakhandler.ui.SolaceBreakHandlerPanel;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.solace.api.breaks.BreakHandler.CONFIG_GROUP;
import static net.solace.api.breaks.BreakHandler.sanitizedName;

@PluginDescriptor(
        name = "Solace Break Handler",
        description = "Automatically takes breaks for you",
        enabledByDefault = true
)
@Slf4j
public class SolaceBreakHandlerPlugin extends Plugin {
    private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;

    private static final int INVENTORY_TAB = 171;
    public static String data;
    public final Map<Plugin, Disposable> disposables = new HashMap<>();
    public Disposable activeBreaks;
    public Disposable secondsDisposable;
    public Disposable activeDisposable;
    public Disposable logoutDisposable;

    @Inject
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    @Getter
    private ConfigManager configManager;

    @Inject
    private BreakHandler solaceBreakHandler;

    @Inject
    @Getter
    private OptionsConfig optionsConfig;

    @Inject
    private WorldService worldService;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private IGame game;

    @Inject
    private ITabs tabs;

    @Inject
    private IWidgets widgets;

    @Inject
    private IWorlds worlds;

    @Inject
    private IKeyboard keyboard;

    private NavigationButton navButton;
    private SolaceBreakHandlerPanel panel;
    private boolean logout;
    private int delay = -1;
    private SolaceBreakHandlerState state = SolaceBreakHandlerState.NULL;
    private ExecutorService executorService;
    private net.runelite.api.World quickHopTargetWorld;
    private int displaySwitcherAttempts = 0;


    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Provides
    public NullConfig getConfig() {
        return configManager.getConfig(NullConfig.class);
    }

    @Provides
    public OptionsConfig getOptionsConfig(ConfigManager configManager) {
        return configManager.getConfig(OptionsConfig.class);
    }

    @Override
    public void startUp() {
        executorService = Executors.newSingleThreadExecutor();

        panel = injector.getInstance(SolaceBreakHandlerPanel.class);

        final var icon = ImageUtil.loadImageResource(getClass(), "pamuk_special.png");

        navButton = NavigationButton.builder()
                .tooltip("Solace break handler")
                .icon(icon)
                .priority(4)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);

        activeBreaks = solaceBreakHandler
                .getCurrentActiveBreaksObservable()
                .subscribe(this::breakActivated);

        secondsDisposable = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribe(this::seconds);

        activeDisposable = solaceBreakHandler
                .getActiveObservable()
                .subscribe(
                        (plugins) ->
                        {
                            if (!plugins.isEmpty()) {
//								if (navButton != null && !navButton.isSelected())
//								{
//									navButton.getOnSelect().run();
//								}
                            }
                        }
                );

        logoutDisposable = solaceBreakHandler
                .getlogoutActionObservable()
                .subscribe(
                        (plugin) ->
                        {
                            if (plugin != null) {
                                logout = true;
                                state = SolaceBreakHandlerState.LOGOUT;
                            }
                        }
                );
    }

    @Override
    public void shutDown() {
        executorService.shutdown();

        clientToolbar.removeNavigation(navButton);

        panel.pluginDisposable.dispose();
        panel.activeDisposable.dispose();
        panel.currentDisposable.dispose();
        panel.startDisposable.dispose();
        panel.configDisposable.dispose();

        for (var disposable : disposables.values()) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        if (activeBreaks != null && !activeBreaks.isDisposed()) {
            activeBreaks.dispose();
        }

        if (secondsDisposable != null && !secondsDisposable.isDisposed()) {
            secondsDisposable.dispose();
        }

        if (activeDisposable != null && !activeDisposable.isDisposed()) {
            activeDisposable.dispose();
        }

        if (logoutDisposable != null && !logoutDisposable.isDisposed()) {
            logoutDisposable.dispose();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        solaceBreakHandler.getConfigChanged().onNext(configChanged);
    }

    public void scheduleBreak(Plugin plugin) {
        var from = Integer.parseInt(configManager.getConfiguration(CONFIG_GROUP, sanitizedName(plugin) + "-thresholdfrom")) * 60;
        var to = Integer.parseInt(configManager.getConfiguration(CONFIG_GROUP, sanitizedName(plugin) + "-thresholdto")) * 60;

        var random = new IntRandomNumberGenerator(from, to).nextInt();

        solaceBreakHandler.planBreak(plugin, Instant.now().plus(random, ChronoUnit.SECONDS));
    }

    private void breakActivated(Pair<Plugin, Instant> pluginInstantPair) {
        var plugin = pluginInstantPair.getKey();

        if (plugin == null) {
            return;
        }

        if (!solaceBreakHandler.getPlugins().get(plugin) || Boolean.parseBoolean(configManager.getConfiguration(CONFIG_GROUP, sanitizedName(plugin) + "-logout"))) {
            logout = true;
            state = SolaceBreakHandlerState.LOGOUT;
        }
    }

    private void seconds(long ignored) {
        var activeBreaks = solaceBreakHandler.getActiveBreaks();

        if (activeBreaks.isEmpty() || client.getGameState() != GameState.LOGIN_SCREEN) {
            return;
        }

        var finished = true;

        for (var duration : activeBreaks.values()) {
            if (Instant.now().isBefore(duration)) {
                finished = false;
            }
        }

        if (finished) {
            log.info("Break finished, logging in.");

            if (Static.getClient().isWorldSelectOpen()) {
                worlds.closeLobbyWorlds();
            }

            final var username = configManager.getConfiguration(CONFIG_GROUP, "accountselection-manual-username");
            final var password = configManager.getConfiguration(CONFIG_GROUP, "accountselection-manual-password");
            final var isOuth = Static.getClient().isOAuthCredentialsSet();
            final var hasValidUsername = username != null && !username.isEmpty() && password != null && !password.isEmpty();
            log.info("Break: Logging in: {}, Found other credentials: {}", isOuth, hasValidUsername);

            if (isOuth) {
                log.info("Break: Logging in with OAuth.");
                Static.getClientThread().invoke(() -> {
                    Static.getClient().setOAuthLoginMode();
                    Static.getClient().setGameState(GameState.LOGGING_IN);
                });
                return;
            }

            if (hasValidUsername) {
                log.info("Break: Logging in with username and password.");
                Static.getClientThread().invoke(() -> {
                    Static.getClient().setUsername(username);
                    Static.getClient().setPassword(password);
                });

                keyboard.sendEnter();
                keyboard.sendEnter();
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
            state = SolaceBreakHandlerState.LOGIN_SCREEN;

            if (!solaceBreakHandler.getActivePlugins().isEmpty() && !solaceBreakHandler.getActiveBreaks().isEmpty()) {
                if (optionsConfig.hopAfterBreak() && (optionsConfig.american() || optionsConfig.unitedKingdom() || optionsConfig.german() || optionsConfig.australian())) {
                    hop();
                }
            }

            if (optionsConfig.stopAfterBreaks() != 0 && solaceBreakHandler.getTotalAmountOfBreaks() >= optionsConfig.stopAfterBreaks()) {
                for (var plugin : Set.copyOf(solaceBreakHandler.getActivePlugins())) {
                    solaceBreakHandler.stopPlugin(plugin);
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (state == SolaceBreakHandlerState.NULL && logout && delay == 0) {
            log.info("Break: Logging out.");
            state = SolaceBreakHandlerState.LOGOUT;
        } else if (state == SolaceBreakHandlerState.LOGIN_SCREEN && !solaceBreakHandler.getActiveBreaks().isEmpty()) {
            logout = false;

            var loginScreen = widgets.get(InterfaceAddress.fromWidgetInfo(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN));
            var playButtonText = widgets.get(WidgetID.LOGIN_CLICK_TO_PLAY_GROUP_ID, 72);

            if (playButtonText != null && playButtonText.isVisible()) {
                log.info("Break: Clicking play button.");
                playButtonText.interact("Play");
            } else if (loginScreen == null) {
                log.info("Break: Switching to inventory.");
                state = SolaceBreakHandlerState.INVENTORY;
            }
        } else if (state == SolaceBreakHandlerState.LOGOUT) {
            log.info("Break: Logging out.");
            game.logout();
        } else if (state == SolaceBreakHandlerState.INVENTORY) {
            log.info("Break: Resuming plugin.");
            // Inventory
            tabs.open(Tab.INVENTORY);
            state = SolaceBreakHandlerState.RESUME;
        } else if (state == SolaceBreakHandlerState.RESUME) {
            log.info("Break: Resetting breaks.");
            for (var plugin : solaceBreakHandler.getActiveBreaks().keySet()) {
                solaceBreakHandler.stopBreak(plugin);
            }

            state = SolaceBreakHandlerState.NULL;
        } else if (!solaceBreakHandler.getActiveBreaks().isEmpty()) {
            var activeBreaks = solaceBreakHandler.getActiveBreaks();

            if (activeBreaks
                    .keySet()
                    .stream()
                    .anyMatch(e ->
                            !Boolean.parseBoolean(configManager.getConfiguration(CONFIG_GROUP, sanitizedName(e) + "-logout")))) {
                if (client.getKeyboardIdleTicks() > 14900) {
                    Static.getClient().setKeyboardIdleTicks(0);
                }
                if (client.getMouseIdleTicks() > 14900) {
                    Static.getClient().setMouseIdleTicks(0);
                }

                var finished = true;

                for (var duration : activeBreaks.values()) {
                    if (Instant.now().isBefore(duration)) {
                        finished = false;
                    }
                }

                if (finished) {
                    state = SolaceBreakHandlerState.INVENTORY;
                }
            }
        }

        if (delay > 0) {
            delay--;
        }

        if (quickHopTargetWorld == null) {
            return;
        }

        if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            client.openWorldHopper();

            if (++displaySwitcherAttempts >= DISPLAY_SWITCHER_MAX_ATTEMPTS) {
                var chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.NORMAL)
                        .append("Failed to quick-hop after ")
                        .append(ChatColorType.HIGHLIGHT)
                        .append(Integer.toString(displaySwitcherAttempts))
                        .append(ChatColorType.NORMAL)
                        .append(" attempts.")
                        .build();

                chatMessageManager
                        .queue(QueuedMessage.builder()
                                .type(ChatMessageType.CONSOLE)
                                .runeLiteFormattedMessage(chatMessage)
                                .build());

                resetQuickHopper();
            }
        } else {
            client.hopToWorld(quickHopTargetWorld);
            resetQuickHopper();
        }
    }

    private void resetQuickHopper() {
        displaySwitcherAttempts = 0;
        quickHopTargetWorld = null;
    }

    public boolean isValidBreak(Plugin plugin) {
        var plugins = solaceBreakHandler.getPlugins();

        if (!plugins.containsKey(plugin)) {
            return false;
        }

        if (!plugins.get(plugin)) {
            return true;
        }

        var thresholdfrom = configManager.getConfiguration(CONFIG_GROUP, sanitizedName(plugin) + "-thresholdfrom");
        var thresholdto = configManager.getConfiguration(CONFIG_GROUP, sanitizedName(plugin) + "-thresholdto");
        var breakfrom = configManager.getConfiguration(CONFIG_GROUP, sanitizedName(plugin) + "-breakfrom");
        var breakto = configManager.getConfiguration(CONFIG_GROUP, sanitizedName(plugin) + "-breakto");

        return isNumeric(thresholdfrom) &&
                isNumeric(thresholdto) &&
                isNumeric(breakfrom) &&
                isNumeric(breakto) &&
                Integer.parseInt(thresholdfrom) <= Integer.parseInt(thresholdto) &&
                Integer.parseInt(breakfrom) <= Integer.parseInt(breakto);
    }

    private World findWorld(List<World> worlds, EnumSet<WorldType> currentWorldTypes, int totalLevel) {
        var world = worlds.get(new Random().nextInt(worlds.size()));

        var types = world.getTypes().clone();

        types.remove(WorldType.LAST_MAN_STANDING);

        if (types.contains(WorldType.SKILL_TOTAL)) {
            try {
                var totalRequirement = Integer.parseInt(world.getActivity().substring(0, world.getActivity().indexOf(" ")));

                if (totalLevel >= totalRequirement) {
                    types.remove(WorldType.SKILL_TOTAL);
                }
            } catch (NumberFormatException ex) {
            }
        }

        if (currentWorldTypes.equals(types)) {
            var worldLocation = world.getLocation();

            if (Boolean.parseBoolean(configManager.getConfiguration(CONFIG_GROUP, "american")) && worldLocation == 0) {
                return world;
            } else if (Boolean.parseBoolean(configManager.getConfiguration(CONFIG_GROUP, "united-kingdom")) && worldLocation == 1) {
                return world;
            } else if (Boolean.parseBoolean(configManager.getConfiguration(CONFIG_GROUP, "australian")) && worldLocation == 3) {
                return world;
            } else if (Boolean.parseBoolean(configManager.getConfiguration(CONFIG_GROUP, "german")) && worldLocation == 7) {
                return world;
            }
        }

        return null;
    }

    private void hop() {
        Static.getClientThread().invoke(() ->
        {
            var worldResult = worldService.getWorlds();
            if (worldResult == null) {
                return;
            }

            var currentWorld = worldResult.findWorld(client.getWorld());

            if (currentWorld == null) {
                return;
            }

            var currentWorldTypes = currentWorld.getTypes().clone();

            currentWorldTypes.remove(WorldType.PVP);
            currentWorldTypes.remove(WorldType.HIGH_RISK);
            currentWorldTypes.remove(WorldType.BOUNTY);
            currentWorldTypes.remove(WorldType.SKILL_TOTAL);
            currentWorldTypes.remove(WorldType.LAST_MAN_STANDING);

            var worlds = worldResult.getWorlds();

            var totalLevel = client.getTotalLevel();

            World world;
            do {
                world = findWorld(worlds, currentWorldTypes, totalLevel);
            }
            while (world == null || world == currentWorld);

            hop(world.getId());
        });
    }

    private void hop(int worldId) {
        var worldResult = worldService.getWorlds();
        // Don't try to hop if the world doesn't exist
        var world = worldResult.findWorld(worldId);
        if (world == null) {
            return;
        }

        final var rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN) {
            client.changeWorld(rsWorld);
            return;
        }

        var chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Hopping away from a player. New world: ")
                .append(ChatColorType.HIGHLIGHT)
                .append(Integer.toString(world.getId()))
                .append(ChatColorType.NORMAL)
                .append("..")
                .build();

        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());

        quickHopTargetWorld = rsWorld;
        displaySwitcherAttempts = 0;
    }

    public interface State {
        int MAIN_MENU = 0;
        int BETA_WORLD = 1;
        int ENTER_CREDENTIALS = 2;
        int INVALID_CREDENTIALS = 3;
        int AUTHENTICATOR = 4;
        int OAUTH2 = 10;
        int DISABLED = 14;
        int BEEN_DISCONNECTED = 24;
    }
}