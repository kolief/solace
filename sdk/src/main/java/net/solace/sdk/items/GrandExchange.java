package net.solace.sdk.items;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.util.Text;
import net.solace.api.Static;
import net.solace.api.commons.Predicates;
import net.solace.api.coords.Area;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.items.GrandExchangeState;
import net.solace.api.items.IGrandExchange;
import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.sdk.coords.Coord;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.GameThread;
import net.solace.sdk.game.Vars;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.widgets.Dialog;
import net.solace.sdk.widgets.Widgets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrandExchange {
    private static final Logger log = LoggerFactory.getLogger(GrandExchange.class);
    private static final IGrandExchange GRAND_EXCHANGE = Static.getGrandExchange();
    private static final int F2P_SLOTS = 3;
    private static final int P2P_SLOTS = 8;
    private static final int PRICE_VARBIT = 4398;
    private static final int QUANTITY_VARBIT = 4396;
    private static final Supplier<IWidget> COLLECT_BUTTON = () -> Widgets.get(465, 6, 0);
    private static final Supplier<IWidget> CONFIRM_BUTTON = () -> Widgets.get(465, 30);
    private static final Supplier<IWidget> OFFER_PRICE = () -> Widgets.get(465, 28);
    private static final Supplier<IWidget> ENTER_PRICE = () -> Widgets.get(30474266).getChild(12);
    private static final Supplier<IWidget> ENTER_QUANTITY = () -> Widgets.get(30474266).getChild(7);
    private static final Supplier<IWidget> INCREASE_FIVE = () -> Widgets.get(30474266).getChild(13);
    private static final Supplier<IWidget> DECREASE_FIVE = () -> Widgets.get(30474266).getChild(10);
    private static final Supplier<IWidget> SEARCH_BUTTON = () -> Widgets.get(30474266).getChild(22);
    private static final Supplier<IWidget> SEARCH_RESULT_CONTAINER = () -> Widgets.get(162, 51);

    public static GrandExchangeState getView() {
        return GRAND_EXCHANGE.getState();
    }

    public static boolean isOpen() {
        return GRAND_EXCHANGE.isOpen();
    }

    public static boolean isSetupOpen() {
        return GrandExchange.getView() == GrandExchangeState.BUYING || GrandExchange.getView() == GrandExchangeState.SELLING;
    }

    public static void openBank() {
        TileObjects.getSurrounding(new WorldPoint(3163, 3490, 0), 5, x -> x.getName() != null && x.getName().toLowerCase().contains("exchange booth") && x.hasAction(new String[]{"Bank"})).stream().min(Comparator.comparingDouble(x -> Coord.distanceTo2DHypotenuse(x.getWorldLocation(), Players.getLocal().getWorldLocation()))).ifPresent(bank -> bank.interact("Bank"));
    }

    public static boolean isSelling() {
        return GrandExchange.getView() == GrandExchangeState.SELLING;
    }

    public static boolean isBuying() {
        return GrandExchange.getView() == GrandExchangeState.BUYING;
    }

    public static int getItemId() {
        return Vars.getVarp(1151);
    }

    public static String getItemName() {
        if (GrandExchange.getItemId() == -1) {
            return "";
        }
        ItemComposition itemComposition = Client.getItemComposition(GrandExchange.getItemId());
        if (itemComposition.getName() == null) {
            return "";
        }
        return itemComposition.getName();
    }

    public static void setItem(int id) {
        Client.runScript(754, id, 84);
    }

    public static void setItem(String name) {
        GameThread.invoke(() -> {
            Client.setVarcStrValue(359, name);
            Client.runScript(112, -1, null, "What would you like to buy?");
        });
    }

    public static int getPrice() {
        return Vars.getBit(4398);
    }

    public static void setPrice(int price) {
        if (Dialog.isOpen()) {
            Dialog.enterAmount(price);
            return;
        }
        IWidget enterPricebutton = ENTER_PRICE.get();
        if (Widgets.isVisible(enterPricebutton)) {
            enterPricebutton.interact("Enter price");
        }
    }

    public static int getQuantity() {
        return Vars.getBit(4396);
    }

    public static void setQuantity(int quantity) {
        if (Dialog.isOpen()) {
            Dialog.enterAmount(quantity);
            return;
        }
        IWidget enterQuantityButton = ENTER_QUANTITY.get();
        if (Widgets.isVisible(enterQuantityButton)) {
            enterQuantityButton.interact("Enter quantity");
        }
    }

    public static int getGuidePrice() {
        IWidget priceWidget = OFFER_PRICE.get();
        if (priceWidget != null) {
            return Integer.parseInt(priceWidget.getText().replaceAll("[^0-9]", ""));
        }
        return -1;
    }

    public static void open() {
        if (GrandExchange.isOpen()) {
            return;
        }
        IPlayer local = Players.getLocal();
        WorldArea area = BankLocation.GRAND_EXCHANGE_BANK.getArea();
        WorldPoint center = Area.centerOf((WorldArea)area);
        if (!local.getWorldLocation().isInArea(new WorldArea[]{area})) {
            if (Movement.isWalking()) {
                return;
            }
            Movement.walkTo(center);
            return;
        }
        ITileObject booth = TileObjects.getSurrounding(new WorldPoint(3163, 3490, 0), 5, x -> x.hasAction(new String[]{"Exchange"})).stream().findFirst().orElse(null);
        if (booth != null) {
            if (local.isMoving()) {
                return;
            }
            booth.interact("Exchange");
        }
    }

    public static void sell(Predicate<IItem> filter) {
        IInventoryItem item = Inventory.getFirst(filter);
        if (item != null) {
            Coordinate clickPoint = item.getClickPoint();
            int x = clickPoint != null ? clickPoint.getX() : -1;
            int y = clickPoint != null ? clickPoint.getY() : -1;
            Client.interact(1, MenuAction.CC_OP.getId(), item.getSlot(), 30605312, x, y);
        }
    }

    public static void sell(int ... ids) {
        GrandExchange.sell((IItem x) -> Arrays.stream(ids).anyMatch(y -> y == x.getId() || y == x.getNotedId()));
    }

    public static void sell(String ... names) {
        GrandExchange.sell(Predicates.names((String[])names));
    }

    public static void createBuyOffer() {
        List<IWidget> geRoot = Widgets.getAll(465);
        if (GrandExchange.isFull()) {
            return;
        }
        if (geRoot == null) {
            return;
        }
        for (int i = 7; i < 15; ++i) {
            IWidget buyButton;
            IWidget box = geRoot.get(i);
            if (box == null || !Widgets.isVisible(buyButton = box.getChild(3))) continue;
            buyButton.interact(0);
            return;
        }
    }

    public static void abortOffer(int itemId) {
        List<IWidget> geRoot = Widgets.getAll(465);
        if (GrandExchange.isEmpty()) {
            return;
        }
        if (geRoot == null) {
            return;
        }
        for (int i = 7; i < 15; ++i) {
            IWidget itemIdBox;
            IWidget abortBox;
            IWidget box = geRoot.get(i);
            if (box == null || !Widgets.isVisible(abortBox = box.getChild(2)) || !abortBox.hasAction(new String[]{"Abort offer"}) || !Widgets.isVisible(itemIdBox = box.getChild(18)) || itemIdBox.getItemId() != itemId) continue;
            abortBox.interact("Abort offer");
            return;
        }
    }

    public static void abortOffer(String itemName) {
        List<IWidget> geRoot = Widgets.getAll(465);
        if (GrandExchange.isEmpty()) {
            return;
        }
        if (geRoot == null) {
            return;
        }
        for (int i = 7; i < 15; ++i) {
            IWidget itemIdBox;
            IWidget abortBox;
            IWidget box = geRoot.get(i);
            if (box == null || !Widgets.isVisible(abortBox = box.getChild(2)) || !abortBox.hasAction(new String[]{"Abort offer"}) || !Widgets.isVisible(itemIdBox = box.getChild(19)) || !itemIdBox.getText().toLowerCase().contains(itemName.toLowerCase())) continue;
            abortBox.interact("Abort offer");
            return;
        }
    }

    public static boolean isFull() {
        return GrandExchange.getEmptySlots() == 0;
    }

    public static boolean isEmpty() {
        return GrandExchange.getOffers().isEmpty();
    }

    public static int getEmptySlots() {
        return Game.getMembershipDays() <= 0 ? 3 - GrandExchange.getOffers().size() : 8 - GrandExchange.getOffers().size();
    }

    public static List<GESearchResult> getSearchResults() {
        if (!GrandExchange.isSearchResultsOpen()) {
            return Collections.emptyList();
        }
        IWidget[] children = SEARCH_RESULT_CONTAINER.get().getChildren();
        if (children == null || children.length % 3 != 0) {
            return Collections.emptyList();
        }
        return IntStream.range(0, children.length / 3).mapToObj(i -> {
            IWidget mainWidget = children[i * 3];
            IWidget itemIDWidget = children[i * 3 + 2];
            return new AbstractMap.SimpleEntry<IWidget, IWidget>(mainWidget, itemIDWidget);
        }).filter(entry -> entry.getKey() != null && entry.getValue() != null).map(entry -> {
            int itemID = ((IWidget)entry.getValue()).getItemId();
            String itemName = Text.removeTags((String)((IWidget)entry.getKey()).getName());
            return itemID == -1 || itemName == null || itemName.isEmpty() ? null : new GESearchResult(itemID, itemName, (IWidget)entry.getKey());
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<GrandExchangeOffer> getOffers() {
        ArrayList<GrandExchangeOffer> out = new ArrayList<GrandExchangeOffer>();
        GrandExchangeOffer[] offers = Client.getGrandExchangeOffers();
        if (offers != null) {
            for (GrandExchangeOffer offer : offers) {
                if (offer.getItemId() <= 0) continue;
                out.add(offer);
            }
        }
        return out;
    }

    public static boolean canCollect() {
        return Widgets.isVisible(COLLECT_BUTTON.get());
    }

    public static void collect() {
        GrandExchange.collect(false);
    }

    public static void collect(boolean toBank) {
        IWidget collect = COLLECT_BUTTON.get();
        if (Widgets.isVisible(collect)) {
            collect.interact(toBank ? "Collect to bank" : "Collect to inventory");
        }
    }

    public static void confirm() {
        IWidget confirm = CONFIRM_BUTTON.get();
        if (Widgets.isVisible(confirm)) {
            confirm.interact("Confirm");
        }
    }

    public static boolean isSearchResultsOpen() {
        return Widgets.isVisible(SEARCH_RESULT_CONTAINER.get());
    }

    public static boolean isSearchingItem() {
        return GRAND_EXCHANGE.isSearchingItem();
    }

    public static void openItemSearch() {
        IWidget widget = SEARCH_BUTTON.get();
        if (!Widgets.isVisible(widget)) {
            return;
        }
        AutomatedMenu menu = AutomatedMenu.builder().identifier(1).opcode(MenuAction.CC_OP).param0(0).param1(30474266).clickPoint(widget.getClickPoint()).clickPointSupplier(() -> ((IWidget)widget).getClickPoint()).build();
        Client.interact(menu);
    }

    public static boolean sell(int itemId, int quantity, int price) {
        return GrandExchange.exchange(false, itemId, quantity, price, true, false);
    }

    public static boolean sell(int itemId, int price, boolean collect, boolean toBank) {
        IInventoryItem itemToSell = Inventory.getFirst(itemId);
        if (itemToSell == null) {
            return false;
        }
        int itemCount = GameThread.invokeAndWait(() -> Inventory.getCount(itemToSell.isNoted() || itemToSell.isStackable(), itemId));
        return GrandExchange.exchange(false, itemId, itemCount, price, collect, toBank);
    }

    public static boolean sell(int itemId, int quantity, int price, boolean collect, boolean toBank) {
        return GrandExchange.exchange(false, itemId, quantity, price, collect, toBank);
    }

    public static boolean buy(int itemId, int quantity, int price) {
        return GrandExchange.exchange(true, itemId, quantity, price, true, false);
    }

    public static boolean buy(int itemId, int quantity, int price, boolean collect, boolean toBank) {
        return GrandExchange.exchange(true, itemId, quantity, price, collect, toBank);
    }

    public static boolean buy(String itemName, int quantity, int price) {
        return GrandExchange.exchange(true, itemName, quantity, price, true, false);
    }

    public static boolean buy(String itemName, int quantity, int price, boolean collect, boolean toBank) {
        return GrandExchange.exchange(true, itemName, quantity, price, collect, toBank);
    }

    public static boolean exchange(boolean buy, int itemId, int quantity, int price) {
        return GrandExchange.exchange(buy, itemId, quantity, price, true, false);
    }

    public static boolean exchange(boolean buy, String itemName, int quantity, int price, boolean collect, boolean toBank) {
        if (!GrandExchange.isOpen()) {
            GrandExchange.open();
            return false;
        }
        if (collect && GrandExchange.canCollect()) {
            GrandExchange.collect(toBank);
            return false;
        }
        if (buy) {
            if (!GrandExchange.isBuying()) {
                GrandExchange.createBuyOffer();
                return false;
            }
        } else if (!GrandExchange.isSelling()) {
            GrandExchange.sell(itemName);
            return false;
        }
        if (GrandExchange.getItemId() == -1 || GrandExchange.getItemName().isBlank()) {
            if (buy) {
                List<GESearchResult> results;
                if (!GrandExchange.isSearchResultsOpen()) {
                    GrandExchange.openItemSearch();
                }
                if ((results = GrandExchange.getSearchResults()).isEmpty()) {
                    GrandExchange.setItem(itemName);
                    return false;
                }
                GESearchResult result = results.stream().filter(x -> x.itemName.equalsIgnoreCase(itemName)).findFirst().orElse(null);
                if (result == null) {
                    result = results.stream().filter(x -> x.itemName.toLowerCase().contains(itemName.toLowerCase())).findFirst().orElse(null);
                }
                if (result != null) {
                    result.chooseOption();
                    return false;
                }
            } else {
                GrandExchange.sell(itemName);
            }
            return false;
        }
        if (GrandExchange.getPrice() != price) {
            log.debug("Setting price to {}", (Object)price);
            GrandExchange.setPrice(price);
            return false;
        }
        if (GrandExchange.getQuantity() != quantity) {
            log.debug("Setting quantity to {}", (Object)quantity);
            GrandExchange.setQuantity(quantity);
            return false;
        }
        if (GrandExchange.getPrice() == price && GrandExchange.getQuantity() == quantity) {
            log.debug("Confirming offer");
            GrandExchange.confirm();
            return true;
        }
        return false;
    }

    public static boolean exchange(boolean buy, int itemId, int quantity, int price, boolean collect, boolean toBank) {
        if (!GrandExchange.isOpen()) {
            GrandExchange.open();
            return false;
        }
        if (collect && GrandExchange.canCollect()) {
            GrandExchange.collect(toBank);
            return false;
        }
        if (buy) {
            if (!GrandExchange.isBuying()) {
                GrandExchange.createBuyOffer();
                return false;
            }
        } else if (!GrandExchange.isSelling()) {
            GrandExchange.sell(itemId);
            return false;
        }
        int notedId = GameThread.invokeAndWait(() -> Client.getItemComposition(itemId).getLinkedNoteId());
        if (GrandExchange.getItemId() == -1 || GrandExchange.getItemId() != itemId && GrandExchange.getItemId() != notedId) {
            if (buy) {
                if (!GrandExchange.isSearchingItem()) {
                    GrandExchange.openItemSearch();
                }
                GrandExchange.setItem(itemId);
            } else {
                GrandExchange.sell(itemId);
            }
            return false;
        }
        if (GrandExchange.getPrice() != price) {
            log.debug("Setting price to {}", (Object)price);
            GrandExchange.setPrice(price);
            return false;
        }
        if (GrandExchange.getQuantity() != quantity) {
            log.debug("Setting quantity to {}", (Object)quantity);
            GrandExchange.setQuantity(quantity);
            return false;
        }
        if (GrandExchange.getPrice() == price && GrandExchange.getQuantity() == quantity) {
            log.debug("Confirming offer");
            GrandExchange.confirm();
            return true;
        }
        return false;
    }

    public static class GESearchResult {
        private final int itemID;
        private final String itemName;
        private final IWidget mainWidget;

        public GESearchResult(int itemID, String itemName, IWidget mainWidget) {
            this.itemID = itemID;
            this.itemName = itemName;
            this.mainWidget = mainWidget;
        }

        public void chooseOption() {
            if (Widgets.isVisible(this.mainWidget)) {
                this.mainWidget.interact("Select");
            }
        }

        public int getItemID() {
            return this.itemID;
        }

        public String getItemName() {
            return this.itemName;
        }

        public IWidget getMainWidget() {
            return this.mainWidget;
        }
    }
}

