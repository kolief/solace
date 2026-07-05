package net.solace.loader.plugins.chartershipper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.ItemID;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum GlassBlowItem {
    BEER_GLASS(ItemID.BEER_GLASS, 1),
    CANDLE_LANTERN(ItemID.CANDLE_LANTERN_UNLIT, 2),
    OIL_LAMP(ItemID.OIL_LAMP_UNLIT, 3),
    VIAL(ItemID.VIAL_EMPTY, 4),
    FISHBOWL(ItemID.FISHBOWL_WATER, 5),
    UNPOWERED_ORB(ItemID.STAFFORB, 6),
    LANTERN_LENS(ItemID.BULLSEYE_LANTERN_LENS, 7),
    LIGHT_ORB(ItemID.DORGESH_LIGHT_BULB, 8),
    ;

    private final int itemId;
    private final int menuIndex;
}
