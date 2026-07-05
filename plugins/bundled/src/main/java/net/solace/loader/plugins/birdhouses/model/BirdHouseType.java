package net.solace.loader.plugins.birdhouses.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.ItemID;

import javax.annotation.Nullable;

@RequiredArgsConstructor
@Getter
public enum BirdHouseType {
    NORMAL("Bird House", ItemID.BIRDHOUSE_NORMAL, ItemID.LOGS),
    OAK("Oak Bird House", ItemID.BIRDHOUSE_OAK, ItemID.OAK_LOGS),
    WILLOW("Willow Bird House", ItemID.BIRDHOUSE_WILLOW, ItemID.WILLOW_LOGS),
    TEAK("Teak Bird House", ItemID.BIRDHOUSE_TEAK, ItemID.TEAK_LOGS),
    MAPLE("Maple Bird House", ItemID.BIRDHOUSE_MAPLE, ItemID.MAPLE_LOGS),
    MAHOGANY("Mahogany Bird House", ItemID.BIRDHOUSE_MAHOGANY, ItemID.MAHOGANY_LOGS),
    YEW("Yew Bird House", ItemID.BIRDHOUSE_YEW, ItemID.YEW_LOGS),
    MAGIC("Magic Bird House", ItemID.BIRDHOUSE_MAGIC, ItemID.MAGIC_LOGS),
    REDWOOD("Redwood Bird House", ItemID.BIRDHOUSE_REDWOOD, ItemID.REDWOOD_LOGS);

    private final String name;
    private final int itemId;
    private final int logItemId;

    @Nullable
    public static BirdHouseType fromVarpValue(int varp) {
        int index = (varp - 1) / 3;

        if (varp <= 0 || index >= values().length) {
            return null;
        }

        return values()[index];
    }
}