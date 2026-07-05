package net.solace.loader.plugins.birdhouses;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.account.GameAccount;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.Task;
import net.solace.api.plugins.TaskPlugin;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.birdhouses.model.BirdHouse;
import net.solace.loader.plugins.birdhouses.model.BirdHouseLocation;
import net.solace.loader.plugins.birdhouses.model.BirdHouseState;
import net.solace.loader.plugins.birdhouses.tasks.AwaitAndLogin;
import net.solace.loader.plugins.birdhouses.tasks.Break;
import net.solace.loader.plugins.birdhouses.tasks.GatherTools;
import net.solace.loader.plugins.birdhouses.tasks.SetupBirdHouse;
import net.solace.loader.plugins.birdhouses.tasks.WaitAtBank;
import net.solace.loader.plugins.birdhouses.tasks.WalkToBirdHouse;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.Vars;
import net.solace.sdk.script.blocking_events.BlockingEventManager;
import net.solace.sdk.script.blocking_events.LoginEvent;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@PluginDescriptor(name = "Solace Bird Houses")
@Slf4j
public class SolaceBirdHousesPlugin extends TaskPlugin {
    private static final int FIVE_MINUTES_IN_TICKS = 500;

    private static final List<Integer> INV_SETUP_ITEMS = List.of(
            ItemID.HAMMER,
            ItemID.CHISEL
    );

    private static final List<BirdHouse> BIRD_HOUSES = List.of(
            new BirdHouse(BirdHouseLocation.MEADOW_SOUTH, BirdHouseState.UNKNOWN),
            new BirdHouse(BirdHouseLocation.MEADOW_NORTH, BirdHouseState.UNKNOWN),
            new BirdHouse(BirdHouseLocation.VALLEY_NORTH, BirdHouseState.UNKNOWN),
            new BirdHouse(BirdHouseLocation.VALLEY_SOUTH, BirdHouseState.UNKNOWN)
    );

    private final Task[] tasks =
            {
                    new AwaitAndLogin(this),
                    new GatherTools(this),
                    new WalkToBirdHouse(this),
                    new SetupBirdHouse(this),
                    new WaitAtBank(this),
                    new Break(this),
            };

    private final BlockingEventManager blockingEventManager = new BlockingEventManager();
    @Getter
    private final LoginEvent loginEvent = new LoginEvent(blockingEventManager);

    private Task previousTask = null;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private SolaceBirdHousesConfig config;

    @Override
    public Task[] getTasks() {
        return tasks;
    }

    @Override
    public void startUp() {
        blockingEventManager.remove(LoginEvent.class);

        if (!config.username().isBlank() && !config.password().isBlank()) {
            Game.setGameAccount(new GameAccount(config.username(), config.password()));
        }

        if (Game.isLoggedIn()) {
            for (BirdHouse birdHouse : getAvailableBirdHouses()) {
                birdHouse.setState(BirdHouseState.fromVarpValue(Vars.getVarp(birdHouse.getVarp())));
            }

            printState();
        }
    }

    public List<BirdHouse> getAvailableBirdHouses() {
        return BIRD_HOUSES.stream()
                .filter(b -> b.getState() != BirdHouseState.SEEDED || b.isComplete())
                .collect(Collectors.toList());
    }

    public Optional<BirdHouse> getNextBirdHouse() {
        return getAvailableBirdHouses().stream().findFirst();
    }

    public List<BirdHouse> getBirdHouses() {
        return BIRD_HOUSES;
    }

    public List<Integer> getTools() {
        return INV_SETUP_ITEMS;
    }

    public void printMessage(String message) {
        chatMessageManager.queue(QueuedMessage.builder()
                .runeLiteFormattedMessage(
                        new ChatMessageBuilder()
                                .append(ChatColorType.NORMAL)
                                .append("[Bird Houses] ")
                                .append(ChatColorType.HIGHLIGHT)
                                .append(message)
                                .build()
                )
                .type(ChatMessageType.ITEM_EXAMINE)
                .build());
    }

    private void printState() {
        for (BirdHouse birdHouse : BIRD_HOUSES) {
            printMessage(birdHouse.toString());
        }
    }

    @Provides
    SolaceBirdHousesConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceBirdHousesConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if (!Objects.equals(previousTask, getCurrentTask())) {
            previousTask = getCurrentTask();
            printMessage("Task changed: " + (previousTask == null ? "Idle" : previousTask));
        }

        int ticks = Client.getTickCount();
        if (ticks % FIVE_MINUTES_IN_TICKS == 0) {
            printState();
        }
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged e) {
        int varpId = e.getVarpId();
        for (BirdHouse birdHouse : BIRD_HOUSES) {
            if (birdHouse.getVarp() == varpId) {
                birdHouse.setState(BirdHouseState.fromVarpValue(e.getValue()));
            }
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged e) {
        GameState state = e.getGameState();

        if (state == GameState.LOGGED_IN) {
            getLoginEvent().setAttemptedLogins(0);
        }
    }
}
