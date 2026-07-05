package net.solace.loader.plugins.shops;

import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigTitle;
import net.solace.api.plugins.config.Range;

@ConfigGroup("solaceshops")
public interface SolaceShopsConfig extends Config {
    @ConfigTitle(
            name = "Instructions",
            description = "",
            position = 0
    )
    String instructions = "Instructions";

    @ConfigTitle(
            name = "1. Start the plugin",
            description = "",
            position = 1,
            title = instructions
    )
    String startPlugin = "1. Start the plugin";

    @ConfigTitle(
            name = "2. Right click an NPC/Object",
            description = "",
            position = 2,
            title = instructions
    )
    String rightClick = "2. Right click an NPC/Object";

    @ConfigTitle(
            name = "3. Select a Shop",
            description = "",
            position = 3,
            title = instructions
    )
    String shop = "3. Select a Shop";

    @ConfigTitle(
            name = "4. Plugin will now start",
            description = "",
            position = 3,
            title = instructions
    )
    String start = "4. Plugin will now start";

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "actionsPerTick",
            name = "Actions per Tick",
            description = "",
            position = 5
    )
    default int actionsPerTick() {
        return 1;
    }

    @ConfigItem(
            keyName = "shopperMode",
            name = "Shopper Mode",
            description = "",
            position = 4
    )
    default ShopperMode shopperMode() {
        return ShopperMode.BUY;
    }

    @ConfigItem(
            keyName = "storeAmount",
            name = "Amount",
            description = "",
            position = 5
    )
    default Amount shopperAmount() {
        return Amount.FIFTY;
    }

    @ConfigItem(
            keyName = "ignoreStockWhenBuying",
            name = "Ignore stock when buying",
            description = "Ignore stock when buying",
            position = 6
    )
    default boolean ignoreStockWhenBuying() {
        return false;
    }

    @ConfigItem(
            keyName = "itemId",
            name = "Item IDs",
            description = "The Item IDs to buy",
            position = 10
    )
    default String itemId() {
        return "0";
    }

    @ConfigItem(
            keyName = "minGold",
            name = "Minimum gold",
            description = "The minimum amount of gold to withdraw",
            position = 11
    )
    default int minGold() {
        return 5000;
    }

    @ConfigItem(
            keyName = "maxGold",
            name = "Maximum gold",
            description = "The maximum amount of gold to withdraw",
            position = 12
    )
    default int maxGold() {
        return 100000;
    }

    @ConfigItem(
            keyName = "currencyType",
            name = "Currency",
            description = "Currency type to use",
            position = 13
    )
    default SolaceShopsPlugin.Currency currencyType() {
        return SolaceShopsPlugin.Currency.COINS;
    }

    @ConfigItem(
            keyName = "openPacks",
            name = "Open item packs",
            description = "Open item packs",
            position = 14
    )
    default boolean openPacks() {
        return true;
    }

    @ConfigItem(
            keyName = "minItemsInShop",
            name = "Min. stock in shop",
            description = "Minimum stock in shop before buying",
            position = 15
    )
    default int minItemsInShop() {
        return 0;
    }

    @ConfigItem(
            keyName = "maxItemsInShop",
            name = "Max. stock in shop",
            description = "Maximum stock in shop before hopping (seller)",
            position = 16
    )
    default int maxItemsInShop() {
        return 0;
    }

    @ConfigItem(
            keyName = "bankMode",
            name = "Bank Mode",
            description = "",
            position = 17
    )
    default BankMode bankMode() {
        return BankMode.BANK;
    }

    @ConfigItem(
            keyName = "bankLocation",
            name = "Bank Location",
            description = "Bank Location",
            position = 18,
            hidden = true,
            unhide = "bankMode",
            unhideValue = "BANK"
    )
    default BankLocation bankLocation() {
        return BankLocation.GRAND_EXCHANGE_BANK;
    }

    @ConfigItem(
            keyName = "maxBuy",
            name = "Max Buy",
            description = "Max. amount to buy",
            position = 20
    )
    default int maxBuy() {
        return 0;
    }

    @ConfigItem(
            keyName = "drinkStamina",
            name = "Drink Stamina",
            description = "Drink Stamina potions at bank",
            position = 21
    )
    default boolean drinkStamina() {
        return false;
    }
}