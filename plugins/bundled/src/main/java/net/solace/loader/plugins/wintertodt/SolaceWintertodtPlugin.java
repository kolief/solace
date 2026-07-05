package net.solace.loader.plugins.wintertodt;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.domain.items.IItem;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.Task;
import net.solace.api.plugins.TaskPlugin;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.wintertodt.tasks.AwaitGame;
import net.solace.loader.plugins.wintertodt.tasks.Banking;
import net.solace.loader.plugins.wintertodt.tasks.ChopRoot;
import net.solace.loader.plugins.wintertodt.tasks.DropJunk;
import net.solace.loader.plugins.wintertodt.tasks.EnterGame;
import net.solace.loader.plugins.wintertodt.tasks.EscapeSnow;
import net.solace.loader.plugins.wintertodt.tasks.Exit;
import net.solace.loader.plugins.wintertodt.tasks.FeedBrazier;
import net.solace.loader.plugins.wintertodt.tasks.Fletch;
import net.solace.loader.plugins.wintertodt.tasks.Heal;
import net.solace.loader.plugins.wintertodt.tasks.Hide;
import net.solace.loader.plugins.wintertodt.tasks.LightBrazier;
import net.solace.loader.plugins.wintertodt.tasks.LightFinalLogs;
import net.solace.loader.plugins.wintertodt.tasks.RemoveDialog;
import net.solace.loader.plugins.wintertodt.tasks.RepairBrazier;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.game.Vars;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Equipment;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.widgets.Dialog;
import net.solace.sdk.widgets.Widgets;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(name = "Solace Wintertodt")
@Slf4j
public class SolaceWintertodtPlugin extends TaskPlugin {
    private final Task[] tasks =
            {
                    new Banking(this),
                    new RemoveDialog(this),
                    new Exit(this),
                    new Heal(this),
                    new Hide(this),
                    new DropJunk(this),
                    new AwaitGame(this),
                    new EscapeSnow(this),
                    new RepairBrazier(this),
                    new LightBrazier(this),
                    new LightFinalLogs(this),
                    new Fletch(this),
                    new FeedBrazier(this),
                    new ChopRoot(this),
                    new EnterGame(this),
            };
    private Task lastTask = null;

    @Getter
    @Setter
    private boolean interrupted;

    @Inject
    @Getter
    private SolaceWintertodt config;

    @Inject
    @Getter
    private Client client;

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

    @Override
    public int loop() {
        if (breakHandler.isBreakActive(this)) {
            return 1000;
        }

        return super.loop();
    }

    public boolean isInside() {
        return Players.getLocal().getWorldLocation().getY() > WintertodtConstants.ENTRANCE_COORD.getY();
    }

    public boolean isGameStarted() {
        return Vars.getBit(VarbitID.WINT_TRANSMIT_RESPAWNDELAY) == 0;
    }

    public String getFoodName() {
        return getConfig().food().replaceAll("[0-9*()]+", "");
    }

    public IItem getFoodItem() {
        return Inventory.getFirst(item -> item.getName().toLowerCase().contains(getFoodName().toLowerCase()));
    }

    public int getFoodCount() {
        return Inventory.getCount(item -> item.getName().toLowerCase().contains(getFoodName().toLowerCase()));
    }

    public int getTimer() {
        return Vars.getBit(VarbitID.WINT_TRANSMIT_RESPAWNDELAY);
    }

    public int getPoints() {
        var widget = Widgets.get(396, 6);
        if (!Widgets.isVisible(widget)) {
            return 0;
        }

        return Integer.parseInt(widget.getText().replaceAll("\\D", ""));
    }

    public int getEnergy() {
        Widget component = Widgets.get(396, 20);
        if (component != null) {
            Matcher matcher = Pattern.compile("(\\d+)").matcher(component.getText());
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }

        return -1;
    }

