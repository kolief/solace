package net.solace.loader.plugins.wintertodt;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.Range;

@ConfigGroup("solacewintertodt")
public interface SolaceWintertodt extends Config {
    @ConfigItem(
            keyName = "food",
            name = "Food",
            description = "Food to use",
            position = 0
    )
    default String food() {
        return "Cake";
    }

    @ConfigItem(
            keyName = "minFoodAmount",
            name = "Min. food amount",
            description = "Min. amount of food before banking",
            position = 1
    )
    default int minFoodAmount() {
        return 2;
    }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Food withdraw amount",
            description = "Amount of food to use",
            position = 2
    )
    default int foodAmount() {
        return 4;
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "eatHp",
            name = "Eat HP %",
            description = "Eat at HP %",
            position = 3
    )
    default int eatHp() {
        return 25;
    }

    @ConfigItem(
            keyName = "axe",
            name = "Axe name",
            description = "Axe to use",
            position = 4
    )
    default String axe() {
        return "Steel axe";
    }

    @ConfigItem(
            keyName = "fletch",
            name = "Fletch",
            description = "Fletch into kindlings",
            position = 5
    )
    default boolean fletch() {
        return true;
    }

    @ConfigItem(
            keyName = "repair",
            name = "Repair brazier",
            description = "Repair broken brazier",
            position = 6
    )
    default boolean repair() {
        return true;
    }

    @Range(max = 28)
    @ConfigItem(
            keyName = "minInvSpace",
            name = "Min. inventory space",
            description = "Min. inv space before the plugin should bank",
            position = 7
    )
    default int minInvSpace() {
        return 10;
    }
}
