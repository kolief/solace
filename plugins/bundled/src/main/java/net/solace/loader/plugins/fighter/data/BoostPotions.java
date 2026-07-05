package net.solace.loader.plugins.fighter.data;

import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;

@Getter
public enum BoostPotions {
    DIVINE_SUPER_COMBAT(
            "Divine super combat potion",
            Skill.ATTACK,
            ItemID._4DOSEDIVINECOMBAT,
            ItemID._3DOSEDIVINECOMBAT,
            ItemID._2DOSEDIVINECOMBAT,
            ItemID._1DOSEDIVINECOMBAT
    ),
    DIVINE_BASTION(
            "Divine bastion potion",
            Skill.RANGED,
            ItemID._4DOSEDIVINEBASTION,
            ItemID._3DOSEDIVINEBASTION,
            ItemID._2DOSEDIVINEBASTION,
            ItemID._1DOSEDIVINEBASTION
    ),
    DIVINE_RANGING("Divine ranging potion",
            Skill.RANGED,
            ItemID._4DOSEDIVINERANGE,
            ItemID._3DOSEDIVINERANGE,
            ItemID._2DOSEDIVINERANGE,
            ItemID._1DOSEDIVINERANGE
    ),
    SUPER_COMBAT_POTION(
            "Super combat potion",
            Skill.ATTACK,
            ItemID._4DOSE2COMBAT,
            ItemID._3DOSE2COMBAT,
            ItemID._2DOSE2COMBAT,
            ItemID._1DOSE2COMBAT
    ),
    COMBAT_POTION(
            "Combat potion",
            Skill.ATTACK,
            ItemID._4DOSECOMBAT,
            ItemID._3DOSECOMBAT,
            ItemID._2DOSECOMBAT,
            ItemID._1DOSECOMBAT
    ),
    BASTION("Bastion potion",
            Skill.RANGED,
            ItemID._4DOSEBASTION,
            ItemID._3DOSEBASTION,
            ItemID._2DOSEBASTION,
            ItemID._1DOSEBASTION
    ),
    RANGING("Ranging potion",
            Skill.RANGED,
            ItemID._4DOSERANGERSPOTION,
            ItemID._3DOSERANGERSPOTION,
            ItemID._2DOSERANGERSPOTION,
            ItemID._1DOSERANGERSPOTION
    ),
    SUPER_ATTACK("Super attack",
            Skill.ATTACK,
            ItemID._4DOSE2ATTACK,
            ItemID._3DOSE2ATTACK,
            ItemID._2DOSE2ATTACK,
            ItemID._1DOSE2ATTACK
    ),
    SUPER_DEFENCE("Super defence",
            Skill.DEFENCE,
            ItemID._4DOSE2DEFENSE,
            ItemID._3DOSE2DEFENSE,
            ItemID._2DOSE2DEFENSE,
            ItemID._1DOSE2DEFENSE
    ),
    SUPER_STRENGTH("Super strength",
            Skill.STRENGTH,
            ItemID._4DOSE2STRENGTH,
            ItemID._3DOSE2STRENGTH,
            ItemID._2DOSE2STRENGTH,
            ItemID._1DOSE2STRENGTH
    ),
    IMBUED_HEART("Imbued heart",
            Skill.MAGIC,
            ItemID.IMBUED_HEART),
    SATURATED_HEART("Saturated heart",
            Skill.MAGIC,
            ItemID.SATURATED_HEART);

    private final String type;
    private final Skill skill;
    private final int[] ids;

    BoostPotions(String type, Skill skill, int... ids) {
        this.type = type;
        this.skill = skill;
        this.ids = ids;
    }

    @Override
    public String toString() {
        return type;
    }
}