    public int enterDoor() {
        if (breakHandler.shouldBreak(this) && !isInside()) {
            breakHandler.startBreak(this);
            return -2;
        }

        if (Movement.isWalking()) {
            return -2;
        }

        if (Dialog.isOpen() && !Bank.isOpen()) {
            Dialog.chooseOption(0);
            return -2;
        }

        var door = TileObjects.getFirstAt(WintertodtConstants.ENTRANCE_COORD, WintertodtConstants.ENTRANCE_DOOR_ID);
        if (door == null || door.distanceTo(Players.getLocal()) > 10) {
            Movement.walkTo(WintertodtConstants.ENTRANCE_COORD);
            return -3;
        }

        door.interact("Enter");
        return -4;
    }

    public HashMap<String, Integer> getRequiredItems() {
        HashMap<String, Integer> requiredItems = new HashMap<>();

        if (!Inventory.contains(getConfig().axe()) && !Equipment.contains(getConfig().axe())) {
            requiredItems.put(getConfig().axe(), 1);
        }

        int food = Inventory.getCount(item -> item.getName().toLowerCase().contains(getFoodName().toLowerCase()));
        if (food < getConfig().minFoodAmount()) {
            requiredItems.put(getConfig().food(), getConfig().foodAmount() - food);
        } else if (food > getConfig().foodAmount()) {
            requiredItems.put(getConfig().food(), getConfig().foodAmount() - food);
        }

        if (canUseTorch()) {
            if (useOffhand()) {
                if (!Inventory.contains("Bruma torch (off-hand)") && !Equipment.contains("Bruma torch (off-hand)")) {
                    requiredItems.put("Bruma torch (off-hand)", 1);
                }
            } else {
                if (!Inventory.contains("Bruma torch") && !Equipment.contains("Bruma torch")) {
                    requiredItems.put("Bruma torch", 1);
                }
            }
        } else {
            if (!Inventory.contains("Tinderbox")) {
                requiredItems.put("Tinderbox", 1);
            }
        }

        if (getConfig().fletch() && !Inventory.contains("Knife")) {
            requiredItems.put("Knife", 1);
        }

        if (getConfig().repair() && !Inventory.contains("Hammer")) {
            requiredItems.put("Hammer", 1);
        }

        return requiredItems;
    }

    public boolean canUseTorch() {
        return Bank.contains(ItemID.WINT_TORCH, ItemID.WINT_TORCH_OFFHAND) || Equipment.contains(ItemID.WINT_TORCH, ItemID.WINT_TORCH_OFFHAND) || Inventory.contains(ItemID.WINT_TORCH, ItemID.WINT_TORCH_OFFHAND);
    }

    public boolean useOffhand() {
        return Equipment.contains(ItemID.WINT_TORCH_OFFHAND) || Inventory.contains(ItemID.WINT_TORCH_OFFHAND) || Bank.contains(ItemID.WINT_TORCH_OFFHAND);
    }

    public boolean isChopping() {
        return WintertodtConstants.CHOP_ANIMATION_IDS.contains(Players.getLocal().getAnimation());
    }


    public boolean isPyromancerAlive() {
        return NPCs.getNearest(NpcID.WINT_WIZARD) != null;
    }

    @Subscribe
    private void onHitsplatApplied(HitsplatApplied e) {
        if (e.getActor() == Players.getLocal().getWrapped()) {
            interrupted = true;
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage e) {
        if (e.getMessage().equals("It heals some health.")) {
            interrupted = true;
        }
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if (!Objects.equals(getCurrentTask(), lastTask)) {
            log.info(
                    "Task changed {} -> {}",
                    lastTask == null ? null : lastTask,
                    getCurrentTask() == null ? null : getCurrentTask()
            );
            lastTask = getCurrentTask();
        }
    }

    @Override
    public Task[] getTasks() {
        return tasks;
    }

    @Provides
    SolaceWintertodt provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceWintertodt.class);
    }
}
