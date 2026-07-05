package net.solace.loader.plugins.cooker;

import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.solace.sdk.game.Skills;

@Getter
public enum Meat {
    ALL(-1, -1, 0, 1),
    MEAT(ItemID.RAW_BEEF, ItemID.COOKED_MEAT, 4, 1),
    SHRIMPS(ItemID.RAW_SHRIMP, ItemID.SHRIMP, 4, 1),
    CHICKEN(ItemID.RAW_CHICKEN, ItemID.COOKED_CHICKEN, 4, 1),
    RABBIT(ItemID.RAW_RABBIT, ItemID.HUNTGUIDE_RABBIT, 4, 1),
    ANCHOVIES(ItemID.RAW_ANCHOVIES, ItemID.ANCHOVIES, 4, 1),
    SARDINE(ItemID.RAW_SARDINE, ItemID.SARDINE, 4, 1),
    HERRING(ItemID.RAW_HERRING, ItemID.HERRING, 4, 5),
    MACKEREL(ItemID.RAW_MACKEREL, ItemID.MACKEREL, 4, 10),
    TROUT(ItemID.RAW_TROUT, ItemID.TROUT, 4, 15),
    COD(ItemID.RAW_COD, ItemID.COD, 4, 18),
    PIKE(ItemID.RAW_PIKE, ItemID.PIKE, 4, 20),
    SALMON(ItemID.RAW_SALMON, ItemID.SALMON, 4, 25),
    TUNA(ItemID.RAW_TUNA, ItemID.TUNA, 4, 30),
    LOBSTER(ItemID.RAW_LOBSTER, ItemID.LOBSTER, 4, 40),
    BASS(ItemID.RAW_BASS, ItemID.BASS, 4, 43),
    SWORDFISH(ItemID.RAW_SWORDFISH, ItemID.SWORDFISH, 4, 45),
    MONKFISH(ItemID.RAW_MONKFISH, ItemID.MONKFISH, 4, 62),
    POISON_KARAMBWAN(ItemID.TBWT_RAW_KARAMBWAN, ItemID.TBWT_POORLY_COOKED_KARAMBWAN, 4, 1, 1),
    KARAMBWAN(ItemID.TBWT_RAW_KARAMBWAN, ItemID.TBWT_COOKED_KARAMBWAN, 4, 30, 0),
    SHARK(ItemID.RAW_SHARK, ItemID.SHARK, 4, 80),
    SEA_TURTLE(ItemID.RAW_SEATURTLE, ItemID.SEATURTLE, 4, 82),
    ANGLERFISH(ItemID.RAW_ANGLERFISH, ItemID.ANGLERFISH, 4, 84),
    DARK_CRAB(ItemID.RAW_DARK_CRAB, ItemID.DARK_CRAB, 4, 90),
    MANTA_RAY(ItemID.RAW_MANTARAY, ItemID.MANTARAY, 4, 91),
    SEAWEED(ItemID.SEAWEED, ItemID.SODA_ASH, 4, 1),
    GIANT_SEAWEED(ItemID.GIANT_SEAWEED, ItemID.SODA_ASH, 4, 1, 0, 4),
    ;

    private final int rawId;
    private final int cookedId;
    private final int cookTicks;
    private final int cookingLevel;
    private final int productionIndex;
    private final int withdrawAmount;

    Meat(int rawId, int cookedId, int cookTicks, int cookingLevel) {
        this(rawId, cookedId, cookTicks, cookingLevel, 0, 28);
    }

    Meat(int rawId, int cookedId, int cookTicks, int cookingLevel, int productionIndex) {
        this(rawId, cookedId, cookTicks, cookingLevel, productionIndex, 28);
    }

    Meat(int rawId, int cookedId, int cookTicks, int cookingLevel, int productionIndex, int withdrawAmount) {
        this.rawId = rawId;
        this.cookedId = cookedId;
        this.cookTicks = cookTicks;
        this.cookingLevel = cookingLevel;
        this.productionIndex = productionIndex;
        this.withdrawAmount = withdrawAmount;
    }

    public boolean canCook() {
        return Skills.getLevel(Skill.COOKING) >= cookingLevel;
    }
}