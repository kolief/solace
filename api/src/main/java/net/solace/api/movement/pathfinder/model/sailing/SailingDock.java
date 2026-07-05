package net.solace.api.movement.pathfinder.model.sailing;

import java.util.Arrays;
import net.runelite.api.coords.WorldPoint;

public enum SailingDock {
    PORT_SARIM(0, new WorldPoint(3049, 3193, 0)),
    THE_PANDEMONIUM(1, new WorldPoint(3068, 2987, 0)),
    LANDS_END(2, new WorldPoint(1505, 3402, 0)),
    MUSA_POINT(3, new WorldPoint(2959, 3147, 0)),
    HOSIDIUS(4, new WorldPoint(1726, 3454, 0)),
    RIMMINGTON(5, new WorldPoint(2908, 3226, 0)),
    CATHERBY(6, new WorldPoint(2797, 3414, 0)),
    PORT_PISCARILIUS(7, new WorldPoint(1845, 3689, 0)),
    BRIMHAVEN(8, new WorldPoint(2760, 3229, 0)),
    ARDOUGNE(9, new WorldPoint(2670, 3267, 0)),
    PORT_KHAZARD(10, new WorldPoint(2684, 3162, 0)),
    WITCHAVEN(11, new WorldPoint(2745, 3305, 0)),
    ENTRANA(12, new WorldPoint(2877, 3335, 0)),
    CIVITAS_ILLA_FORTIS(13, new WorldPoint(1776, 3141, 0)),
    CORSAIR_COVE(14, new WorldPoint(2578, 2843, 0)),
    CAIRN_ISLE(15, new WorldPoint(2751, 2952, 0)),
    SUNSET_COAST(16, new WorldPoint(2730, 2962, 0)),
    THE_SUMMER_SHORE(17, new WorldPoint(3174, 2369, 0)),
    ALDARIN(18, new WorldPoint(1452, 2968, 0)),
    RUINS_OF_UNKAH(19, new WorldPoint(3145, 2825, 0)),
    VOID_KNIGHTS_OUTPOST(20, new WorldPoint(2651, 2676, 0)),
    PORT_ROBERTS(21, new WorldPoint(1862, 3307, 0)),
    RED_ROCK(22, new WorldPoint(2807, 2509, 0)),
    RELLEKKA(23, new WorldPoint(2631, 3703, 0)),
    ETCETERIA(24, new WorldPoint(2606, 3727, 0)),
    PORT_TYRAS(25, new WorldPoint(2144, 3121, 0)),
    DEEPFIN_POINT(26, new WorldPoint(1923, 2760, 0)),
    JATIZSO(27, new WorldPoint(2412, 3781, 0)),
    NEITIZNOT(28, new WorldPoint(2310, 3783, 0)),
    PRIFDDINAS(29, new WorldPoint(2158, 3325, 0)),
    PISCATORIS(30, new WorldPoint(2305, 3689, 0)),
    LUNAR_ISLE(31, new WorldPoint(2150, 3881, 0)),
    ISLE_OF_SOULS(32, new WorldPoint(2281, 2823, 0)),
    WATERBIRTH_ISLAND(33, new WorldPoint(2542, 3764, 0)),
    WEISS(34, new WorldPoint(2860, 3970, 0)),
    DOGNOSE_ISLAND(35, new WorldPoint(3060, 2640, 0)),
    REMOTE_ISLAND(36, new WorldPoint(2970, 2603, 0)),
    THE_LITTLE_PEARL(37, new WorldPoint(3354, 2215, 0)),
    THE_ONYX_CREST(38, new WorldPoint(2997, 2287, 0)),
    LAST_LIGHT(39, new WorldPoint(2500, 2326, 0)),
    CHARRED_ISLAND(40, new WorldPoint(2659, 2394, 0)),
    VATRACHOS_ISLAND(41, new WorldPoint(1874, 2984, 0)),
    ANGLERS_RETREAT(42, new WorldPoint(2468, 2720, 0)),
    MINOTAURS_REST(43, new WorldPoint(1960, 3115, 0)),
    ISLE_OF_BONES(44, new WorldPoint(2532, 2532, 0)),
    TEAR_OF_THE_SOUL(45, new WorldPoint(2319, 2775, 0)),
    WINTUMBER_ISLAND(46, new WorldPoint(2060, 2606, 0)),
    THE_CROWN_JEWEL(47, new WorldPoint(1765, 2658, 0)),
    RAINBOWS_END(48, new WorldPoint(2343, 2270, 0)),
    SUNBLEAK_ISLAND(49, new WorldPoint(2190, 2325, 0)),
    SHIMMERING_ATOLL(50, new WorldPoint(1558, 2772, 0)),
    LAGUNA_AURORAE(51, new WorldPoint(1201, 2734, 0)),
    CHINCHOMPA_ISLAND(52, new WorldPoint(1891, 3429, 0)),
    LLEDRITH_ISLAND(53, new WorldPoint(2096, 3187, 0)),
    YNYSDAIL(54, new WorldPoint(2222, 3467, 0)),
    BUCCANEERS_HAVEN(55, new WorldPoint(2080, 3689, 0)),
    DRUMSTICK_ISLE(56, new WorldPoint(2150, 3531, 0)),
    BRITTLE_ISLE(57, new WorldPoint(1954, 4057, 0)),
    GRIMSTONE(58, new WorldPoint(2927, 4056, 0));

    private final int id;
    private final WorldPoint location;

    public boolean isUnavailable() {
        return this.id == 253 || this.id == 254 || this.id == 255;
    }

    public static SailingDock fromId(int id) {
        return Arrays.stream(SailingDock.values()).filter(dock -> dock.id == id).findFirst().orElse(null);
    }

    public int getId() {
        return this.id;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    private SailingDock(int id, WorldPoint location) {
        this.id = id;
        this.location = location;
    }
}

