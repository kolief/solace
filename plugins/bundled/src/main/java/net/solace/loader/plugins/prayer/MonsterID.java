package net.solace.loader.plugins.prayer;

import net.runelite.api.gameval.NpcID;

public interface MonsterID {
    int[] BLACK_DEMONS =
            {
                    NpcID.BLACK_DEMON2, NpcID.BLACK_DEMON, NpcID.BLACK_DEMON3, NpcID.BLACK_DEMON4,
                    NpcID.BLACK_DEMON5, NpcID.KOUREND_BLACK_DEMON_1, NpcID.KOUREND_BLACK_DEMON_2,
            };

    int[] GREATER_DEMONS =
            {
                    NpcID.GREATER_DEMON, NpcID.GREATER_DEMON2, NpcID.GREATER_DEMON3, NpcID.GREATER_DEMON4,
                    NpcID.GREATER_DEMON5, NpcID.GREATER_DEMON_STRONGHOLDCAVE_2, NpcID.GREATER_DEMON_STRONGHOLDCAVE_3,
                    NpcID.KOUREND_GREATER_DEMON1, NpcID.KOUREND_GREATER_DEMON2, NpcID.KOUREND_GREATER_DEMON3,
                    NpcID.WILD_CAVE_GREATER_DEMON, NpcID.WILD_CAVE_GREATER_DEMON2, NpcID.WILD_CAVE_GREATER_DEMON3
            };

    int[] DRAGONS =
            {
                    NpcID.BLACK_DRAGON, NpcID.BLACK_DRAGON2, NpcID.BLACK_DRAGON3, NpcID.BLACK_DRAGON4,
                    NpcID.BLACK_DRAGON5, NpcID.BLACK_DRAGON_STRONGHOLDCAVE_1, NpcID.BLACK_DRAGON_STRONGHOLDCAVE_2, NpcID.BLACK_DRAGON_STRONGHOLDCAVE_3,
            };
}
