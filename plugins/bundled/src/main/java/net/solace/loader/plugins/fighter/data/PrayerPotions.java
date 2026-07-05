package net.solace.loader.plugins.fighter.data;

import net.runelite.api.World;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.Vars;
import net.solace.sdk.game.Worlds;
import net.solace.sdk.items.Inventory;

import java.util.Arrays;
import java.util.List;

public enum PrayerPotions {
    NONE("None", -1),
    PRAYER_POTION(
            "Prayer potion",
            ItemID._4DOSEPRAYERRESTORE,
            ItemID._3DOSEPRAYERRESTORE,
            ItemID._2DOSEPRAYERRESTORE,
            ItemID._1DOSEPRAYERRESTORE
    ),
    MOONLIGHT_MOTH_MIX(
            "Moonlight moth mix",
            ItemID.HUNTER_MIX_MOONMOTH_2DOSE,
            ItemID.HUNTER_MIX_MOONMOTH_1DOSE
    ),
    MOONLIGHT_MOTH(
            "Moonlight moth",
            ItemID.BUTTERFLY_JAR_MOONMOTH
    ),
    SUPER_RESTORE(
            "Super restore",
            ItemID._4DOSE2RESTORE,
            ItemID._3DOSE2RESTORE,
            ItemID._2DOSE2RESTORE,
            ItemID._1DOSE2RESTORE
    ),
    BLIGHTED_SUPER_RESTORE(
            "Blighted super restore",
            ItemID.BLIGHTED_4DOSE2RESTORE,
            ItemID.BLIGHTED_3DOSE2RESTORE,
            ItemID.BLIGHTED_2DOSE2RESTORE,
            ItemID.BLIGHTED_1DOSE2RESTORE
    ),
    SANFEW_SERUM(
            "Sanfew serum",
            ItemID.SANFEW_SALVE_4_DOSE,
            ItemID.SANFEW_SALVE_3_DOSE,
            ItemID.SANFEW_SALVE_2_DOSE,
            ItemID.SANFEW_SALVE_1_DOSE
    ),
    PRAYER_REGENERATION(
            "Prayer regeneration potion",
            ItemID._4DOSE1PRAYER_REGENERATION,
            ItemID._3DOSE1PRAYER_REGENERATION,
            ItemID._2DOSE1PRAYER_REGENERATION,
            ItemID._1DOSE1PRAYER_REGENERATION
    );

    private final String type;
    private final int[] ids;

    PrayerPotions(String type, int... ids) {
        this.type = type;
        this.ids = ids;
    }

    public String getType() {
        return type;
    }

    public int[] getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return type;
    }

    public boolean isPrayerRegen() {
        return this == PRAYER_REGENERATION;
    }

    public boolean canUse() {
        if (this == BLIGHTED_SUPER_RESTORE) {
            World world = Worlds.getCurrent();
            return Game.isInWilderness() || world != null && Worlds.isAllPkWorld(world);
        }

        return true;
    }

    public static boolean isRegenActive() {
        return Vars.getBit(VarbitID.PRAYER_REGENERATION_POTION_TIMER) > 0;
    }

    public static List<IInventoryItem> getPrayerRestores() {
        var allIds = Arrays.stream(PrayerPotions.values())
                .filter(x -> !x.isPrayerRegen() && x.canUse())
                .map(PrayerPotions::getIds)
                .flatMapToInt(Arrays::stream)
                .toArray();

        return Inventory.getAll(allIds);
    }

    public static List<IInventoryItem> getPrayerRegenPotions() {
        var allIds = Arrays.stream(PrayerPotions.values())
                .filter(PrayerPotions::isPrayerRegen)
                .map(PrayerPotions::getIds)
                .flatMapToInt(Arrays::stream)
                .toArray();

        return Inventory.getAll(allIds);
    }

    public static IInventoryItem getPrayerRestore() {
        if (getPrayerRestores().isEmpty()) {
            return null;
        }

        return getPrayerRestores().get(0);
    }
}
