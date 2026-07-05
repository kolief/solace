package net.solace.loader.plugins.shops.tasks;

import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.shops.ShopperMode;
import net.solace.loader.plugins.shops.SolaceShopsPlugin;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.items.Shop;

public class Exchange extends PluginTask<SolaceShopsPlugin> {
    public Exchange(SolaceShopsPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !Inventory.isFull()
               && Inventory.getCount(true, getContext().getCurrencyId()) >= getContext().getConfig().minGold();
    }

    @Override
    public int execute() {
        if (Shop.isOpen()) {
            if (stockCheck()) {
                getContext().setShouldHop(true);
                return -1;
            }

            int minStock = getContext().getMinStock();
            int maxStock = getContext().getMaxStock();

            for (Integer itemId : getContext().getItemIds()) {
                if (isSell()) {
                    var item = Inventory.getFirst(itemId);

                    int sellableAmount = maxStock - Shop.getStock(itemId);

                    if (item.isNoted()) {
                        sellableAmount = maxStock - Shop.getStock(item.getNotedId());
                    }

                    if (sellableAmount == 0) {
                        continue;
                    }

                    if (sellableAmount <= 4) {
                        Shop.sellOne(itemId);
                        continue;
                    }

                    if (sellableAmount <= 9) {
                        Shop.sellFive(itemId);
                        continue;
                    }

                    if (sellableAmount <= 49) {
                        Shop.sellTen(itemId);
                        continue;
                    }

                    if (sellableAmount == 50) {
                        Shop.sellFifty(itemId);
                        continue;
                    }

                    switch (getContext().getConfig().shopperAmount()) {
                        case FIFTY:
                            loopActions(() -> Shop.sellFifty(itemId));
                            break;
                        case TEN:
                            loopActions(() -> Shop.sellTen(itemId));
                            break;
                        case FIVE:
                            loopActions(() -> Shop.sellFive(itemId));
                            break;
                        case ONE:
                            loopActions(() -> Shop.sellOne(itemId));
                            break;
                    }
                } else {

                    int maxBuy = getContext().getConfig().maxBuy();

                    if (maxBuy > 0 && getContext().getPurchasedItems().getOrDefault(itemId, 0) >= maxBuy) {
                        continue;
                    }

                    int buyableAmount = getContext().getConfig().ignoreStockWhenBuying() ? Integer.MAX_VALUE : Shop.getStock(itemId) - minStock;

                    if (buyableAmount <= 4) {
                        Shop.buyOne(itemId);
                        getContext().getPurchasedItems().put(itemId, getContext().getPurchasedItems().getOrDefault(itemId, 0) + 1);
                        continue;
                    }

                    if (buyableAmount <= 9) {
                        Shop.buyFive(itemId);
                        getContext().getPurchasedItems().put(itemId, getContext().getPurchasedItems().getOrDefault(itemId, 0) + 5);
                        continue;
                    }

                    if (buyableAmount <= 49) {
                        Shop.buyTen(itemId);
                        getContext().getPurchasedItems().put(itemId, getContext().getPurchasedItems().getOrDefault(itemId, 0) + 10);
                        continue;
                    }

                    if (buyableAmount == 50) {
                        Shop.buyFifty(itemId);
                        getContext().getPurchasedItems().put(itemId, getContext().getPurchasedItems().getOrDefault(itemId, 0) + 50);
                        continue;
                    }

                    switch (getContext().getConfig().shopperAmount()) {
                        case FIFTY:
                            loopActions(() -> {
                                Shop.buyFifty(itemId);
                                getContext().getPurchasedItems().put(itemId, getContext().getPurchasedItems().getOrDefault(itemId, 0) + 50);
                            });
                            break;
                        case TEN:
                            loopActions(() -> {
                                Shop.buyTen(itemId);
                                getContext().getPurchasedItems().put(itemId, getContext().getPurchasedItems().getOrDefault(itemId, 0) + 10);
                            });
                            break;
                        case FIVE:
                            loopActions(() -> {
                                Shop.buyFive(itemId);
                                getContext().getPurchasedItems().put(itemId, getContext().getPurchasedItems().getOrDefault(itemId, 0) + 5);
                            });
                            break;
                        case ONE:
                            loopActions(() -> {
                                Shop.buyOne(itemId);
                                getContext().getPurchasedItems().put(itemId, getContext().getPurchasedItems().getOrDefault(itemId, 0) + 1);
                            });
                            break;
                    }
                }
            }

            return -1;
        }

        getContext().openShop();
        return -1;
    }

    private void loopActions(Runnable r) {
        for (int j = 0; j < getContext().getActionsPetTick(); j++) {
            r.run();
        }
    }


    private boolean stockCheck() {
        return getContext().getItemIds().stream()
                .allMatch(itemId -> {
                    int stock = Shop.getStock(itemId);
                    if (isSell()) {
                        var item = Inventory.getFirst(itemId);

                        if (item.isNoted()) {
                            stock = Shop.getStock(item.getNotedId());
                        }
                    }

                    return isSell() ? stock >= getContext().getMaxStock() :
                            stock == 0 || stock <= getContext().getMinStock();
                });
    }

    private boolean isSell() {
        return getContext().getConfig().shopperMode() == ShopperMode.SELL;
    }
}