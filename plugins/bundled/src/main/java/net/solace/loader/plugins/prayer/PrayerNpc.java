package net.solace.loader.plugins.prayer;

import lombok.Getter;
import net.runelite.api.gameval.NpcID;
import net.solace.api.prayer.PrayerInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum PrayerNpc {
    FIRE_GIANT(
            new Attack(PrayerInfo.PROTECT_FROM_MELEE, 4667, 5, NpcID.FIREGIANT, NpcID.FIREGIANT2, NpcID.FIREGIANT3, NpcID.FIREGIANT_STRONGHOLDCAVE_1, NpcID.FIREGIANT_STRONGHOLDCAVE_2, NpcID.KOUREND_FIREGIANT2, NpcID.KOUREND_FIREGIANT1),
            new Attack(PrayerInfo.PROTECT_FROM_MELEE, 4666, 5, NpcID.FIREGIANT_STRONGHOLDCAVE_3)
    ),

    HELLHOUND(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 6562, 4, NpcID.HELLHOUND, NpcID.HELLHOUND_STRONGHOLDCAVE)),

    ABERRANT_SPECTRE(
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 1507, 4, NpcID.SLAYER_ABBERANT_SPECTRE_1, NpcID.SLAYER_ABBERANT_SPECTRE_2, NpcID.SLAYER_ABBERANT_SPECTRE_3, NpcID.SLAYER_ABBERANT_SPECTRE_4, NpcID.SLAYER_ABERRANTSPECTRE_1_STRONGHOLDCAVE, NpcID.SLAYER_ABERRANTSPECTRE_2_STRONGHOLDCAVE),
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 7550, 4, NpcID.SUPERIOR_ABBERANT_SPECTRE)
    ),

    TZTOK_JAD(
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 2656, 8, 10L, NpcID.TZHAAR_FIGHTCAVE_SWARM_BOSS),
            new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 2652, 8, 10L, NpcID.TZHAAR_FIGHTCAVE_SWARM_BOSS)
    ),

    JALTOK_JAD(
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 7592, 8, 10L, NpcID.INFERNO_JAD, NpcID.INFERNO_JAD_FINALWAVE, NpcID.JAD_CHALLENGE_JAD),
            new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 7593, 8, 10L, NpcID.INFERNO_JAD, NpcID.INFERNO_JAD_FINALWAVE, NpcID.JAD_CHALLENGE_JAD)
    ),

    SUQAH(
            new Attack(PrayerInfo.PROTECT_FROM_MELEE, 4388, 6, NpcID.LUNAR_SUQKA5),
            new Attack(PrayerInfo.PROTECT_FROM_MELEE, 4387, 6, NpcID.LUNAR_SUQKA6)
    ),

    BLOODVELD(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 1552, 4, NpcID.KOUREND_BLOODVELD, NpcID.SLAYER_BLOODVELD, NpcID.SLAYER_BLOODVELD_BABY, NpcID.SLAYER_BLOODVELD_STRONGHOLDCAVE, NpcID.SLAYER_BLOODVELD_BABY_STRONGHOLDCAVE, NpcID.GODWARS_BLOODVELD)),

    BLACK_DEMON(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 64, 4, MonsterID.BLACK_DEMONS)),

    GRTR_DEMON(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 64, 4, MonsterID.GREATER_DEMONS)),

    ANKOU(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 422, 4, NpcID.SOS_DEATH_ANKOU_STRONGHOLDCAVE, NpcID.SOS_DEATH_ANKOU_STRONGHOLDCAVE_2, NpcID.SOS_DEATH_ANKOU_STRONGHOLDCAVE_3)),

    SPIRITUAL_RANGER(new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 426, 4, NpcID.SOS_DEATH_ANKOU_STRONGHOLDCAVE, NpcID.SOS_DEATH_ANKOU_STRONGHOLDCAVE_2, NpcID.GODWARS_SPIRITUAL_ZAMORAK_RANGER)),

    KET_ZEK(new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 2647, 4, 9L, NpcID.TZHAAR_FIGHTCAVE_SWARM_5A, NpcID.TZHAAR_FIGHTCAVE_SWARM_5B)),

    TOK_XIL(new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 2633, 4, 8L, NpcID.POH_TOK_XIL, NpcID.TZHAAR_FIGHTPIT_SWARM_3A, NpcID.TZHAAR_FIGHTPIT_SWARM_3B, NpcID.TZHAAR_FIGHTCAVE_SWARM_3A, NpcID.TZHAAR_FIGHTCAVE_SWARM_3B)),

    YT_MEJKOT(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 2637, 4, 7L, NpcID.TZHAAR_FIGHTCAVE_SWARM_4A, NpcID.TZHAAR_FIGHTCAVE_SWARM_4B)),

    WYRM(
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 8271, 4, NpcID.WYRM_DARK, NpcID.WYRM_LIGHT),
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 8271, 4, NpcID.SUPERIOR_WYRM_DARK, NpcID.SUPERIOR_WYRM_LIGHT)
    ),

    DUST_DEVIL(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 1557, 4, NpcID.SLAYER_DUSTDEVIL, NpcID.KOUREND_DUSTDEVIL, NpcID.WILD_CAVE_DUSTDEVIL)),

    CAVE_HORROR(
            new Attack(PrayerInfo.PROTECT_FROM_MELEE, List.of(4234, 4235, 4237), 4, NpcID.HARMLESS_ISLAND_CAVE_HORROR_WORKER, NpcID.HARMLESS_ISLAND_CAVE_HORROR_YOUNG_WORKER, NpcID.HARMLESS_ISLAND_CAVE_HORROR_GUARD, NpcID.HARMLESS_ISLAND_CAVE_HORROR_SMALL_GUARD, NpcID.HARMLESS_ISLAND_CAVE_HORROR_ALPHA)
    ),

    MTN_TROLL(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 284, 6, NpcID.DEATH_TROLL_MELEE1, NpcID.DEATH_TROLL_MELEE2, NpcID.DEATH_TROLL_MELEE3, NpcID.DEATH_TROLL_MELEE4, NpcID.DEATH_TROLL_MELEE5, NpcID.DEATH_TROLL_MELEE6, NpcID.DEATH_TROLL_MELEE7, NpcID.TROLL_MELEE1)),

    ZEAH_DAGANNOTH(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 1341, 4, NpcID.KOUREND_DAGANNOTH1, NpcID.KOUREND_DAGANNOTH2)),

    ELF(
            new Attack(PrayerInfo.PROTECT_FROM_MELEE, 428, 4, NpcID.REGICIDE_DARKELF2),
            new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 426, 4, NpcID.REGICIDE_DARKELF)
    ),

    TZHAAR_KET(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 2610, 4, NpcID.TZHAAR_KET_STRONG)),

    DRAGONS(new Attack(PrayerInfo.PROTECT_FROM_MELEE, List.of(80, 81, 91), 4, MonsterID.DRAGONS)),

    ARMA_MAGER(new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 6955, 5, NpcID.GODWARS_ARMADYL_BODYGUARD_SKREE)),

    ARMA_RANGER(new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 6956, 5, NpcID.GODWARS_ARMADYL_BODYGUARD_GEERIN)),

    ARMA_MELEE(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 6957, 5, NpcID.GODWARS_ARMADYL_BODYGUARD_KILISA)),

    KREE_ARRA(new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 6978, 3, 10L, NpcID.GODWARS_ARMADYL_AVATAR)),

    SARA_RANGER(new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 7026, 5, NpcID.GODWARS_SARADOMIN_CENTAUR)),

    SARA_MAGER(new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 7037, 5, NpcID.GODWARS_SARADOMIN_LION)),

    SARA_MELEE(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 6376, 5, NpcID.GODWARS_SARADOMIN_UNICORN)),

    ZILYANA(
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 6967, 2, 10L, NpcID.GODWARS_SARADOMIN_AVATAR),
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 6964, 2, 10L, NpcID.GODWARS_SARADOMIN_AVATAR),
            new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 6970, 2, 10L, NpcID.GODWARS_SARADOMIN_AVATAR)
    ),

    DKS_REX(new Attack(PrayerInfo.PROTECT_FROM_MELEE, 2853, 4, NpcID.DAGCAVE_MELEE_BOSS)),
    DKS_PRIME(new Attack(PrayerInfo.PROTECT_FROM_MAGIC, 2854, 4, NpcID.DAGCAVE_MAGIC_BOSS)),
    DKS_SUPREME(new Attack(PrayerInfo.PROTECT_FROM_MISSILES, 2855, 4, NpcID.DAGCAVE_RANGED_BOSS)),
    ;

    static final List<Integer> JAD_ATTACKS = List.of(7592, 7593, 2656, 2652);

    private final Attack[] attacks;

    PrayerNpc(Attack... attacks) {
        this.attacks = attacks;
    }

    public boolean isJad() {
        return Arrays.stream(attacks).anyMatch(Attack::isJad);
    }


    @Getter
    public static final class Attack {
        private final PrayerInfo protectionPrayer;
        private final List<Integer> animations;
        private final int speed;
        private final int[] npcIds;
        private final long prio;

        public Attack(PrayerInfo protectionPrayer, int animationId, int speed, int... npcIds) {
            this(protectionPrayer, animationId, speed, 0, npcIds);
        }

        public Attack(PrayerInfo protectionPrayer, int animationId, int speed, long prio, int... npcIds) {
            this(protectionPrayer, Collections.singletonList(animationId), speed, prio, npcIds);
        }

        public Attack(PrayerInfo protectionPrayer, List<Integer> animations, int speed, int... npcIds) {
            this(protectionPrayer, animations, speed, 0, npcIds);
        }

        public Attack(PrayerInfo protectionPrayer, List<Integer> animations, int speed, long prio, int... npcIds) {
            this.protectionPrayer = protectionPrayer;
            this.animations = animations;
            this.speed = speed;
            this.npcIds = npcIds;
            this.prio = prio;
        }

        public boolean isJad() {
            return JAD_ATTACKS.stream().anyMatch(animations::contains);
        }

        public boolean isInfernoJad() {
            return animations.contains(7592) || animations.contains(7593);
        }
    }
}
