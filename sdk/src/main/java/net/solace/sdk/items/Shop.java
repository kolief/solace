package net.solace.sdk.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.solace.api.domain.widgets.IWidget;
import net.solace.sdk.widgets.Widgets;
import org.apache.commons.lang3.StringUtils;

public class Shop {
    private static final Supplier<IWidget> SHOP = () -> Widgets.get(300, 0);
    private static final Supplier<IWidget> SHOP_ITEMS = () -> Widgets.get(300, 16);
    private static final Supplier<IWidget> INVENTORY = () -> Widgets.get(301, 0);

    public static boolean isOpen() {
        return Widgets.isVisible(SHOP.get());
    }

    public static List<IWidget> getItemsWidgets() {
        IWidget items = SHOP_ITEMS.get();
        if (!Widgets.isVisible(items)) {
            return new ArrayList<IWidget>();
        }
        IWidget[] children = items.getChildren();
        if (children == null) {
            return new ArrayList<IWidget>();
        }
        return Arrays.asList(children);
    }

    public static IWidget getWidgetForItem(int itemId) {
        return Shop.getItemsWidgets().stream().filter(itemWidget -> itemWidget.getItemId() == itemId).findFirst().orElse(null);
    }

    public static int getStock(int itemId) {
        IWidget itemWidget = Shop.getWidgetForItem(itemId);
        if (itemWidget == null) {
            return 0;
        }
        return itemWidget.getItemQuantity();
    }

    public static int[] getActionQuantities(int itemId) {
        IWidget itemWidget = Shop.getWidgetForItem(itemId);
        if (itemWidget == null) {
            return new int[0];
        }
        String[] actions = itemWidget.getActions();
        if (actions == null || actions.length == 0) {
            return new int[0];
        }
        return Arrays.stream(actions).filter(Objects::nonNull).mapToInt(action -> {
            try {
                return Integer.parseInt(action.replaceAll("[^0-9]", ""));
            }
            catch (NumberFormatException e) {
                return 0;
            }
        }).toArray();
    }

    public static int[] getActionQuantities(IWidget widget) {
        if (widget == null) {
            return new int[0];
        }
        String[] actions = widget.getActions();
        if (actions == null || actions.length == 0) {
            return new int[0];
        }
        return Arrays.stream(actions).filter(Objects::nonNull).mapToInt(action -> {
            try {
                return Integer.parseInt(action.replaceAll("[^0-9]", ""));
            }
            catch (NumberFormatException e) {
                return 0;
            }
        }).toArray();
    }

    public static int getMaxAction(int itemId) {
        int[] quantities = Shop.getActionQuantities(itemId);
        if (quantities.length == 0) {
            return 0;
        }
        return Arrays.stream(quantities).max().orElse(0);
    }

    public static int getMaxAction(IWidget widget) {
        int[] quantities = Shop.getActionQuantities(widget);
        if (quantities.length == 0) {
            return 0;
        }
        return Arrays.stream(quantities).max().orElse(0);
    }

    private static String getActionForQuantity(IWidget itemWidget, int quantity) {
        String[] actions = itemWidget.getActions();
        if (actions == null || actions.length == 0) {
            return null;
        }
        int maxQuantity = Shop.getMaxAction(itemWidget);
        if (quantity > maxQuantity) {
            quantity = maxQuantity;
        }
        int finalQuantity = quantity;
        return Arrays.stream(actions).filter(Objects::nonNull).filter(action -> action.endsWith(String.valueOf(finalQuantity))).findFirst().orElse(null);
    }

    public static void buyOne(int itemId) {
        Shop.buy(itemId, 1);
    }

    public static void buyOne(String itemName) {
        Shop.buy(itemName, 1);
    }

    public static void buyFive(int itemId) {
        Shop.buy(itemId, 5);
    }

    public static void buyFive(String itemName) {
        Shop.buy(itemName, 5);
    }

    public static void buyTen(int itemId) {
        Shop.buy(itemId, 10);
    }

    public static void buyTen(String itemName) {
        Shop.buy(itemName, 10);
    }

    public static void buyFifty(int itemId) {
        Shop.buy(itemId, 50);
    }

    public static void buyFifty(String itemName) {
        Shop.buy(itemName, 50);
    }

    public static void sellOne(int itemId) {
        Shop.sell(itemId, 1);
    }

    public static void sellFive(int itemId) {
        Shop.sell(itemId, 5);
    }

    public static void sellTen(int itemId) {
        Shop.sell(itemId, 10);
    }

    public static void sellFifty(int itemId) {
        Shop.sell(itemId, 50);
    }

    public static List<Integer> getItems() {
        ArrayList<Integer> out = new ArrayList<Integer>();
        IWidget container = SHOP_ITEMS.get();
        if (container == null) {
            return out;
        }
        IWidget[] items = container.getChildren();
        if (items == null) {
            return out;
        }
        for (IWidget item : items) {
            if (item.getItemId() == -1) continue;
            out.add(item.getItemId());
        }
        return out;
    }

    private static void buy(int itemId, int amount) {
        Shop.exchange(itemId, amount, SHOP_ITEMS.get());
    }

    private static void buy(String itemName, int amount) {
        Shop.exchange(itemName, amount, SHOP_ITEMS.get());
    }

    private static void sell(int itemId, int amount) {
        Shop.exchange(itemId, amount, INVENTORY.get());
    }

    private static void exchange(int itemId, int amount, IWidget container) {
        if (container == null) {
            return;
        }
        IWidget[] items = container.getChildren();
        if (items == null) {
            return;
        }
        for (IWidget item : items) {
            if (item.getItemId() != itemId) continue;
            String action = Shop.getActionForQuantity(item, amount);
            if (action == null) {
                return;
            }
            item.interact(action);
            return;
        }
    }

    private static void exchange(String itemName, int amount, IWidget container) {
        if (container == null) {
            return;
        }
        IWidget[] items = container.getChildren();
        if (items == null) {
            return;
        }
        for (IWidget item : items) {
            String nestedName = StringUtils.substringBetween((String)item.getName(), (String)">", (String)"<");
            if (nestedName == null || !nestedName.equals(itemName)) continue;
            String action = Shop.getActionForQuantity(item, amount);
            if (action == null) {
                return;
            }
            item.interact(action);
            return;
        }
    }
}

