package net.solace.loader.plugins.fighter.data;

import net.runelite.api.gameval.ItemID;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.sdk.entities.TileItems;
import net.solace.sdk.items.Inventory;

import java.util.Arrays;
import java.util.List;

public enum Antivenom {
    ANTI_VENOM(
            "Anti-venom",
            ItemID.ANTIVENOM4,
            ItemID.ANTIVENOM3,
            ItemID.ANTIVENOM2,
            ItemID.ANTIVENOM1
    ),
    ANTI_VENOM_PLUS(
            "Anti-venom+",
            ItemID.ANTIVENOM_4,
            ItemID.ANTIVENOM_3,
            ItemID.ANTIVENOM_2,
            ItemID.ANTIVENOM_1
    ),
    ANTIPOISON(
            "Antipoison",
            ItemID._4DOSEANTIPOISON,
            ItemID._3DOSEANTIPOISON,
            ItemID._2DOSEANTIPOISON,
            ItemID._1DOSEANTIPOISON
    ),
    SUPER_ANTIPOISON(
            "Superantipoison",
            ItemID._4DOSE2ANTIPOISON,
            ItemID._3DOSE2ANTIPOISON,
            ItemID._2DOSE2ANTIPOISON,
            ItemID._1DOSE2ANTIPOISON
    ),
    ANTIDOTE_PLUS_PLUS(
            "Antidote++",
            ItemID.ANTIDOTE__4,
            ItemID.ANTIDOTE__3,
            ItemID.ANTIDOTE__2,
            ItemID.ANTIDOTE__1
    ),
    SANFEW(
            "Sanfew serum",
            ItemID.SANFEW_SALVE_4_DOSE,
            ItemID.SANFEW_SALVE_3_DOSE,
            ItemID.SANFEW_SALVE_2_DOSE,
            ItemID.SANFEW_SALVE_1_DOSE
    ),
    EXTENDED_ANTI_VENOM(
            "Extended anti-venom",
            ItemID.EXTENDED_ANTIVENOM_4,
            ItemID.EXTENDED_ANTIVENOM_3,
            ItemID.EXTENDED_ANTIVENOM_2,
            ItemID.EXTENDED_ANTIVENOM_1
    ),
    ARAXYTE_VENOM_SACK(
            "Araxyte venom sack",
            ItemID.ARAXYTE_VENOM_SACK
    ),
    NONE("None", -1);

    private final String type;
    private final int[] ids;

    Antivenom(String type, int... ids) {
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

    public static List<IInventoryItem> getAntiVenoms() {
        var allIds = Arrays.stream(Antivenom.values())
                .map(Antivenom::getIds)
                .flatMapToInt(Arrays::stream)
                .toArray();

        return Inventory.getAll(allIds);
    }

    public static IItem getAntiVenom() {
        var antivenoms = getAntiVenoms();
        if (antivenoms.isEmpty()) {
            return null;
        }
        return antivenoms.get(0);
    }

    public static List<ITileItem> getAntiVenomDrops() {
        var allIds = Arrays.stream(Antivenom.values())
                .map(Antivenom::getIds)
                .flatMapToInt(Arrays::stream)
                .toArray();

        return TileItems.getAllMine(x -> Arrays.stream(allIds).anyMatch(y -> y == x.getId()));
    }
}