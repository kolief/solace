package net.solace.loader.plugins.chartershipper;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.magic.Spell;
import net.solace.api.magic.SpellBook;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.widgets.Tab;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.Worlds;
import net.solace.sdk.input.Keyboard;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.items.Shop;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.plugins.Plugins;
import net.solace.sdk.utils.MessageUtils;
import net.solace.sdk.widgets.Dialog;
import net.solace.sdk.widgets.Tabs;

import javax.swing.SwingUtilities;
import java.time.Instant;
import java.util.HashMap;

@PluginDescriptor(name = "Solace Charter Shipper")
@Slf4j
public class SolaceCharterShipperPlugin extends LoopedPlugin {
    private final HashMap<Integer, Long> usedWorlds = new HashMap<>();

    private boolean shouldHop = false;

    @Inject
    private SolaceCharterShipperConfig config;

    @Inject
    private BreakHandler breakHandler;

    @Override
    public void startUp() {
        usedWorlds.clear();

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
        if (!Game.isLoggedIn() || breakHandler.isBreakActive(this)) {
            return -2;
        }

        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return -1;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return -1;
        }

        int ash = Inventory.getCount(ItemID.SODA_ASH);
        int bucket = Inventory.getCount(ItemID.BUCKET_SAND);

        if (ash > 0 && bucket > 0) {
            Spell glassMake = SpellBook.Lunar.SUPERGLASS_MAKE;

            if (glassMake.canCast()) {
                glassMake.cast();
                return -4;
            }

            MessageUtils.addMessage("Can't cast Superglass Make, stopping");
            SwingUtilities.invokeLater(() -> Plugins.stopPlugin(this));
            return -1;
        }

        var glass = Inventory.getFirst(ItemID.MOLTEN_GLASS);
        var pipe = Inventory.getFirst(ItemID.GLASSBLOWINGPIPE);
        if (Movement.isWalking()) {
            return -1;
        }

        if (glass != null && pipe != null) {
            if (!Players.getLocal().isIdle()) {
                return -1;
            }

            if (Dialog.isOpen()) {
                Keyboard.type(config.item().getMenuIndex());
                return Inventory.getCount(ItemID.MOLTEN_GLASS) * -3;
            }

            if (!Tabs.isOpen(Tab.INVENTORY)) {
                Tabs.open(Tab.INVENTORY);
                return -1;
            }

            pipe.useOn(glass);
            return -1;
        }

        var lightOrbs = Inventory.getAll(ItemID.DORGESH_LIGHTBULB_NOFILAMENT);
        if (!lightOrbs.isEmpty()) {
            if (!Tabs.isOpen(Tab.INVENTORY)) {
                Tabs.open(Tab.INVENTORY);
                return -1;
            }

            lightOrbs.forEach(i -> i.interact("Drop"));
            return -1;
        }

        if (shouldHop) {
            if (Shop.isOpen()) {
                Movement.walk(Players.getLocal().getWorldLocation());
                return -2;
            }

            usedWorlds.put(Worlds.getCurrentId(), Instant.now().getEpochSecond() + ((10 - Shop.getStock(ItemID.BUCKET_SAND)) * 60L));
            Worlds.hopTo(Worlds.getRandom(world -> world.getPlayerCount() <= 1500 && Worlds.isNormal(world) && Worlds.isMembers(world) && (usedWorlds.get(world.getId()) == null || (Instant.now().getEpochSecond() - usedWorlds.get(world.getId()) > 600))));
            return -3;
        }

        if (Shop.isOpen()) {
            int itemId = config.item().getItemId();
            if (itemId != ItemID.DORGESH_LIGHT_BULB) {
                Shop.sellFifty(itemId);
            }

            if (Shop.getStock(ItemID.BUCKET_SAND) < 10 || Shop.getStock(ItemID.SODA_ASH) < 10 || Shop.getStock(ItemID.BUCKET_SAND) > 10 || Shop.getStock(ItemID.SODA_ASH) > 10) {
                shouldHop = true;
                return -1;
            }

            if (Inventory.getCount(ItemID.BUCKET_SAND) > 0) {
                Shop.sellFifty(ItemID.BUCKET_SAND);
            }
            if (Inventory.getCount(ItemID.SODA_ASH) > 0) {
                Shop.sellFifty(ItemID.SODA_ASH);
            } else {
                int coinQuantity = Inventory.getCount(true, ItemID.COINS);

                if (coinQuantity <= 5000) {
                    MessageUtils.addMessage("Low on coins, stopping");
                    SwingUtilities.invokeLater(() -> Plugins.stopPlugin(this));
                    return -1;
                }
                Shop.buyTen(ItemID.BUCKET_SAND);
                Shop.buyTen(ItemID.SODA_ASH);
            }

            usedWorlds.put(Worlds.getCurrentId(), Instant.now().getEpochSecond());
            return -2;
        }

        var crew = NPCs.getNearest("Trader Crewmember");

        if (crew != null) {
            crew.interact("Trade");
        }
        return -2;
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.HOPPING) {
            shouldHop = false;
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage e) {
        String message = Text.sanitize(e.getMessage());
        if (message.startsWith("Please finish what")) {
            Movement.walk(Players.getLocal());
        }
    }

    @Provides
    SolaceCharterShipperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceCharterShipperConfig.class);
    }
}