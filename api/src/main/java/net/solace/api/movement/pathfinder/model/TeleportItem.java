package net.solace.api.movement.pathfinder.model;

import java.util.List;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.MovementConstants;
import net.solace.api.movement.pathfinder.model.requirement.Comparison;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarType;
import net.solace.api.util.EtcUtils;

public enum TeleportItem {
    WATERBIRTH_TELEPORT_TAB(new WorldPoint(2546, 3757, 0), "Break", 24953),
    KHAZARD_TELEPORT_TAB(new WorldPoint(2637, 3166, 0), "Break", 24957),
    VARROCK_TELEPORT_TAB(new WorldPoint(3212, 3424, 0), "Varrock", 8007),
    LUMBRIDGE_TELEPORT_TAB(new WorldPoint(3225, 3219, 0), "Break", 8008),
    FALADOR_TELEPORT_TAB(new WorldPoint(2966, 3379, 0), "Break", 8009),
    CAMELOT_TELEPORT_TAB(new WorldPoint(2757, 3479, 0), "Camelot", 8010),
    SEERS_TELEPORT_TAB(new WorldPoint(2727, 3485, 0), "Seers' Village", Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 4477, 1)), 8010),
    ARDOUGNE_TELEPORT_TAB(new WorldPoint(2661, 3300, 0), "Break", 8011),
    WEST_ARDOUGNE_TELEPORT_TAB(new WorldPoint(2500, 3290, 0), "Break", Quest.BIOHAZARD, 19623),
    RIMMINGTON_TELEPORT_TAB(new WorldPoint(2954, 3224, 0), "Break", 11741),
    TAVERLEY_TELEPORT_TAB(new WorldPoint(2894, 3465, 0), "Break", 11742),
    RELLEKKA_TELEPORT_TAB(new WorldPoint(2668, 3631, 0), "Break", 11744),
    BRIMHAVEN_TELEPORT_TAB(new WorldPoint(2758, 3178, 0), "Break", 11745),
    POLLNIVNEACH_TELEPORT_TAB(new WorldPoint(3340, 3004, 0), "Break", 11743),
    YANILLE_TELEPORT_TAB(new WorldPoint(2544, 3095, 0), "Break", 11746),
    HOSIDIUS_TELEPORT_TAB(new WorldPoint(1744, 3517, 0), "Break", 19651),
    SALVE_GRAVEYARD_TELEPORT_TAB(new WorldPoint(3432, 3460, 0), "Break", Quest.PRIEST_IN_PERIL, 19619),
    DRAYNOR_MANOR_TELEPORT_TAB(new WorldPoint(3109, 3350, 0), "Break", 19615),
    FENKENSTRAINS_CASTLE_TELEPORT_TAB(new WorldPoint(3549, 3530, 0), "Break", Quest.PRIEST_IN_PERIL, 19621),
    TROLLHEIM_TELEPORT_TAB(new WorldPoint(2890, 3679, 0), "Break", 11747),
    PRIFDDINAS_TELEPORT_TAB(new WorldPoint(3239, 6076, 0), "Break", 23771),
    MOONCLAN_TELEPORT_TAB(new WorldPoint(2110, 3915, 0), "Break", Quest.LUNAR_DIPLOMACY, 24949),
    OURANIA_TELEPORT_TAB(new WorldPoint(2470, 3247, 0), "Break", Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 5376, 1)), 24951),
    BARBARIAN_TELEPORT_TAB(new WorldPoint(2541, 3571, 0), "Break", 24955),
    FISHING_GUILD_TELEPORT_TAB(new WorldPoint(2611, 3390, 0), "Break", 24959),
    CATHERBY_TELEPORT_TAB(new WorldPoint(2802, 3449, 0), "Break", 24961),
    HOUSE_OUTSIDE_TELEPORT_TAB(null, "Outside", 8013),
    CIVITAS_TELEPORT_TAB(new WorldPoint(1679, 3132, 0), "Break", 28824),
    KOUREND_CASTLE_TELEPORT_TAB(new WorldPoint(1641, 3673, 0), "Break", Quest.CLIENT_OF_KOUREND, 28790),
    ICE_PLATEAU_TELEPORT_TAB(new WorldPoint(2973, 3939, 0), "Break", 24963),
    PADDEWWA_TELEPORT_TAB(new WorldPoint(3098, 9883, 0), "Break", Quest.DESERT_TREASURE_I, 12781),
    SENNTISTEN_TELEPORT_TAB(new WorldPoint(3321, 3335, 0), "Break", Quest.DESERT_TREASURE_I, 12782),
    KHARYRLL_TELEPORT_TAB(new WorldPoint(3493, 3474, 0), "Break", Quest.DESERT_TREASURE_I, 12779),
    LASSAR_TELEPORT_TAB(new WorldPoint(3004, 3468, 0), "Break", Quest.DESERT_TREASURE_I, 12780),
    DAREEYAK_TELEPORT_TAB(new WorldPoint(2969, 3695, 0), "Break", Quest.DESERT_TREASURE_I, 12777),
    CRAB_TELEPORT_TAB(new WorldPoint(3352, 3782, 0), "Break", 24251),
    CARRALLANGAR_TELEPORT_TAB(new WorldPoint(3157, 3667, 0), "Break", Quest.DESERT_TREASURE_I, 12776),
    ANNAKARL_TELEPORT_TAB(new WorldPoint(3288, 3888, 0), "Break", Quest.DESERT_TREASURE_I, 12775),
    GHORROCK_TELEPORT_TAB(new WorldPoint(2977, 3872, 0), "Break", Quest.DESERT_TREASURE_I, 12778),
    ARCEUUS_LIBRARY_TELEPORT_TAB(new WorldPoint(1634, 3836, 0), "Break", 19613),
    BATTLEFRONT_TELEPORT_TAB(new WorldPoint(1350, 3740, 0), "Break", 22949),
    MIND_ALTAR_TELEPORT_TAB(new WorldPoint(2978, 3508, 0), "Break", 19617),
    HARMONY_ISLAND_TELEPORT_TAB(new WorldPoint(3799, 2867, 0), "Break", Quest.THE_GREAT_BRAIN_ROBBERY, 19625),
    CEMETERY_TELEPORT_TAB(new WorldPoint(2978, 3763, 0), "Break", 19627),
    BARROWS_TELEPORT_TAB(new WorldPoint(3563, 3313, 0), "Break", Quest.PRIEST_IN_PERIL, 19629),
    APE_ATOLL_TELEPORT_TAB(new WorldPoint(2769, 9100, 0), "Break", Quest.MONKEY_MADNESS_I, 19631),
    WEISS_ICY_BASALT(new WorldPoint(2846, 3940, 0), "Weiss", Quest.MAKING_FRIENDS_WITH_MY_ARM, 22599),
    TROLL_STRONGHOLD(new WorldPoint(2838, 3693, 0), "Troll Stronghold", Quest.MAKING_FRIENDS_WITH_MY_ARM, 22601),
    TROLL_STRONGHOLD_OUTSIDE(new WorldPoint(2844, 3693, 0), "Troll Stronghold", Quest.MAKING_FRIENDS_WITH_MY_ARM, 22601),
    BLUE_RUM(new WorldPoint(3812, 3021, 0), "Drink", 8941),
    RED_RUM(new WorldPoint(3812, 3021, 0), "Drink", 8940),
    HALLOWED_CRYSTAL_SHARD(new WorldPoint(2378, 5996, 0), "Activate", Quest.SINS_OF_THE_FATHER, 24709),
    MOKHAIOTL_WAYSTONE(new WorldPoint(1312, 9496, 1), "Channel", 31099),
    ROYAL_SEED_POD(new WorldPoint(2465, 3495, 0), "Commune", Quest.MONKEY_MADNESS_II, 19564),
    ECTOPHIAL(new WorldPoint(3659, 3523, 0), "Empty", 4251),
    TELEPORT_CRYSTAL_LLETYA(new WorldPoint(2330, 3172, 0), "Lletya", MovementConstants.TELEPORT_CRYSTAL),
    TELEPORT_CRYSTAL_PRIFDDINAS(new WorldPoint(3264, 6065, 0), "Prifddinas", Quest.SONG_OF_THE_ELVES, MovementConstants.TELEPORT_CRYSTAL),
    CHAMPIONS_GUILD_CHRONICLE(new WorldPoint(3202, 3357, 0), "Teleport", "Teleport", 13660),
    RELLEKKKA_LYRE(new WorldPoint(2664, 3643, 0), "Rellekka", Quest.THE_FREMENNIK_TRIALS, MovementConstants.ENCHANTED_LYRE),
    WATERBIRTH_ISLAND_LYRE(new WorldPoint(2550, 3756, 0), "Waterbirth Island", Quest.THE_FREMENNIK_TRIALS, MovementConstants.ENCHANTED_LYRE),
    NEITIZNOT_LYRE(new WorldPoint(2336, 3801, 0), "Neitiznot", Quest.THE_FREMENNIK_TRIALS, MovementConstants.ENCHANTED_LYRE),
    JATIZSO_LYRE(new WorldPoint(2409, 3809, 0), "Jatizso", Quest.THE_FREMENNIK_TRIALS, MovementConstants.ENCHANTED_LYRE),
    MYTHICAL_CAPE(new WorldPoint(2457, 2849, 0), "Teleport", "Teleport", Quest.DRAGON_SLAYER_II, 22114),
    ARDOUGNE_CLOAK(new WorldPoint(2606, 3220, 0), "Monastery Teleport", "Kandarin Monastery", MovementConstants.ARDOUGNE_CLOAK),
    QUEST_POINT_CAPE(new WorldPoint(2730, 3347, 0), "Teleport", "Teleport", MovementConstants.QUEST_POINT_CAPE),
    EXPLORERS_RING(new WorldPoint(3053, 3291, 0), "Teleport", "Teleport", MovementConstants.EXPLORERS_RING),
    DESERT_AMULET_KALPHITE(new WorldPoint(3322, 3122, 0), "Kalphite cave", "Kalphite cave", 13136),
    DESERT_AMULET_NARDAH(new WorldPoint(3424, 2927, 0), "Nardah", "Nardah", 13136),
    KANDARIN_HEADGEAR(new WorldPoint(2736, 3418, 0), "Teleport", "Teleport", 13140),
    MORYTANIA_LEGS_BURGH(new WorldPoint(3480, 3228, 0), "Burgh Teleport", "Burgh de Rott", 13114, 13115),
    MORYTANIA_LEGS_ECTO(new WorldPoint(3683, 9888, 0), "Ecto Teleport", "Ectofuntus Pit", 13115),
    WESTERN_BANNER(new WorldPoint(2332, 3678, 0), "Teleport", "Teleport", 13144);

    private final WorldPoint destination;
    private final Requirements requirement;
    private final int[] itemIds;
    private final String action;
    private final String equippedAction;

    private TeleportItem(WorldPoint destination, String action, String equippedAction, Requirements requirement, int ... itemIds) {
        this.destination = destination;
        this.requirement = requirement;
        this.itemIds = itemIds;
        this.action = action;
        this.equippedAction = equippedAction;
    }

    private TeleportItem(WorldPoint destination, String action, String equippedAction, int ... itemIds) {
        this(destination, action, equippedAction, (Requirements)null, itemIds);
    }

    private TeleportItem(WorldPoint destination, String action, int ... itemIds) {
        this(destination, action, null, (Requirements)null, itemIds);
    }

    private TeleportItem(WorldPoint destination, String action, Requirements requirement, int ... itemIds) {
        this(destination, action, null, requirement, itemIds);
    }

    private TeleportItem(WorldPoint destination, String action, String equippedAction, Quest quest, int ... itemIds) {
        this(destination, action, equippedAction, Requirements.of(QuestRequirement.finished(quest)), itemIds);
    }

    private TeleportItem(WorldPoint destination, String action, Quest quest, int ... itemIds) {
        this(destination, action, null, quest, itemIds);
    }

    public WorldPoint getDestination() {
        if (this == HOUSE_OUTSIDE_TELEPORT_TAB) {
            return Static.getMovement().getNearestWalkableTile(Static.getHouse().getOutsideLocation(), Static.getGlobalCollisionMap(), x -> true);
        }
        return this.destination;
    }

    public boolean canUse(List<Integer> items) {
        if (this == HOUSE_OUTSIDE_TELEPORT_TAB && Static.getHouse().getOutsideLocation() == null) {
            return false;
        }
        return this.hasRequirements() && (Static.getInventory().contains(this.itemIds) || Static.getEquipment().contains(this.itemIds) || EtcUtils.containsItem(this.itemIds, items));
    }

    public boolean canUse() {
        return this.canUse(List.of());
    }

    public boolean hasRequirements() {
        boolean hasReqs = this.requirement == null || this.requirement.fulfilled();
        switch (this) {
            case JATIZSO_LYRE: 
            case NEITIZNOT_LYRE: {
                return hasReqs && Static.getVars().getBit(4494) > 0;
            }
            case WATERBIRTH_ISLAND_LYRE: {
                return hasReqs && Static.getVars().getBit(4493) > 0;
            }
            case TROLL_STRONGHOLD: {
                return hasReqs && Static.getSkills().getLevel(Skill.AGILITY) >= 73 && Static.getVars().getBit(4493) > 0;
            }
            case ARDOUGNE_TELEPORT_TAB: {
                return hasReqs && Static.getVars().getVarp(165) >= 30;
            }
            case SALVE_GRAVEYARD_TELEPORT_TAB: {
                return hasReqs && Static.getVars().getVarp(302) >= 61;
            }
            case EXPLORERS_RING: {
                return hasReqs && Static.getVars().getBit(4552) < 3;
            }
            case CIVITAS_TELEPORT_TAB: {
                return hasReqs && Static.getQuests().isFinished(Quest.TWILIGHTS_PROMISE);
            }
        }
        return hasReqs;
    }

    public int[] getItemIds() {
        return this.itemIds;
    }

    public String getAction() {
        return this.action;
    }

    public String getEquippedAction() {
        return this.equippedAction;
    }
}

