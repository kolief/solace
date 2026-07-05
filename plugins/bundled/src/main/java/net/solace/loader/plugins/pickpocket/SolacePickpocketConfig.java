package net.solace.loader.plugins.pickpocket;


import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

@ConfigGroup("unethicalpickpocket")
public interface SolacePickpocketConfig extends Config {
    @ConfigItem(
            keyName = "bank",
            name = "Bank for food",
            description = "",
            position = 0
    )
    default boolean bank() {
        return true;
    }

    @ConfigItem(
            keyName = "bankStyle",
            name = "Banking style",
            description = "",
            position = 1,
            hidden = true,
            unhide = "bank"
    )
    default BankStyle bankStyle() {
        return BankStyle.DEPOSIT_ALL;
    }

    @ConfigItem(
            keyName = "bankLocation",
            name = "Bank Location",
            description = "",
            position = 2,
            hidden = true,
            unhide = "bank"
    )
    default BankLocation bankLocation() {
        return BankLocation.ARDOUGNE_SOUTH_BANK;
    }

    @ConfigItem(
            keyName = "npcName",
            name = "Npc name",
            description = "",
            position = 3
    )
    default String npcName() {
        return "Knight of Ardougne";
    }

    @ConfigItem(
            keyName = "eat",
            name = "Eat",
            description = "",
            position = 4
    )
    default boolean eat() {
        return true;
    }

    @ConfigItem(
            keyName = "foodName",
            name = "Food name",
            description = "",
            position = 5,
            hidden = true,
            unhide = "eat"
    )
    default String foodName() {
        return "Jug of wine";
    }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Food withdraw amount",
            description = "",
            position = 6,
            hidden = true,
            unhide = "eat"
    )
    default int foodAmount() {
        return 10;
    }

    @ConfigItem(
            keyName = "eatHp",
            name = "Eat at X missing HP",
            description = "",
            position = 7,
            hidden = true,
            unhide = "eat"
    )
    default int eatHp() {
        return 11;
    }

    @ConfigItem(
            keyName = "dodgy",
            name = "Should equip Dodgy Necklace?",
            description = "Enable the use of Dodgy Necklace's",
            position = 8
    )
    default boolean shouldEquipDodgy() {
        return false;
    }

    @ConfigItem(
            keyName = "dodgyQuantity",
            name = "Quantity to withdraw",
            description = "",
            position = 9,
            hidden = true,
            unhide = "dodgy"
    )
    default int necklaceQuantity() {
        return 3;
    }

    @ConfigItem(
            keyName = "junk",
            name = "Items to drop",
            description = "",
            position = 10
    )
    default String junk() {
        return "Jug, Potato seed";
    }
}
