package net.solace.api.movement.pathfinder.model.poh;

import java.util.HashSet;
import java.util.Set;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.poh.HousePortal;

public enum PortalNexus {
    ARCEUUS_LIBRARY(HousePortal.ARCEUUS_LIBRARY, 20),
    DRAYNOR_MANOR(HousePortal.DRAYNOR_MANOR, 21),
    BATTLEFRONT(HousePortal.BATTLEFRONT, 22),
    VARROCK(HousePortal.VARROCK, 1),
    GRAND_EXCHANGE(HousePortal.GRAND_EXCHANGE, 151),
    MIND_ALTAR(HousePortal.MIND_ALTAR, 23),
    LUMBRIDGE(HousePortal.LUMBRIDGE, 2),
    FALADOR(HousePortal.FALADOR, 3),
    SALVE_GRAVEYARD(HousePortal.SALVE_GRAVEYARD, 24),
    CAMELOT(HousePortal.CAMELOT, 4),
    SEERS_VILLAGE(HousePortal.SEERS_VILLAGE, 154),
    FENKENSTRAINS_CASTLE(HousePortal.FENKENSTRAINS_CASTLE, 25),
    EAST_ARDOUGNE(HousePortal.EAST_ARDOUGNE, 5),
    WATCHTOWER(HousePortal.WATCHTOWER, 6),
    YANILLE(HousePortal.YANILLE, 156),
    SENNTISTEN(HousePortal.SENNTISTEN, 7),
    WEST_ARDOUGNE(HousePortal.WEST_ARDOUGNE, 26),
    MARIM(HousePortal.MARIM, 8),
    HARMONY_ISLAND(HousePortal.HARMONY_ISLAND, 27),
    KHARYRLL(HousePortal.KHARYRLL, 9),
    LUNAR_ISLE(HousePortal.LUNAR_ISLE, 10),
    KOUREND(HousePortal.KOUREND, 11),
    CEMETERY(HousePortal.CEMETERY, 28),
    WATERBIRTH_ISLAND(HousePortal.WATERBIRTH_ISLAND, 12),
    BARROWS(HousePortal.BARROWS, 29),
    CARRALLANGAR(HousePortal.CARRALLANGAR, 18),
    FISHING_GUILD(HousePortal.FISHING_GUILD, 13),
    CATHERBY(HousePortal.CATHERBY, 16),
    ANNAKARL(HousePortal.ANNAKARL, 14),
    APE_ATOLL_DUNGEON(HousePortal.APE_ATOLL_DUNGEON, 30),
    GHORROCK(HousePortal.GHORROCK, 17),
    TROLL_STRONGHOLD(HousePortal.TROLL_STRONGHOLD, 15),
    WEISS(HousePortal.WEISS, 19),
    CIVITAS_ILLA_FORTIS(HousePortal.CIVITAS_ILLA_FORTIS, 31),
    TROLLHEIM(HousePortal.TROLL_STRONGHOLD, 32),
    PADDEWWA(HousePortal.PADDEWWA, 33),
    LASSAR(HousePortal.LASSAR, 34),
    DAREEYAK(HousePortal.DAREEYAK, 35),
    OURANIA(HousePortal.OURANIA, 36),
    BARBARIAN(HousePortal.BATTLEFRONT, 37),
    KHAZARD(HousePortal.EAST_ARDOUGNE, 38),
    ICE_PLATEAU(HousePortal.CEMETERY, 39);

    private final HousePortal portal;
    private final int varValue;

    public static Set<PortalNexus> getAvailable() {
        int[] varbitArray;
        HashSet<PortalNexus> out = new HashSet<PortalNexus>();
        for (int varbit : varbitArray = new int[]{6654, 6655, 6656, 6657, 6658, 6659, 6660, 6661, 6662, 6663, 6664, 6665, 6666, 6667, 6668, 6554, 6555, 6556, 10080, 10081, 10082, 10083, 10084, 10085, 10086, 10087, 10088, 10089, 10090, 10091, 10019, 10020, 10021, 10022, 10023, 20111, 20112, 20113, 20114, 20115, 20116, 20117, 20118, 20119, 20120}) {
            int id = Static.getVars().getBit(varbit);
            for (PortalNexus portalNexus : PortalNexus.values()) {
                if (portalNexus.getVarValue() != id) continue;
                out.add(portalNexus);
            }
        }
        if (Static.getVars().getBit(4480) == 1) {
            if (out.contains((Object)VARROCK) && !out.contains((Object)GRAND_EXCHANGE)) {
                out.add(GRAND_EXCHANGE);
            } else if (out.contains((Object)GRAND_EXCHANGE) && !out.contains((Object)VARROCK)) {
                out.add(VARROCK);
            }
        }
        if (Static.getVars().getBit(4477) == 1) {
            if (out.contains((Object)CAMELOT) && !out.contains((Object)SEERS_VILLAGE)) {
                out.add(SEERS_VILLAGE);
            } else if (out.contains((Object)SEERS_VILLAGE) && !out.contains((Object)CAMELOT)) {
                out.add(CAMELOT);
            }
        }
        if (Static.getVars().getBit(4460) == 1) {
            if (out.contains((Object)YANILLE) && !out.contains((Object)WATCHTOWER)) {
                out.add(WATCHTOWER);
            } else if (out.contains((Object)WATCHTOWER) && !out.contains((Object)YANILLE)) {
                out.add(YANILLE);
            }
        }
        return out;
    }

    public HousePortal getPortal() {
        return this.portal;
    }

    public int getVarValue() {
        return this.varValue;
    }

    private PortalNexus(HousePortal portal, int varValue) {
        this.portal = portal;
        this.varValue = varValue;
    }
}

