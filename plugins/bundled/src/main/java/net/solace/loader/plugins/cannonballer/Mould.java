package net.solace.loader.plugins.cannonballer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.ItemID;

@RequiredArgsConstructor
public enum Mould {
    NORMAL_MOULD(ItemID.AMMO_MOULD),
    DOUBLE_MOULD(ItemID.DOUBLE_AMMO_MOULD),
    ;

    @Getter
    private final int itemId;
}
