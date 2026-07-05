package net.solace.api.movement.pathfinder.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.WalkOptions;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.movement.pathfinder.model.requirement.Comparison;
import net.solace.api.movement.pathfinder.model.requirement.CompositeSkillRequirement;
import net.solace.api.movement.pathfinder.model.requirement.ItemRequirement;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Reduction;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.movement.pathfinder.model.requirement.SkillRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarType;

public enum BankLocation {
    ALCHEMICAL_SOCIETY(new WorldArea(1397, 9312, 4, 3, 0), Requirements.of(Requirement.VISITED_VARLAMORE, new SkillRequirement(Skill.HERBLORE, 60))),
    AL_KHARID_BANK(new WorldArea(3267, 3161, 6, 13, 0)),
    ALDARIN_BANK(new WorldArea(1396, 2923, 6, 8, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    ARCEUUS_BANK(new WorldArea(1621, 3736, 18, 18, 0), Requirements.of(Requirement.VISITED_KOUREND)),
    ARDOUGNE_NORTH_BANK(new WorldArea(2612, 3330, 10, 6, 0)),
    ARDOUGNE_SOUTH_BANK(new WorldArea(2649, 3280, 9, 8, 0)),
    AUBURNVALE(new WorldArea(1412, 3349, 9, 9, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    BARBARIAN_OUTPOST_BANK(new WorldArea(2532, 3570, 6, 10, 0)),
    BLAST_FURNACE(new WorldArea(1937, 4956, 20, 6, 0), Requirements.of(QuestRequirement.of(Quest.THE_GIANT_DWARF, QuestState.IN_PROGRESS, QuestState.FINISHED))),
    BURGH_DE_ROTT_BANK(new WorldArea(3492, 3208, 10, 6, 0), Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 1990, 200))),
    CAM_TORUM(new WorldArea(1449, 9565, 7, 6, 1), Requirements.of(QuestRequirement.of(Quest.PERILOUS_MOONS, QuestState.FINISHED, QuestState.IN_PROGRESS))),
    CANIFIS_BANK(new WorldArea(3509, 3474, 6, 10, 0), Requirements.of(QuestRequirement.finished(Quest.PRIEST_IN_PERIL))),
    CASTLE_WARS_BANK(new WorldArea(2441, 3082, 5, 4, 0)),
    CATHERBY_BANK(new WorldArea(2806, 3438, 7, 6, 0)),
    CHAMBERS_OF_XERIC(new WorldArea(1252, 3570, 5, 4, 0), Requirements.of(Requirement.VISITED_KOUREND)),
    CHARCOAL_BURNERS_BANK(new WorldArea(1714, 3463, 8, 5, 0)),
    CIVITAS_EAST(new WorldArea(1778, 3092, 4, 6, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    CIVITAS_WEST(new WorldArea(1646, 3115, 4, 6, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    COLOSSEUM(new WorldArea(1800, 9499, 6, 7, 0), Requirements.of(Requirement.VISITED_VARLAMORE, new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARP, 4130, 2000))),
    CORSAIR_COVE_BANK(new WorldArea(2567, 2862, 7, 7, 0), Requirements.of(QuestRequirement.finished(Quest.THE_CORSAIR_CURSE))),
    CRAFTING_GUILD(new WorldArea(2929, 3279, 8, 10, 0), Requirements.of(new SkillRequirement(Skill.CRAFTING, 40), new ItemRequirement(Reduction.OR, List.of(Integer.valueOf(9780), Integer.valueOf(9781), Integer.valueOf(13280), Integer.valueOf(13342), Integer.valueOf(1757), Integer.valueOf(20208)), ItemRequirement.Location.EITHER, 1))),
    DARKFROST(new WorldArea(1524, 3291, 6, 5, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    DARKMEYER_BANK(new WorldArea(3601, 3365, 9, 5, 0), Requirements.of(QuestRequirement.finished(Quest.SINS_OF_THE_FATHER))),
    DEEPFIN_WEST(new WorldArea(2010, 9184, 4, 4, 0), Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 18361, 1))),
    DEEPFIN_EAST(new WorldArea(2092, 9196, 4, 3, 0), Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 18362, 1))),
    DRAYNOR_BANK(new WorldArea(3090, 3240, 6, 7, 0)),
    DUEL_ARENA_BANK(new WorldArea(3380, 3267, 5, 7, 0)),
    EDGEVILLE_BANK(new WorldArea(3091, 3488, 8, 12, 0)),
    ETCETERIA_BANK(new WorldArea(2618, 3893, 4, 4, 0), Requirements.of(QuestRequirement.finished(Quest.THRONE_OF_MISCELLANIA))),
    FALADOR_EAST_BANK(new WorldArea(3009, 3353, 13, 5, 0)),
    FALADOR_WEST_BANK(new WorldArea(2943, 3366, 7, 8, 0)),
    FARMING_GUILD_NORTH(new WorldArea(1245, 3754, 8, 7, 0), Requirements.of(new SkillRequirement(Skill.FARMING, 85))),
    FARMING_GUILD_SOUTH(new WorldArea(1250, 3738, 6, 6, 0), Requirements.of(new SkillRequirement(Skill.FARMING, 45))),
    FEROX_ENCLAVE_BANK(new WorldArea(3127, 3627, 7, 6, 0)),
    FOSSIL_ISLAND(new WorldArea(3765, 3898, 3, 3, 0), Requirements.of(QuestRequirement.of(Quest.BONE_VOYAGE, QuestState.FINISHED))),
    FOSSIL_ISLAND_TENT(new WorldArea(3738, 3802, 5, 5, 0), Requirements.of(QuestRequirement.of(Quest.BONE_VOYAGE, QuestState.FINISHED), new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 5800, 1))),
    GRAND_EXCHANGE_BANK(new WorldArea(3159, 3484, 12, 12, 0)),
    GRAND_TREE_SOUTH_BANK(new WorldArea(2448, 3480, 3, 5, 1)),
    GRAND_TREE_WEST_BANK(new WorldArea(2440, 3487, 5, 3, 1)),
    HALLOWED_SEPULCHRE(new WorldArea(2393, 5975, 15, 15, 0), Requirements.of(QuestRequirement.of(Quest.SINS_OF_THE_FATHER, QuestState.FINISHED))),
    HOSIDIUS_BANK(new WorldArea(1746, 3596, 5, 6, 0)),
    HOSIDIUS_KITCHEN(new WorldArea(1675, 3615, 5, 5, 0)),
    HOSIDIUS_VINE_YARD(new WorldArea(1807, 3564, 5, 5, 0)),
    HUNTER_GUILD(new WorldArea(1541, 3039, 4, 5, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    JATIZSO_BANK(new WorldArea(2413, 3798, 7, 7, 0), Requirements.of(QuestRequirement.of(Quest.THE_FREMENNIK_ISLES, QuestState.IN_PROGRESS, QuestState.FINISHED))),
    KOUREND_CASTLE_BANK(new WorldArea(1610, 3679, 4, 5, 2), Requirements.of(Requirement.VISITED_KOUREND)),
    LLETYA_BANK(new WorldArea(2349, 3160, 8, 7, 0)),
    LUMB_CELLAR(new WorldArea(3208, 9615, 12, 9, 0), Requirements.of(QuestRequirement.of(Quest.RECIPE_FOR_DISASTER, QuestState.IN_PROGRESS, QuestState.FINISHED), new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 1866, 1))),
    LUMBRIDGE_BANK(new WorldArea(3207, 3215, 4, 8, 2)),
    MARIM(new WorldArea(2778, 2781, 5, 5, 0), Requirements.of(QuestRequirement.of(Quest.MONKEY_MADNESS_II, QuestState.FINISHED))),
    LUNAR_ISLE(new WorldArea(2097, 3917, 2, 5, 0), Requirements.of(() -> Static.getInventory().contains(9083) || Static.getEquipment().contains(9083) || Static.getVars().getBit(4494) == 1)),
    MINING_GUILD(new WorldArea(3012, 9716, 5, 5, 0), Requirements.of(new SkillRequirement(Skill.MINING, 60))),
    MISTROCK(new WorldArea(1379, 2864, 5, 5, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    MOR_UL_REK(new WorldArea(2538, 5137, 8, 8, 0), Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 5646, 1))),
    MOS_LEHARMLESS_EAST(new WorldArea(3806, 3020, 10, 5, 0), Requirements.of(QuestRequirement.of(Quest.CABIN_FEVER, QuestState.FINISHED))),
    MOS_LEHARMLESS_WEST(new WorldArea(3677, 2980, 6, 5, 0), Requirements.of(QuestRequirement.of(Quest.CABIN_FEVER, QuestState.FINISHED))),
    MOUNT_KARUULM(new WorldArea(1320, 3820, 8, 8, 0), Requirements.of(Requirement.VISITED_KOUREND)),
    MYTHS_GUILD(new WorldArea(2462, 2844, 5, 7, 1), Requirements.of(QuestRequirement.finished(Quest.DRAGON_SLAYER_II))),
    NARDAH(new WorldArea(3424, 2889, 9, 6, 0)),
    NEITIZNOT_BANK(new WorldArea(2334, 3805, 6, 4, 0), Requirements.of(QuestRequirement.of(Quest.THE_FREMENNIK_ISLES, QuestState.IN_PROGRESS, QuestState.FINISHED))),
    NEMUS_RETREAT(new WorldArea(1385, 3307, 4, 5, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    NEX_BANK(new WorldArea(2900, 5198, 9, 11, 0), Requirements.of(QuestRequirement.finished(Quest.THE_FROZEN_DOOR))),
    PANDEMONIUM(new WorldArea(3036, 2998, 5, 4, 0), Requirements.of(QuestRequirement.finished(Quest.PANDEMONIUM))),
    PORT_KHAZARD_BANK(new WorldArea(2658, 3158, 7, 7, 0)),
    PORT_PHASMATYS_BANK(new WorldArea(3686, 3464, 6, 5, 0), Requirements.of(QuestRequirement.finished(Quest.GHOSTS_AHOY))),
    PORT_PISCARILIUS_BANK(new WorldArea(1794, 3787, 18, 6, 0)),
    PRIFDDINAS_NORTH(new WorldArea(3255, 6103, 5, 9, 0), Requirements.of(QuestRequirement.finished(Quest.SONG_OF_THE_ELVES))),
    PRIFDDINAS_SOUTH(new WorldArea(3294, 6058, 5, 4, 0), Requirements.of(QuestRequirement.finished(Quest.SONG_OF_THE_ELVES))),
    QUETZACALLI_GORGE(new WorldArea(1517, 3227, 6, 4, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    ROGUES_DEN(new WorldArea(3039, 4966, 10, 10, 1)),
    SEERS_VILLAGE_BANK(new WorldArea(2721, 3490, 10, 6, 0)),
    SHANTAY_PASS_BANK(new WorldArea(3299, 3118, 11, 10, 0)),
    SHAYZIEN_ENCAMPMENT(new WorldArea(1482, 3644, 6, 5, 0), Requirements.of(Requirement.VISITED_KOUREND)),
    SHAYZIEN_NORTH(new WorldArea(1485, 3589, 8, 7, 0), Requirements.of(Requirement.VISITED_KOUREND)),
    SHILO_VILLAGE_BANK(new WorldArea(2842, 2951, 20, 8, 0), Requirements.of(QuestRequirement.finished(Quest.SHILO_VILLAGE))),
    SOUL_WARS(new WorldArea(2208, 2856, 9, 7, 0)),
    TAL_TEKLAN(new WorldArea(1241, 3120, 5, 3, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    TEMPLE_OF_THE_EYE(new WorldArea(3615, 9471, 5, 5, 0), Requirements.of(QuestRequirement.finished(Quest.TEMPLE_OF_THE_EYE))),
    TEMPOROSS(new WorldArea(3155, 2834, 4, 3, 0)),
    TOMBS_OF_AMASCUT(new WorldArea(3351, 9117, 5, 5, 0), Requirements.of(QuestRequirement.finished(Quest.BENEATH_CURSED_SANDS))),
    TREE_GNOME_STRONGHOLD_BANK(new WorldArea(2443, 3422, 7, 6, 1)),
    TZHAAR(new WorldArea(2442, 5175, 8, 7, 0)),
    VARROCK_EAST_BANK(new WorldArea(3250, 3418, 8, 6, 0)),
    VARROCK_WEST_BANK(new WorldArea(3180, 3433, 8, 15, 0)),
    VER_SINHAZA_BANK(new WorldArea(3646, 3204, 10, 11, 0)),
    WARRIORS_GUILD(new WorldArea(2841, 3540, 6, 6, 0), Requirements.of(new CompositeSkillRequirement(130, Skill.ATTACK, Skill.STRENGTH))),
    WINTERTODT_BANK(new WorldArea(1639, 3943, 3, 3, 0)),
    WOODCUTTING_GUILD(new WorldArea(1589, 3475, 5, 5, 0), Requirements.of(new SkillRequirement(Skill.WOODCUTTING, 60))),
    YANILLE_BANK(new WorldArea(2609, 3088, 7, 10, 0)),
    FISHING_GUILD(new WorldArea(2584, 3418, 4, 5, 0), Requirements.of(new SkillRequirement(Skill.FISHING, 68))),
    LANDS_END(new WorldArea(1508, 3415, 6, 9, 0));

    private final WorldArea area;
    private final Requirements requirements;

    private BankLocation(WorldArea area) {
        this(area, new Requirements());
    }

    public static BankLocation getNearest() {
        return Arrays.stream(BankLocation.values()).filter(loc -> loc.requirements.fulfilled()).min(Comparator.comparingInt(x -> x.getArea().distanceTo2D(Static.getPlayers().getLocal().getWorldLocation()))).orElse(null);
    }

    public static BankLocation getNearestPath(boolean useTeleports) {
        return Arrays.stream(BankLocation.values()).filter(loc -> loc.requirements.fulfilled()).min(Comparator.comparingInt(x -> {
            try {
                ArrayList<WorldPoint> startPoints = new ArrayList<WorldPoint>();
                startPoints.add(Static.getPlayers().getLocal().getWorldLocation());
                HashMap<WorldPoint, Teleport> teleports = new HashMap<WorldPoint, Teleport>();
                if (useTeleports) {
                    teleports = Static.getWalker().buildTeleportLinks(x.getArea());
                }
                return Static.getMovement().getPath(startPoints, x.getArea(), Static.getGlobalCollisionMap(), false, true, teleports).size();
            }
            catch (Exception e) {
                return Integer.MAX_VALUE;
            }
        })).orElse(null);
    }

    public static BankLocation getNearestPath() {
        return BankLocation.getNearestPath(false);
    }

    public static BankLocation findNearest() {
        return BankLocation.findNearest(Arrays.stream(BankLocation.values()).collect(Collectors.toList()), WalkOptions.builder().build());
    }

    public static BankLocation findNearest(List<BankLocation> locations, WalkOptions options) {
        List<BankLocation> usableBanks = locations.stream().filter(BankLocation::canUse).collect(Collectors.toList());
        if (usableBanks.isEmpty()) {
            return null;
        }
        List<WorldArea> targetAreas = usableBanks.stream().map(BankLocation::getArea).collect(Collectors.toList());
        try {
            TilePath path = Static.getWalker().buildPath(targetAreas, options);
            if (path == null || path.isEmpty()) {
                return null;
            }
            WorldPoint destination = path.getDestination();
            BankLocation matchingBank = usableBanks.stream().filter(bank -> bank.getArea().contains(destination)).findFirst().orElse(null);
            if (matchingBank == null) {
                matchingBank = usableBanks.stream().filter(bank -> bank.getArea().distanceTo(destination) <= 2).min(Comparator.comparingInt(bank -> bank.getArea().distanceTo(destination))).orElse(null);
            }
            return matchingBank;
        }
        catch (Exception e) {
            return null;
        }
    }

    public boolean canUse() {
        return this.requirements.fulfilled();
    }

    public String toString() {
        String name = this.name();
        return name.charAt(0) + name.substring(1).replace("_BANK", "").replace("_", " ").toLowerCase();
    }

    private BankLocation(WorldArea area, Requirements requirements) {
        this.area = area;
        this.requirements = requirements;
    }

    public WorldArea getArea() {
        return this.area;
    }

    public Requirements getRequirements() {
        return this.requirements;
    }
}

