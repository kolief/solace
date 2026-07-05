package net.solace.loader.plugins.arceuuslibrary;

import net.solace.api.plugins.DoNotRename;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.Range;
import net.solace.loader.plugins.arceuuslibrary.domain.BookReward;

@ConfigGroup("solacearceuuslibrary")
@DoNotRename
public interface SolaceArceuusLibraryConfig extends Config {
    @ConfigItem(
            keyName = "replenishStaminaPotions",
            name = "Get Stamina Potions",
            description = "Get stamina potions from the bank",
            position = 0
    )
    default boolean replenishStaminaPotions() {
        return false;
    }

    @ConfigItem(
            keyName = "replenishStaminaPotions",
            name = "Get Stamina Potions",
            description = "Get stamina potions from the bank",
            position = 0
    )
    void replenishStaminaPotions(boolean value);

    @ConfigItem(
            keyName = "staminaPotionQuantity",
            name = "Stamina Potion Amount",
            description = "Get X amount of stamina potions from the bank",
            position = 1,
            hidden = true,
            unhide = "replenishStaminaPotions"
    )
    default int staminaPotionQuantity() {
        return 4;
    }

    @ConfigItem(
            keyName = "logoutNoStamina",
            name = "Logout if no stamina",
            description = "Logout if you do not have any stamina in the bank",
            position = 2,
            hidden = true,
            unhide = "replenishStaminaPotions"
    )
    default boolean logoutNoStamina() {
        return false;
    }

    @ConfigItem(
            keyName = "collectMultipleBooks",
            name = "Collect multiple books",
            description = "Collect multiple books before returning them",
            position = 3
    )
    default boolean collectMultipleBooks() {
        return false;
    }

    @ConfigItem(
            keyName = "collectMultipleBooksAmount",
            name = "Amount of books to collect",
            description = "Amount of books to collect before returning them",
            hidden = true,
            unhide = "collectMultipleBooks",
            position = 4
    )
    @Range(
            min = 1,
            max = 15
    )
    default int collectMultipleBooksAmount() {
        return 10;
    }

    @ConfigItem(
            keyName = "bookReward",
            name = "Reward choice",
            description = "Magic or Runecrafting XP reward",
            position = 5
    )
    default BookReward bookReward() {
        return BookReward.RUNECRAFTING;
    }
}
