package net.solace.loader.plugins.shops;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import net.solace.api.Static;
import net.solace.api.ui.ColorScheme;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.commons.Rand;
import net.solace.api.domain.SceneEntity;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.Task;
import net.solace.api.plugins.TaskPlugin;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.shops.tasks.Await;
import net.solace.loader.plugins.shops.tasks.DepositItems;
import net.solace.loader.plugins.shops.tasks.DrinkStamina;
import net.solace.loader.plugins.shops.tasks.Exchange;
import net.solace.loader.plugins.shops.tasks.OpenRunePacks;
import net.solace.loader.plugins.shops.tasks.StopPlugin;
import net.solace.loader.plugins.shops.tasks.WithdrawGold;
import net.solace.loader.plugins.shops.tasks.WorldHop;
import net.solace.sdk.entities.Players;
import net.solace.sdk.items.Bank;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.utils.MessageUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Solace Shops",
        description = "Solace Shops",
        tags = {"shops", "solace"}
)
@Slf4j
public class SolaceShopsPlugin extends TaskPlugin {
    @Inject
    @Getter
    private SolaceShopsConfig config;

    @Inject
    private Client client;

    @Inject
    private BreakHandler reflectBreakHandler;

    @Inject
    private ShopperOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Getter
    private SelectedEntity selectedShop;

    @Getter
    @Setter
    private boolean shouldHop;

    @Getter
    @Setter
    private int hopDelay = 8;

    @Getter
    @Setter
    private GameState lastGameState;

    @Getter
    @Setter
    private HashMap<Integer, Integer> purchasedItems = new HashMap<>();

    @Override
    public void startUp() throws Exception {
        overlayManager.add(overlay);
        reflectBreakHandler.registerPlugin(this);
        reflectBreakHandler.startPlugin(this);

        selectedShop = null;
        hopDelay = 4;
        lastGameState = null;
        purchasedItems.clear();
    }

    @Override
    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reflectBreakHandler.unregisterPlugin(this);
        reflectBreakHandler.stopPlugin(this);
    }

    private final Task[] tasks = new Task[]{
            new Await(this),
            new StopPlugin(this),
            new OpenRunePacks(this),
            new DepositItems(this),
            new WithdrawGold(this),
            new WorldHop(this),
            new DrinkStamina(this),
            new Exchange(this)
    };

    @Override
    public int loop() {
        if (reflectBreakHandler.isBreakActive(this)) {
            return 1000;
        }

        if (reflectBreakHandler.shouldBreak(this)) {
            reflectBreakHandler.startBreak(this);
            return -1;
        }

        if (getHopDelay() > 0) {
            log.debug("Idling for {} ticks", getHopDelay());
            setHopDelay(getHopDelay() - 1);
            return -1;
        }

        return super.loop();
    }

    public void openShop() {
        if (Movement.isWalking()) {
            return;
        }

        SceneEntity shop = selectedShop.get();

        var returnTile = selectedShop.getReturnTile();

        if (selectedShop.distance() < 10 && shop != null && (shop.isInteractable() || returnTile != null && Players.getLocal().getWorldLocation().equals(returnTile))) {
            shop.interact("Trade", "Buy-food");
        } else {
            Movement.walkTo(returnTile != null ? returnTile : selectedShop.getWorldPoint());
        }
    }

    public void openBank() {
        Bank.open(config.bankLocation());
    }

    public List<Integer> getItemIds() {
        return Text.fromCSV(config.itemId()).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public boolean shouldOpenPacks() {
        return config.openPacks();
    }

    public int getMinStock() {
        return config.minItemsInShop();
    }

    public int getMaxStock() {
        return config.maxItemsInShop();
    }

    public int getActionsPetTick() {
        return config.actionsPerTick();
    }

    public int getCurrencyId() {
        return config.currencyType().itemID;
    }

    @Subscribe
    private void onMenuOpened(MenuOpened e) {
        List<? extends SceneEntity> hovered = Static.getClient().getHoveredEntities().stream()
                .filter(entity -> entity instanceof INPC || entity instanceof ITileObject)
                .collect(Collectors.toList());

        for (SceneEntity sceneEntity : hovered) {
            if (sceneEntity.hasAction(x -> true)) {
                String color = sceneEntity instanceof INPC ? "<col=ffff00>" : "<col=00ffff>";
                MenuEntry parentMenu = client.createMenuEntry(1)
                        .setOption(ColorScheme.brandCol("Solace Shops"))
                        .setTarget(color + sceneEntity.getName() + "</col>")
                        .setType(MenuAction.RUNELITE);

                var subMenu = parentMenu.createSubMenu();

                subMenu.createMenuEntry(0)
                        .setOption("Select Shop")
                        .setType(MenuAction.RUNELITE)
                        .onClick(menu -> selectedShop = new SelectedEntity(sceneEntity.getId(), sceneEntity.getWorldLocation(), sceneEntity.getClass()));
            }
        }

        var selectedTile = Static.getClient().getSelectedSceneTile();

        if (selectedTile != null) {
            MenuEntry parentMenu = client.createMenuEntry(1)
                    .setOption(ColorScheme.brandCol("Solace Shops"))
                    .setTarget("Set return tile")
                    .setType(MenuAction.RUNELITE)
                    .onClick(menu -> {
                        if (selectedShop == null) {
                            MessageUtils.addMessage("Select a shop first.");
                            return;
                        }

                        selectedShop.setReturnTile(selectedTile.getWorldLocation());
                    });
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage e) {
        if (e.getType() == ChatMessageType.GAMEMESSAGE) {
            String message = e.getMessage();
            if (message.contains("You can't sell this item to this shop.")) {
                MessageUtils.addMessage("Shop does not buy this item.");
                selectedShop = null;
            }
            if (message.contains("Ironmen may not buy items that are overstocked in a shop.")) {
                setShouldHop(true);
            }
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.HOPPING) {
            shouldHop = false;
        }

        if (getLastGameState() == null
                || (getLastGameState() != GameState.LOGGED_IN && e.getGameState() == GameState.LOGGED_IN)) {
            setHopDelay(Rand.nextInt(5,7));
        }

        setLastGameState(e.getGameState());
    }

    @Override
    public Task[] getTasks() {
        return tasks;
    }

    public enum Currency {
        COINS(ItemID.COINS),
        TOKKUL(ItemID.TZHAAR_TOKEN);

        private final int itemID;

        Currency(int itemID) {
            this.itemID = itemID;
        }

        public int getItemID() {
            return itemID;
        }
    }

    @Provides
    SolaceShopsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceShopsConfig.class);
    }
}