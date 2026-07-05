package net.solace.loader.plugins.agility;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ItemConfig;
import net.solace.api.plugins.config.Range;

@ConfigGroup("solaceagility")
public interface SolaceAgilityConfig extends Config {
    @ConfigItem(
            name = "Course",
            keyName = "course",
            description = "Course to complete"
    )
    default Course course() {
        return Course.NEAREST;
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "eatHp",
            name = "Eat HP %",
            description = "Eat food when at this HP or below. Will stop if runs out of food.",
            position = 8
    )
    default int eatHp() {
        return 75;
    }

    @ConfigItem(
            keyName = "useSummerPies",
            name = "Use Summer Pies",
            description = "Enable using Summer Pies to boost Agility",
            position = 20
    )
    default boolean useSummerPies() {
        return false;
    }


    @ConfigItem(
            keyName = "summerPieStyle",
            name = "Boost Style",
            description = "Target level, or target boost amount",
            position = 25,
            hidden = true,
            unhide = "useSummerPies"
    )
    default BoostStyle summerPieStyle() {
        return BoostStyle.BOOST_AMOUNT;
    }

    @Range(min = 1, max = 5)
    @ConfigItem(
            keyName = "minBoostAmount",
            name = "Min Boost Amount",
            description = "The minimum amount you want your Agility to be boosted",
            position = 30,
            hidden = true,
            unhide = "summerPieStyle",
            unhideValue = "Boost Amount"
    )
    default int minBoostAmount() {
        return 1;
    }

    @Range(min = 1, max = 99)
    @ConfigItem(
            keyName = "targetBoost",
            name = "Target level",
            description = "The level to boost if we drop below",
            position = 30,
            hidden = true,
            unhide = "summerPieStyle",
            unhideValue = "Target Level"
    )
    default int targetBoostLevel() {
        return 90;
    }

    @ConfigItem(
            keyName = "stopWhenOutOfSummerPies",
            name = "Stop when out of Summer Pies",
            description = "Enable this to prevent trying to do a course you don't have the Agility level for",
            position = 40,
            hidden = true,
            unhide = "useSummerPies"
    )
    default boolean stopWhenOutOfSummerPies() {
        return true;
    }

    @ConfigItem(
            keyName = "useStaminas",
            name = "Use Staminas",
            description = "Uses Stamina potions if there are any in your inventory",
            position = 50
    )
    default boolean useStaminas() {
        return true;
    }

    @ConfigItem(
            keyName = "minEnergyAmount",
            name = "Min",
            description = "Minimum energy to boost at",
            position = 60,
            hidden = true,
            unhide = "useStaminas"
    )
    default int minEnergyAmount() {
        return 20;
    }

    @ConfigItem(
            keyName = "maxEnergyAmount",
            name = "Max",
            description = "Maximum energy to boost at",
            position = 70,
            hidden = true,
            unhide = "useStaminas"
    )
    default int maxEnergyAmount() {
        return 40;
    }

    @ConfigItem(
            keyName = "minimumMarkCount",
            name = "Minimum Marks",
            description = "Minimum amount of Marks of Grace to stack before collecting",
            position = 75,
            hidden = true,
            unhide = "course",
            unhideValue = "Ardy course"
    )
    default int minimumMarkCount() {
        return 10;
    }

    @ConfigItem(
            keyName = "payWilderness",
            name = "Pay wilderness",
            description = "Pay the wilderness course fee",
            position = 80,
            hidden = true,
            unhide = "course",
            unhideValue = "Wildy course"
    )
    default boolean payWilderness() {
        return false;
    }

    @ConfigItem(
            keyName = "redeemWilderness",
            name = "Redeem tickets",
            description = "Redeems tickets",
            position = 81,
            hidden = true,
            unhide = "course",
            unhideValue = "Wildy course"
    )
    default boolean redeemTickets() {
        return false;
    }

    @ConfigItem(
            keyName = "redeemAmount",
            name = "Redeem at amount",
            description = "Redeems tickets when at this value",
            position = 82,
            hidden = true,
            unhide = "course",
            unhideValue = "Wildy course"
    )
    default int redeemAmount() {
        return 40;
    }

    @ConfigItem(
            keyName = "alchItem",
            name = "Item to Alch",
            description = "Item to alch."
    )
    default ItemConfig itemToAlch() {
        return null;
    }

    @ConfigItem(
            keyName = "alchSpell",
            name = "Alching",
            description = "If selected, plugin will alch the item set in Item to Alch"
    )
    default AlchSpell alchSpell() {
        return AlchSpell.OFF;
    }

    @ConfigItem(
            keyName = "stopIfNoFood",
            name = "Stop plugin if no food",
            description = "Stop if no food left, or idle until hp regens"
    )
    default boolean stopIfNoFood() {
        return true;
    }

    @ConfigItem(
            keyName = "stopAtLevel",
            name = "Stop at level",
            description = "Stop at this level"
    )
    default int stopAtLevel() {
        return 99;
    }

    @ConfigItem(
            keyName = "debug",
            name = "Debug",
            description = "Debug"
    )
    default boolean debug() {
        return false;
    }

}