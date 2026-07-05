package net.solace.api.movement.pathfinder.model;

import java.awt.Polygon;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.coords.Cuboid;
import net.solace.api.game.IVars;
import org.apache.commons.lang3.tuple.Pair;

public class MovementConstants {
    public static final Polygon NOT_WILDERNESS_BLACK_KNIGHTS = new Polygon(new int[]{2994, 2995, 2996, 2996, 2994, 2994, 2997, 2998, 2998, 2999, 3000, 3001, 3002, 3003, 3004, 3005, 3005, 3005, 3019, 3020, 3022, 3023, 3024, 3025, 3026, 3026, 3027, 3027, 3028, 3028, 3029, 3029, 3030, 3030, 3031, 3031, 3032, 3033, 3034, 3035, 3036, 3037, 3037}, new int[]{3525, 3526, 3527, 3529, 3529, 3534, 3534, 3535, 3536, 3537, 3538, 3539, 3540, 3541, 3542, 3543, 3544, 3545, 3545, 3546, 3546, 3545, 3544, 3543, 3543, 3542, 3541, 3540, 3539, 3537, 3536, 3535, 3534, 3533, 3532, 3531, 3530, 3529, 3528, 3527, 3526, 3526, 3525}, 43);
    public static final Cuboid MAIN_WILDERNESS_CUBOID = new Cuboid(2944, 3525, 0, 3391, 4351, 3);
    public static final Cuboid GOD_WARS_WILDERNESS_CUBOID = new Cuboid(3008, 10112, 0, 3071, 10175, 3);
    public static final Cuboid WILDERNESS_UNDERGROUND_CUBOID = new Cuboid(2944, 9920, 0, 3455, 10879, 3);
    public static final WorldArea WILDERNESS_ABOVE_GROUND = new WorldArea(2944, 3523, 448, 448, 0);
    public static final WorldArea WILDERNESS_UNDERGROUND = new WorldArea(2944, 9918, 320, 442, 0);
    public static final WorldArea ESCAPE_CAVES = new WorldArea(3329, 10241, 60, 61, 0);
    public static final Skill[] MEMBER_SKILLS = new Skill[]{Skill.AGILITY, Skill.HERBLORE, Skill.THIEVING, Skill.FLETCHING, Skill.SLAYER, Skill.FARMING, Skill.CONSTRUCTION, Skill.HERBLORE};
    public static final WorldArea[] FEROX_AREAS = new WorldArea[]{new WorldArea(3126, 3618, 19, 22, 0), new WorldArea(3138, 3627, 17, 20, 0), new WorldArea(3123, 3622, 6, 12, 0), new WorldArea(3125, 3634, 1, 6, 0), new WorldArea(3131, 3617, 8, 1, 0), new WorldArea(3126, 3618, 19, 22, 1), new WorldArea(3138, 3627, 17, 20, 1), new WorldArea(3123, 3622, 6, 12, 1), new WorldArea(3125, 3634, 1, 6, 1), new WorldArea(3131, 3617, 8, 1, 1)};
    public static final WorldArea[] WILDERNESS_BOSS_AREAS = new WorldArea[]{new WorldArea(1611, 11525, 39, 44, 2), new WorldArea(1740, 11525, 38, 37, 0), new WorldArea(1876, 11533, 23, 27, 1)};
    public static final WorldArea[] SAFE_WILDERNESS_UNDERGROUND = new WorldArea[]{new WorldArea(3140, 9986, 60, 61, 0)};
    public static final WorldArea[] HUNTER_GUILD = new WorldArea[]{new WorldArea(1535, 3022, 52, 53, 0), new WorldArea(1532, 9401, 56, 76, 0), new WorldArea(1546, 3022, 20, 30, 1), new WorldArea(1548, 3031, 20, 22, 2)};
    public static final WorldArea[] RESTRICTED_MINIGAME_TELEPORT_AREAS = new WorldArea[]{new WorldArea(3311, 3224, 78, 51, 0), new WorldArea(3275, 4751, 105, 11, 0)};
    public static final int[] UNDERWATER_REGION_IDS = new int[]{15008, 15264};
    public static final int[] RING_OF_DUELING = new int[]{2552, 2554, 2556, 2558, 2560, 2562, 2564, 2566};
    public static final int[] GAMES_NECKLACE = new int[]{3853, 3855, 3857, 3859, 3861, 3863, 3865, 3867};
    public static final int[] COMBAT_BRACELET = new int[]{11124, 11122, 11120, 11118, 11974, 11972};
    public static final int[] RING_OF_WEALTH = new int[]{11980, 11982, 11984, 11986, 11988};
    public static final int[] AMULET_OF_GLORY = new int[]{11978, 11976, 1712, 1710, 1708, 1706, 19707};
    public static final int[] NECKLACE_OF_PASSAGE = new int[]{21155, 21153, 21151, 21149, 21146};
    public static final int[] BURNING_AMULET = new int[]{21166, 21169, 21171, 21173, 21175};
    public static final int[] XERICS_TALISMAN = new int[]{13393};
    public static final int[] SLAYER_RING = new int[]{21268, 11866, 11867, 11868, 11869, 11870, 11871, 11872, 11873};
    public static final int[] DIGSITE_PENDANT = new int[]{11194, 11193, 11192, 11191, 11190};
    public static final int[] DRAKANS_MEDALLION = new int[]{22400};
    public static final int[] SKILLS_NECKLACE = new int[]{11968, 11970, 11105, 11107, 11109, 11111};
    public static final int[] TELEPORT_CRYSTAL = new int[]{23946, 13102, 6099, 6100, 6101, 6102};
    public static final int[] ENCHANTED_LYRE = new int[]{23458, 13079, 6127, 6126, 6125, 3691};
    public static final int[] SLASH_ITEMS = new int[]{946, 13108, 13109, 13110, 13111};
    public static final int[] ARDOUGNE_CLOAK = new int[]{13121, 13122, 13123, 13124, 20760};
    public static final int[] QUEST_POINT_CAPE = new int[]{9813, 13068};
    public static final int[] EXPLORERS_RING = new int[]{13126, 13127, 13128};
    public static final int[] CONSTRUCTION_CAPE = new int[]{9789, 9790};
    public static final int[] MAX_CAPE = new int[]{13280, 13342};
    public static final int[] GHOMMALS_HILT = new int[]{25930, 25932, 25934, 25936};
    public static final int[] RING_OF_THE_ELEMENTS = new int[]{26818};
    public static final int[] CRAFTING_CAPE = new int[]{9780, 9781};
    public static final int[] FARMING_CAPE = new int[]{9810, 9811};
    public static final int[] FISHING_CAPE = new int[]{9798, 9799};
    public static final int[] RADAS_BLESSING = new int[]{22945, 22947};
    public static final int[] AMULET_OF_THE_EYE = new int[]{26914, 26990, 26992, 26994};
    public static final int[] FAIRY_ITEMS = new int[]{772, 9084, 9091, 9092, 9093};
    public static final int[] HOUSE_ITEMS = new int[]{9789, 9790, 8013, 13280, 13342};
    public static final int[] CHARTERSHIP_BLACKLITSED = new int[]{431, 4033, 4285, 3164, 3165};
    public static final int[] SPIRIT_TREE_BLACKLISTED = new int[]{4033};
    public static final int[] LIGHT_SOURCES = new int[]{26844, 26848, 26846, 26842, 26840, 26838, 26836, 26824, 26834, 26828, 26832, 26830, 26826, 26834, 13280, 13342, 9804, 9805, 20720, 13137, 13138, 13139, 13140, 5013, 9065, 4702, 4550, 4539, 4531, 4534};
    public static final int[] QUETZAL_WHISTLE = new int[]{29271, 29273, 29275};
    public static final List<String> STRONGHOLD_ANSWERS = List.of("Only on the Old School RuneScape website.", "Do not visit the website and report the player who messaged you.", "Report the stream as a scam. Real Jagex streams have a 'verified' mark.", "Don't give out your password to anyone. Not even close friends.", "Don't give them my password.", "Politely tell them no, then use the 'Report Abuse' button.", "Nope, you're tricking me into going somewhere dangerous.", "Through account settings on oldschool.runescape.com.", "Decline the offer and report that player.", "Delete it - it's a fake!", "Delete it - it is fake!", "Don't tell them anything and click the 'Report Abuse' button.", "Nothing, it's a fake.", "Report the incident and do not click any links.", "No, you should never buy an account.", "Talk to any banker.", "Me.", "No.", "Nobody.", "Use the Account Recovery system.", "The birthday of a famous person or event.", "It's never reused on other websites or accounts.", "No, you should never allow anyone to use your account.", "Don't give them the information and send an 'Abuse report'.", "Set up two-factor authentication with my email provider.", "Virus scan my device then change my password.", "Secure my device and reset my password.", "No way! You'll just take my gold for your own! Reported!", "Two-factor authentication on your account and your registered email.", "No way! I'm reporting you to Jagex!", "Two-factor authentication on your account and your registered email.", "Secure my device and reset my password.", "Report the player for phishing.", "It's never used on other websites or accounts.", "Don't share your information and report the player.", "Read the text and follow the advice given.");
    public static final List<Pair<WorldPoint, WorldPoint>> SLASH_WEB_POINTS = List.of(Pair.of(new WorldPoint(3031, 3852, 0), new WorldPoint(3029, 3852, 0)), Pair.of(new WorldPoint(3148, 3727, 0), new WorldPoint(3146, 3727, 0)), Pair.of(new WorldPoint(3147, 3728, 0), new WorldPoint(3147, 3726, 0)), Pair.of(new WorldPoint(3164, 3736, 0), new WorldPoint(3162, 3736, 0)), Pair.of(new WorldPoint(3163, 3737, 0), new WorldPoint(3163, 3735, 0)), Pair.of(new WorldPoint(3183, 3734, 0), new WorldPoint(3183, 3732, 0)), Pair.of(new WorldPoint(3158, 3952, 0), new WorldPoint(3158, 3950, 0)), Pair.of(new WorldPoint(3210, 9899, 0), new WorldPoint(3210, 9897, 0)), Pair.of(new WorldPoint(3115, 3860, 0), new WorldPoint(3115, 3858, 0)), Pair.of(new WorldPoint(3093, 3957, 0), new WorldPoint(3092, 3957, 0)), Pair.of(new WorldPoint(3095, 3957, 0), new WorldPoint(3094, 3957, 0)), Pair.of(new WorldPoint(3105, 3959, 0), new WorldPoint(3105, 3957, 0)), Pair.of(new WorldPoint(3106, 3959, 0), new WorldPoint(3106, 3957, 0)), Pair.of(new WorldPoint(2654, 9767, 0), new WorldPoint(2654, 9765, 0)), Pair.of(new WorldPoint(2566, 3124, 0), new WorldPoint(2564, 3124, 0)), Pair.of(new WorldPoint(2569, 3119, 0), new WorldPoint(2569, 3117, 0)), Pair.of(new WorldPoint(2570, 3119, 0), new WorldPoint(2570, 3117, 0)), Pair.of(new WorldPoint(2573, 3124, 0), new WorldPoint(2574, 3123, 0)), Pair.of(new WorldPoint(2631, 9248, 0), new WorldPoint(2629, 9248, 0)), Pair.of(new WorldPoint(2632, 9264, 0), new WorldPoint(2630, 9264, 0)), Pair.of(new WorldPoint(2628, 9231, 1), new WorldPoint(2628, 9229, 1)), Pair.of(new WorldPoint(2629, 9239, 1), new WorldPoint(2629, 9237, 1)), Pair.of(new WorldPoint(2647, 9118, 0), new WorldPoint(2647, 9116, 0)), Pair.of(new WorldPoint(2638, 9092, 1), new WorldPoint(2638, 9090, 1)), Pair.of(new WorldPoint(2653, 9124, 1), new WorldPoint(2653, 9122, 1)), Pair.of(new WorldPoint(2663, 9110, 1), new WorldPoint(2663, 9108, 1)), Pair.of(new WorldPoint(2633, 9200, 0), new WorldPoint(2633, 9198, 0)), Pair.of(new WorldPoint(2646, 9190, 0), new WorldPoint(2644, 9190, 0)), Pair.of(new WorldPoint(2648, 9199, 0), new WorldPoint(2648, 9197, 0)), Pair.of(new WorldPoint(2662, 9206, 0), new WorldPoint(2662, 9204, 0)), Pair.of(new WorldPoint(2666, 9160, 0), new WorldPoint(2664, 9160, 0)), Pair.of(new WorldPoint(2668, 9194, 0), new WorldPoint(2666, 9194, 0)), Pair.of(new WorldPoint(2541, 9069, 1), new WorldPoint(2539, 9069, 1)), Pair.of(new WorldPoint(2547, 9064, 1), new WorldPoint(2547, 9062, 1)), Pair.of(new WorldPoint(2551, 9054, 1), new WorldPoint(2551, 9052, 1)), Pair.of(new WorldPoint(2555, 9039, 1), new WorldPoint(2553, 9039, 1)), Pair.of(new WorldPoint(2604, 9273, 1), new WorldPoint(2602, 9273, 1)), Pair.of(new WorldPoint(2618, 9211, 1), new WorldPoint(2618, 9209, 1)), Pair.of(new WorldPoint(2620, 9205, 1), new WorldPoint(2620, 9203, 1)), Pair.of(new WorldPoint(2571, 9051, 0), new WorldPoint(2569, 9051, 0)), Pair.of(new WorldPoint(2570, 9052, 0), new WorldPoint(2570, 9050, 0)), Pair.of(new WorldPoint(2599, 9080, 1), new WorldPoint(2597, 9080, 1)), Pair.of(new WorldPoint(2608, 9079, 1), new WorldPoint(2606, 9079, 1)), Pair.of(new WorldPoint(2610, 9047, 1), new WorldPoint(2610, 9045, 1)), Pair.of(new WorldPoint(2613, 9057, 1), new WorldPoint(2613, 9055, 1)), Pair.of(new WorldPoint(2619, 9071, 1), new WorldPoint(2617, 9071, 1)), Pair.of(new WorldPoint(2618, 9072, 1), new WorldPoint(2618, 9070, 1)), Pair.of(new WorldPoint(2674, 9039, 0), new WorldPoint(2674, 9037, 0)), Pair.of(new WorldPoint(2633, 9049, 1), new WorldPoint(2633, 9047, 1)), Pair.of(new WorldPoint(2639, 9062, 1), new WorldPoint(2637, 9062, 1)), Pair.of(new WorldPoint(2638, 9063, 1), new WorldPoint(2638, 9061, 1)), Pair.of(new WorldPoint(2645, 9056, 1), new WorldPoint(2643, 9056, 1)), Pair.of(new WorldPoint(2655, 9073, 1), new WorldPoint(2653, 9073, 1)), Pair.of(new WorldPoint(2654, 9074, 1), new WorldPoint(2654, 9072, 1)), Pair.of(new WorldPoint(2657, 9082, 1), new WorldPoint(2655, 9082, 1)), Pair.of(new WorldPoint(2676, 9074, 1), new WorldPoint(2674, 9074, 1)), Pair.of(new WorldPoint(2678, 9061, 1), new WorldPoint(2678, 9059, 1)), Pair.of(new WorldPoint(2678, 9068, 1), new WorldPoint(2678, 9066, 1)), Pair.of(new WorldPoint(1833, 9945, 0), new WorldPoint(1833, 9943, 0)), Pair.of(new WorldPoint(1841, 9934, 0), new WorldPoint(1841, 9932, 0)), Pair.of(new WorldPoint(1843, 9933, 0), new WorldPoint(1841, 9933, 0)), Pair.of(new WorldPoint(1842, 9934, 0), new WorldPoint(1842, 9932, 0)), Pair.of(new WorldPoint(1849, 9935, 0), new WorldPoint(1849, 9933, 0)), Pair.of(new WorldPoint(1850, 9935, 0), new WorldPoint(1850, 9933, 0)), Pair.of(new WorldPoint(1848, 9919, 0), new WorldPoint(1846, 9919, 0)), Pair.of(new WorldPoint(1847, 9920, 0), new WorldPoint(1847, 9918, 0)), Pair.of(new WorldPoint(1833, 9944, 0), new WorldPoint(1833, 9943, 0)), Pair.of(new WorldPoint(1841, 9934, 0), new WorldPoint(1841, 9933, 0)), Pair.of(new WorldPoint(1847, 9920, 0), new WorldPoint(1847, 9919, 0)));
    public static WorldPoint HOUSE_POINT = new WorldPoint(10000, 4000, 1);
    public static int VISISTED_VARLAMORE = 9652;
    public static int UNLOCKED_BIRD_TRANSPORT = 9649;
    public static int LAST_BIRD_TRANSPORT = 9950;

    public static boolean inDeathDomain() {
        return Static.getClient().isInInstancedRegion() && Arrays.stream(Static.getClient().getMapRegions()).anyMatch(x -> x == 12633);
    }

    public static boolean isInStronghold() {
        return Arrays.stream(Static.getClient().getMapRegions()).anyMatch(x -> x == 7505 || x == 8017 || x == 8530 || x == 9297);
    }

    public static boolean inHuntersGuild(WorldPoint location) {
        return Arrays.stream(HUNTER_GUILD).anyMatch(hunterGuild -> hunterGuild.contains(location));
    }

    public static boolean isInBarrows() {
        return Arrays.stream(Static.getClient().getMapRegions()).anyMatch(x -> x == 14231);
    }

    public static boolean hasVisitedVarlamore(IVars vars) {
        return vars.getBit(VISISTED_VARLAMORE) >= 2;
    }
}

