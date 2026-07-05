package net.solace.loader.plugins.cannonballer;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuAction;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.ui.ColorScheme;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.Task;
import net.solace.api.plugins.TaskPlugin;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.cannonballer.tasks.Await;
import net.solace.loader.plugins.cannonballer.tasks.DepositItems;
import net.solace.loader.plugins.cannonballer.tasks.SmeltBalls;
import net.solace.loader.plugins.cannonballer.tasks.WithdrawItems;
import net.solace.sdk.entities.Players;
import net.solace.sdk.game.Client;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Solace Cannonballer",
        description = "Makes cannonballs for you",
        tags = {"cannonballer", "cannonballs", "cannon", "baller", "solace", "net"}
)
@Slf4j
public class SolaceCannonballerPlugin extends TaskPlugin {
    @Getter
    private SelectedEntity selectedBank;
    @Getter
    private SelectedEntity selectedFurnace;

    @Inject
    @Getter
    private SolaceCannonballerConfig config;

    @Inject
    public BreakHandler breakHandler;

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

    @Override
    public int loop() {
        if (breakHandler.isBreakActive(this)) {
            return 1000;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return -1;
        }

        return super.loop();
    }

    private final Task[] tasks = new Task[]
            {
                    new Await(this),
                    new DepositItems(this),
                    new WithdrawItems(this),
                    new SmeltBalls(this)
            };

    @Subscribe
    private void onMenuOpened(MenuOpened e) {
        var hovered = Client.getHoveredEntities().stream()
                .filter(entity -> entity instanceof INPC || entity instanceof ITileObject)
                .collect(Collectors.toList());

        for (var sceneEntity : hovered) {
            if (sceneEntity.hasAction(x -> true)) {
                var color = sceneEntity instanceof INPC ? "<col=ffff00>" : "<col=00ffff>";
                var parentMenu = Client.getWrapped().createMenuEntry(-1)
                        .setOption(ColorScheme.brandCol("Solace Cannonballer"))
                        .setTarget(color + sceneEntity.getName() + "</col>")
                        .setType(MenuAction.RUNELITE);

                var subMenu = parentMenu.createSubMenu();

                if (sceneEntity.hasAction("Collect", "Deposit")) {
                    subMenu.createMenuEntry(0)
                            .setOption("Select Bank")
                            .setType(MenuAction.RUNELITE)
                            .onClick(menu -> selectedBank = new SelectedEntity(sceneEntity.getId(), sceneEntity.getWorldLocation(), sceneEntity.getClass()));
                }

                if (sceneEntity.hasAction("Smelt") || Objects.equals("Furnace", sceneEntity.getName())) {
                    subMenu.createMenuEntry(0)
                            .setOption("Select Furnace")
                            .setType(MenuAction.RUNELITE)
                            .onClick(menu -> selectedFurnace = new SelectedEntity(sceneEntity.getId(), sceneEntity.getWorldLocation(), sceneEntity.getClass()));
                }
            }
        }
    }

    public int openBank() {
        var selectedBank = getSelectedBank();
        var bank = selectedBank.get();
        if (bank != null && selectedBank.getWorldPoint().distanceTo(Players.getLocal().getWorldLocation()) < 20) {
            if (Players.getLocal().isMoving()) {
                return -2;
            }

            bank.interact("Bank", "Use");
        } else {
            if (Movement.isWalking()) {
                return -2;
            }

            Movement.walkTo(selectedBank.getWorldPoint());
        }

        return -4;
    }

    public Map<Integer, Integer> getInventorySetup() {
        Map<Integer, Integer> out = new HashMap<>();

        var steelBars = Inventory.getCount(ItemID.STEEL_BAR);
        out.put(ItemID.STEEL_BAR, 27 - steelBars);

        var mould = Inventory.getCount(config.mould().getItemId());
        out.put(config.mould().getItemId(), 1 - mould);

        return out;
    }

    @Provides
    SolaceCannonballerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceCannonballerConfig.class);
    }

    @Override
    public Task[] getTasks() {
        return tasks;
    }
}