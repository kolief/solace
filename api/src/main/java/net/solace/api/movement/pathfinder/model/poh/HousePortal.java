package net.solace.api.movement.pathfinder.model.poh;

import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;

public enum HousePortal {
    ARCEUUS_LIBRARY(new WorldPoint(1634, 3836, 0), "Arceuus Library Portal", "Arceuus Library"),
    DRAYNOR_MANOR(new WorldPoint(3109, 3352, 0), "Draynor Manor Portal", "Draynor Manor"),
    BATTLEFRONT(new WorldPoint(1350, 3740, 0), "Battlefront Portal", "Battlefront"),
    VARROCK(new WorldPoint(3212, 3424, 0), "Varrock Portal", "Varrock", "Varrock"),
    GRAND_EXCHANGE(new WorldPoint(3165, 3478, 0), "Grand Exchange Portal", "Grand Exchange", "Grand Exchange"),
    MIND_ALTAR(new WorldPoint(2978, 3508, 0), "Mind Altar Portal", "Mind Altar"),
    LUMBRIDGE(new WorldPoint(3225, 3219, 0), "Lumbridge Portal", "Lumbridge"),
    FALADOR(new WorldPoint(2966, 3379, 0), "Falador Portal", "Falador"),
    SALVE_GRAVEYARD(new WorldPoint(3431, 3460, 0), "Salve Graveyard Portal", "Salve Graveyard"),
    CAMELOT(new WorldPoint(2757, 3479, 0), "Camelot Portal", "Camelot", "Camelot"),
    SEERS_VILLAGE(new WorldPoint(2725, 3485, 0), "Seers' Village Portal", "Seers' Village", "Seers' Village"),
    FENKENSTRAINS_CASTLE(new WorldPoint(3546, 3528, 0), "Fenkenstrain's Castle Portal", "Fenken' Castle"),
    EAST_ARDOUGNE(new WorldPoint(2661, 3300, 0), "Ardougne Portal", "Ardougne"),
    WATCHTOWER(new WorldPoint(2931, 4717, 2), "Yanille Portal", "Watchtower", "Watchtower"),
    YANILLE(new WorldPoint(2605, 3094, 0), "Yanille Portal", "Yanille", "Yanille"),
    SENNTISTEN(new WorldPoint(3321, 3335, 0), "Senntisten Portal", "Senntisten"),
    WEST_ARDOUGNE(new WorldPoint(2502, 3291, 0), "West Ardougne Portal", "West Ardougne"),
    MARIM(new WorldPoint(2798, 2798, 1), "Marim Portal", "Marim"),
    HARMONY_ISLAND(new WorldPoint(3799, 2867, 0), "Harmony Island Portal", "Harmony Island"),
    KHARYRLL(new WorldPoint(3493, 3474, 0), "Kharyrll Portal", "Kharyrll"),
    LUNAR_ISLE(new WorldPoint(2113, 3917, 0), "Lunar Isle Portal", "Lunar Isle"),
    KOUREND(new WorldPoint(1643, 3673, 0), "Kourend Portal", "Kourend Castle"),
    CEMETERY(new WorldPoint(2978, 3763, 0), "Cemetery Portal", "Cemetery"),
    WATERBIRTH_ISLAND(new WorldPoint(2548, 3758, 0), "Waterbirth Island Portal", "Waterbirth Island"),
    BARROWS(new WorldPoint(3563, 3313, 0), "Barrows Portal", "Barrows"),
    CARRALLANGAR(new WorldPoint(3157, 3667, 0), "Carrallangar Portal", "Carrallangar"),
    FISHING_GUILD(new WorldPoint(2611, 3390, 0), "Fishing Guild Portal", "Fishing Guild"),
    CATHERBY(new WorldPoint(2802, 3449, 0), "Catherby Portal", "Catherby"),
    ANNAKARL(new WorldPoint(3288, 3888, 0), "Annakarl Portal", "Annakarl"),
    APE_ATOLL_DUNGEON(new WorldPoint(2769, 9100, 0), "Ape Atoll Dungeon Portal", "Ape Atoll Dungeon"),
    GHORROCK(new WorldPoint(2977, 3872, 0), "Ghorrock Portal", "Ghorrock"),
    TROLL_STRONGHOLD(new WorldPoint(2844, 3693, 0), "Troll Stronghold Portal", "Troll Stronghold"),
    WEISS(new WorldPoint(2846, 3940, 0), "Weiss Portal", "Weiss"),
    CIVITAS_ILLA_FORTIS(new WorldPoint(1679, 3132, 0), "Civitas illa Fortis Portal", "Civitas illa Fortis"),
    TROLLHEIM(new WorldPoint(2888, 3677, 0), "Trollheim Portal", "Trollheim"),
    PADDEWWA(new WorldPoint(3098, 9883, 0), "Paddewwa Portal", "Paddewwa"),
    LASSAR(new WorldPoint(3004, 3468, 0), "Lassar Portal", "Lassar"),
    DAREEYAK(new WorldPoint(2969, 3695, 0), "Dareeyak Portal", "Dareeyak"),
    OURANIA(new WorldPoint(2470, 3247, 0), "Ourania Portal", "Ourania Altar"),
    BARBARIAN(new WorldPoint(2545, 3571, 0), "Barbarian Portal", "Barbarian Outpost"),
    KHAZARD(new WorldPoint(2637, 3166, 0), "Khazard Portal", "Khazard Battlefield"),
    ICE_PLATEAU(new WorldPoint(2973, 3939, 0), "Ice Plateau Portal", "Ice Plateau");

    private final WorldPoint destination;
    private final String portalName;
    private final String nexusTarget;
    private final String action;

    private HousePortal(WorldPoint destination, String portalName, String nexusTarget) {
        this(destination, portalName, nexusTarget, "Enter");
    }

    public String getModifiedName() {
        if (this == VARROCK || this == GRAND_EXCHANGE) {
            return Static.getVars().getBit(4585) == 0 ? VARROCK.getPortalName() : GRAND_EXCHANGE.getPortalName();
        }
        if (this == SEERS_VILLAGE || this == CAMELOT) {
            return Static.getVars().getBit(4560) == 0 ? CAMELOT.getPortalName() : SEERS_VILLAGE.getPortalName();
        }
        return this.portalName;
    }

    public WorldPoint getDestination() {
        return this.destination;
    }

    public String getPortalName() {
        return this.portalName;
    }

    public String getNexusTarget() {
        return this.nexusTarget;
    }

    public String getAction() {
        return this.action;
    }

    private HousePortal(WorldPoint destination, String portalName, String nexusTarget, String action) {
        this.destination = destination;
        this.portalName = portalName;
        this.nexusTarget = nexusTarget;
        this.action = action;
    }
}

