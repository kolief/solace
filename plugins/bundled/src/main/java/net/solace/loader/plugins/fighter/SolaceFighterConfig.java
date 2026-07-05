package net.solace.loader.plugins.fighter;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigSection;
import net.solace.api.plugins.config.Range;
import net.solace.loader.plugins.fighter.data.AlchSpell;
import net.solace.loader.plugins.fighter.data.AntifireType;
import net.solace.loader.plugins.fighter.data.BoostPotions;
import net.solace.loader.plugins.fighter.data.BuryType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigGroup("solacefighter")
public interface SolaceFighterConfig extends Config {
    @ConfigItem(
            keyName = "enabled",
            name = "Enabled",
            description = "Enable/Disable the plugin",
            position = 0
    )
    default boolean enabled() {
        return false;
    }

    @ConfigItem(
            keyName = "enabled",
            name = "Enabled",
            description = "Enable/Disable the plugin",
            position = 0
    )
    void enabled(boolean enabled);

    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 991,
            closedByDefault = true
    )
    String general = "General";
    @ConfigSection(
            name = "Health",
            description = "General settings",
            position = 992,
            closedByDefault = true
    )
    String health = "Health";

    @ConfigSection(
            name = "Boosts",
            description = "Boost settings",
            position = 993,
            closedByDefault = true
    )
    String boost = "Boosts";

    @ConfigSection(
            name = "Loot",
            description = "Loot settings",
            position = 994,
            closedByDefault = true
    )
    String loot = "Loot";

    @ConfigSection(
            name = "Prayers",
            description = "Prayers settings",
            position = 995,
            closedByDefault = true
    )
    String prayers = "Prayers";

    @ConfigSection(
            name = "Alching",
            description = "Alching settings",
            position = 996,
            closedByDefault = true
    )
    String alching = "Alching";

    @ConfigSection(
            name = "Slayer",
            description = "Slayer settings",
            position = 998,
            closedByDefault = true
    )
    String slayer = "Slayer";

    @ConfigSection(
            name = "Antifire",
            description = "Automatically uses antifire",
            position = 999,
            closedByDefault = true
    )
    String antifire = "Antifire";

    @ConfigSection(
            name = "Advanced",
            description = "Advanced settings",
            position = 1001,
            closedByDefault = true
    )
    String advanced = "Advanced";

    @ConfigSection(
            name = "Debug",
            description = "Debugging settings",
            position = 1002,
            closedByDefault = true
    )
    String debug = "Debug";

    @ConfigItem(
            keyName = "monster",
            name = "Monster",
            description = "Monster(s) to kill",
            position = 0,
            section = general
    )
    default String monster() {
        return "Chicken";
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "attackRange",
            name = "Attack range",
            description = "Monster attack range",
            position = 1,
            section = general
    )
    default int attackRange() {
        return 10;
    }

    @ConfigItem(
            keyName = "centerTile",
            name = "Center tile",
            description = "",
            position = 2,
            section = general
    )
    default String centerTile() {
        return "0 0 0";
    }

    @ConfigItem(
            keyName = "returnToCenter",
            name = "Return to Center?",
            description = "Control whether it should return to center if there are no monsters in the area.",
            position = 3,
            section = general
    )
    default boolean returnToCenter() {
        return true;
    }

    @ConfigItem(
            keyName = "bury",
            name = "Bury bones",
            description = "Bury bones",
            position = 4,
            section = general
    )
    default BuryType buryBones() {
        return BuryType.BURY;
    }

    @ConfigItem(
            keyName = "useSpec",
            name = "Enable Spec",
            description = "Enable/Disable spec usage",
            position = 5,
            section = general
    )
    default boolean useSpec() {
        return false;
    }

    @Range(min = 5, max = 100)
    @ConfigItem(
            keyName = "specAmount",
            name = "Required Spec",
            description = "Special attack energy required",
            position = 6,
            hidden = true,
            unhide = "useSpec",
            section = general
    )
    default int specAmount() {
        return 50;
    }

    @ConfigItem(
            keyName = "refillCannon",
            name = "Refill cannon",
            description = "Enables refilling your cannon",
            position = 7,
            section = general
    )
    default boolean refillCannon() {
        return false;
    }

    @ConfigItem(
            keyName = "prioritizeMonsterListedEarlier",
            name = "Prioritize Monsters listed earlier in list",
            description = "Prioritize Monster listed earlier in list - Put superior variant in list before the regular variant",
            position = 8,
            section = general
    )
    default boolean prioritizeMonsterListedEarlier() {
        return false;
    }

    @ConfigItem(
            keyName = "prioritizeAggroOutOfRange",
            name = "Prioritize Aggro Monsters out of range",
            description = "Prioritize Aggro Monsters out of range",
            position = 9,
            section = general
    )
    default boolean prioritizeAggroOutOfRange() {
        return false;
    }

    @ConfigItem(
            keyName = "useSoulBearer",
            name = "Use Soul bearer",
            description = "Picks up ensouled heads and uses it on your Soul bearer",
            position = 10,
            section = general
    )
    default boolean useSoulBearer() {
        return false;
    }


    @ConfigItem(
            keyName = "useHerbSack",
            name = "Use herb sack",
            description = "Picks up grimy herbs and puts them into your herb sack",
            position = 11,
            section = general
    )
    default boolean useHerbSack() {
        return false;
    }

    @ConfigItem(
            keyName = "enableSafespot",
            name = "Enable Safespot",
            description = "Enable/Disable safespotting",
            position = 12,
            section = general
    )
    default boolean enableSafespot() {
        return false;
    }

    @ConfigItem(
            keyName = "safespotTile",
            name = "Safespot tile",
            description = "",
            position = 13,
            section = general,
            hidden = true,
            unhide = "enableSafespot"
    )
    default String safespotTile() {
        return "0 0 0";
    }

    @ConfigItem(
            keyName = "gatherMobs",
            name = "Gather mobs",
            description = "Gather mobs (for bursting)",
            position = 14,
            section = general
    )
    default boolean gatherMobs() {
        return false;
    }

    @ConfigItem(
            keyName = "useGoading",
            name = "Use goading",
            description = "Use goading to lure monsters to you",
            position = 15,
            section = general
    )
    default boolean useGoading() {
        return false;
    }

    @ConfigItem(
            keyName = "safespotTile",
            name = "Safespot tile",
            description = "",
            position = 15,
            section = general,
            hidden = true,
            unhide = "enableSafespot"
    )
    void safespotTile(String safespotTile);

    @ConfigItem(
            keyName = "useCombatPotionsAtLevel",
            name = "Boost Potion Level",
            description = "Use Combat Potions when your boost falls below this level",
            position = 0,
            section = boost
    )
    default int useCombatPotionsAtLevel() {
        return 5;
    }

    @ConfigItem(
            keyName = "boostPotions",
            name = "Boosts",
            description = "Boost potions to drink",
            position = 1,
            section = boost
    )
    default Set<BoostPotions> boostPotions() {
        return Arrays.stream(BoostPotions.values()).collect(Collectors.toSet());
    }

    @ConfigItem(
            keyName = "looting",
            name = "Enable looting",
            description = "Enables looting",
            position = 0,
            section = loot
    )
    default boolean looting() {
        return true;
    }

    @ConfigItem(
            keyName = "onlyUseTelegrab",
            name = "Only use telegrab",
            description = "Only use telegrab to loot",
            position = 1,
            section = loot
    )
    default boolean onlyUseTelegrab() {
        return false;
    }

    @ConfigItem(
            keyName = "loots",
            name = "Loot Items",
            description = "Items to loot separated by comma. ex: Lobster,Tuna",
            position = 2,
            section = loot,
            hidden = true,
            unhide = "looting"
    )
    default String loots() {
        return "Bones";
    }

    @ConfigItem(
            keyName = "dontLoot",
            name = "Don't loot",
            description = "Items to not loot separated by comma. ex: Lobster,Tuna",
            position = 3,
            section = loot,
            hidden = true,
            unhide = "looting"
    )
    default String dontLoot() {
        return "Bones";
    }

    @ConfigItem(
            keyName = "lootByValue",
            name = "Loot items by value",
            description = "",
            position = 4,
            section = loot,
            hidden = true,
            unhide = "looting"
    )
    default boolean lootByValue() {
        return true;
    }

    @ConfigItem(
            keyName = "lootValue",
            name = "Loot GP value",
            description = "Min. value for item to loot",
            position = 5,
            section = loot,
            hidden = true,
            unhide = "lootByValue"
    )
    default int lootValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "stackableLootValue",
            name = "Loot stackables by value",
            description = "Min. value for stackable item to loot",
            position = 6,
            section = loot,
            hidden = true,
            unhide = "lootByValue"
    )
    default int stackableLootValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "untradables",
            name = "Loot untradables",
            description = "Loot untradables",
            position = 7,
            section = loot,
            hidden = true,
            unhide = "looting"
    )
    default boolean untradables() {
        return true;
    }

    @ConfigItem(
            keyName = "eat",
            name = "Eat food",
            description = "Eat food to heal",
            position = 0,
            section = health
    )
    default boolean eat() {
        return true;
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "minEatHealthPercent",
            name = "Min Health %",
            description = "Min Health % to eat at",
            position = 1,
            section = health
    )
    default int minHealthPercent() {
        return 65;
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "maxEatHealthPercent",
            name = "Max Health %",
            description = "Max Health % to eat at",
            position = 2,
            section = health
    )
    default int maxHealthPercent() {
        return 80;
    }

    @ConfigItem(
            keyName = "foods",
            name = "Food",
            description = "Food to eat, separated by comma. ex: Bones,Coins",
            position = 3,
            section = health
    )
    default String foods() {
        return "Any";
    }

    @ConfigItem(
            keyName = "disableWhenNoFood",
            name = "Disable when out of food",
            description = "Stops the plugin when out of food",
            position = 4,
            section = health
    )
    default boolean disableWhenNoFood() {
        return false;
    }


    @ConfigItem(
            keyName = "quickPrayer",
            name = "Use Quick Prayers",
            description = "Use Quick Prayers",
            position = 0,
            section = prayers
    )
    default boolean quickPrayer() {
        return false;
    }

    @ConfigItem(
            keyName = "flick",
            name = "Flick",
            description = "One ticks quick prayers",
            position = 1,
            section = prayers
    )
    default boolean flick() {
        return false;
    }

    @ConfigItem(
            keyName = "restore",
            name = "Restore prayer",
            description = "Drinks pots to restore prayer points",
            position = 2,
            section = prayers
    )
    default boolean restore() {
        return false;
    }

    @ConfigItem(
            keyName = "disableWhenNoPrayer",
            name = "Disable when out of prayer",
            description = "Stops the plugin when out of prayer",
            position = 3,
            section = prayers
    )
    default boolean disableWhenNoPrayer() {
        return false;
    }

    @ConfigItem(
            keyName = "prayAltar",
            name = "Pray at a nearby altar",
            description = "Pray at a nearby altar to recharge prayer",
            position = 4,
            section = prayers
    )
    default boolean prayAltar() {
        return false;
    }

    @ConfigItem(
            keyName = "prayAltar",
            name = "Pray at a nearby altar",
            description = "Pray at a nearby altar to recharge prayer",
            position = 4,
            section = prayers
    )
    void prayAltar(boolean prayAltar);

    @ConfigItem(
            keyName = "alch",
            name = "Alch items",
            description = "Alchs items",
            position = 0,
            section = alching
    )
    default boolean alching() {
        return false;
    }

    @ConfigItem(
            keyName = "alchSpell",
            name = "Alch spell",
            description = "Alch spell",
            position = 1,
            section = alching
    )
    default AlchSpell alchSpell() {
        return AlchSpell.HIGH;
    }

    @ConfigItem(
            keyName = "alchItems",
            name = "Alch items",
            description = "Items to alch, separated by comma. ex: Maple shortbow,Rune scimitar",
            position = 2,
            section = alching
    )
    default String alchItems() {
        return "Weed";
    }

    @ConfigItem(
            keyName = "disableOnTaskCompletion",
            name = "Disable after task",
            description = "Disables plugin once slayer task is finished, so you don't continue attacking monster",
            position = 0,
            section = slayer
    )
    default boolean disableAfterSlayerTask() {
        return false;
    }

    @ConfigItem(
            keyName = "autoEquipBracelets",
            name = "Auto equip bracelets",
            description = "Automatically re-equips slayer bracelets when they break. Equip a bracelet at startup for it to be detected.",
            position = 1,
            section = slayer
    )
    default boolean autoEquipBracelets() {
        return false;
    }

    @ConfigItem(
            keyName = "antifireType",
            name = "Antifire type",
            description = "Type of antifire potion to drink",
            position = 1,
            section = antifire
    )
    default AntifireType antifireType() {
        return AntifireType.ANTIFIRE;
    }

    @ConfigItem(
            keyName = "disableReachability",
            name = "Disable reachability checks",
            description = "Disables checking whether NPCs are reachable. Useful for safespotting through walls.",
            position = 0,
            section = advanced
    )
    default boolean disableReachability() {
        return false;
    }

    @ConfigItem(
            keyName = "drawRadius",
            name = "Draw attack area",
            description = "",
            position = 0,
            section = debug
    )
    default boolean drawRadius() {
        return false;
    }

    @ConfigItem(
            keyName = "drawCenter",
            name = "Draw center tile",
            description = "",
            position = 1,
            section = debug
    )
    default boolean drawCenter() {
        return false;
    }

    @ConfigItem(
            keyName = "debugMessages",
            name = "Debug messages",
            description = "",
            position = 2,
            section = debug
    )
    default boolean debugMessages() {
        return false;
    }

    @ConfigItem(
            keyName = "drawSafespot",
            name = "Draw safespot",
            description = "",
            position = 3,
            section = debug
    )
    default boolean drawSafespot() {
        return false;
    }
}

