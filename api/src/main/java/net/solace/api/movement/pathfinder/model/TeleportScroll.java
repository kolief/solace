package net.solace.api.movement.pathfinder.model;

import net.runelite.api.Quest;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.widgets.IWidget;

public enum TeleportScroll {
    NARDAH("Nardah teleport", 12402, new WorldPoint(3419, 2918, 0), 5672, 5),
    DIGSITE("Digsite teleport", 12403, new WorldPoint(3324, 3413, 0), 5673, 9),
    FELDIP_HILLS("Feldip hills teleport", 12404, new WorldPoint(2540, 2924, 0), 5674, 13),
    LUNAR_ISLE("Lunar isle teleport", 12405, new WorldPoint(2093, 3913, 0), 5675, 17),
    MORTTON("Mort'ton teleport", 12406, new WorldPoint(3489, 3289, 0), 5676, 21),
    PEST_CONTROL("Pest control teleport", 12407, new WorldPoint(2659, 2660, 0), 5677, 25),
    PISCATORIS("Piscatoris teleport", 12408, new WorldPoint(2339, 3648, 0), 5678, 29),
    TAI_BWO_WANNAI("Tai bwo wannai teleport", 12409, new WorldPoint(2790, 3066, 0), 5679, 33),
    IORWERTH("Iorwerth camp teleport", 12410, new WorldPoint(2195, 3259, 0), 5680, 37),
    MOS_LE_HARMLESS("Mos le'harmless teleport", 12411, new WorldPoint(3701, 2996, 0), 5681, 41),
    LUMBERYARD("Lumberyard teleport", 12642, new WorldPoint(3302, 3486, 0), 5682, 45),
    ZUL_ANDRA("Zul-andra teleport", 12938, new WorldPoint(2195, 3055, 0), 5683, 49),
    KEY_MASTER("Key master teleport", 13249, new WorldPoint(1311, 1249, 0), 5684, 53),
    REVENANT_CAVE("Revenant cave teleport", 21802, new WorldPoint(3127, 3831, 0), 6056, 57),
    WATSON("Watson teleport", 23387, new WorldPoint(1644, 3579, 0), 8253, 61),
    SPIDER("Spider cave teleport", 29782, new WorldPoint(3657, 3402, 0), 10995, 63),
    WYRM("Colossal wyrm teleport scroll", 30040, new WorldPoint(1642, 2921, 0), 11029, 73),
    CHASM("Chasm teleport scroll", 30775, new WorldPoint(1440, 10075, 0), 16238, 74);

    private final String name;
    private final int itemId;
    private final WorldPoint destination;
    private final int bookVarbit;
    private final int bookWidgetChildIdx;

    public boolean canUse() {
        boolean canUse;
        boolean bl = canUse = Static.getInventory().contains(this.name) || Static.getInventory().contains("Master scroll book") && Static.getVars().getBit(this.bookVarbit) > 0;
        if (this == SPIDER) {
            return canUse && Static.getQuests().isFinished(Quest.PRIEST_IN_PERIL);
        }
        if (this == IORWERTH) {
            return canUse && Static.getQuests().isFinished(Quest.REGICIDE);
        }
        return canUse;
    }

    public boolean use() {
        if (this == REVENANT_CAVE && Static.getDialog().isOpen() && Static.getDialog().hasOption("Yes, teleport me now.")) {
            Static.getDialog().chooseOption("Yes, teleport me now");
            return true;
        }
        IWidget widget = Static.getWidgets().get(597, this.bookWidgetChildIdx);
        if (Static.getWidgets().isVisible(widget)) {
            widget.interact("Activate");
            return true;
        }
        IInventoryItem scroll = (IInventoryItem)Static.getInventory().getFirst(this.name);
        if (scroll != null) {
            scroll.interact("Teleport");
            return true;
        }
        IInventoryItem book = (IInventoryItem)Static.getInventory().getFirst("Master scroll book");
        if (book != null) {
            book.interact("Open");
            return true;
        }
        return false;
    }

    private TeleportScroll(String name, int itemId, WorldPoint destination, int bookVarbit, int bookWidgetChildIdx) {
        this.name = name;
        this.itemId = itemId;
        this.destination = destination;
        this.bookVarbit = bookVarbit;
        this.bookWidgetChildIdx = bookWidgetChildIdx;
    }

    public String getName() {
        return this.name;
    }

    public int getItemId() {
        return this.itemId;
    }

    public WorldPoint getDestination() {
        return this.destination;
    }

    public int getBookVarbit() {
        return this.bookVarbit;
    }

    public int getBookWidgetChildIdx() {
        return this.bookWidgetChildIdx;
    }
}

