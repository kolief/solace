package net.solace.loader.plugins.wintertodt;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;

import java.util.List;

public interface WintertodtConstants {
    int ENTRANCE_DOOR_ID = ObjectID.WINT_DOOR;
    int BANK_CHEST_ID = ObjectID.WINT_BANKCHEST;
    int SW_TREE_ID = ObjectID.WINT_ROOTS;
    int SNOWFALL_ID = 26690;
    int LIGHT_ANIMATION_ID = 733;
    int FEED_ANIMATION_ID = 832;
    int FLETCH_ANIMATION_ID = 1248;
    List<Integer> CHOP_ANIMATION_IDS = List.of(867, 869, 871, 873, 875, 877, 879, 2846, 10070);

    WorldPoint ENTRANCE_COORD = new WorldPoint(1630, 3965, 0);
    WorldPoint BANK_COORD = new WorldPoint(1641, 3944, 0);
    WorldPoint SAFE_SPOT = new WorldPoint(1630, 3981, 0);
    WorldPoint SW_BRAZIER_COORD = new WorldPoint(1621, 3998, 0);
    WorldPoint SW_TREE_COORD = new WorldPoint(1620, 3988, 0);
    WorldPoint SW_SAFESPOT_COORD = new WorldPoint(1622, 3988, 0);
}
