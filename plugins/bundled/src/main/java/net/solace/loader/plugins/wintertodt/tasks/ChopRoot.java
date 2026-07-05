package net.solace.loader.plugins.wintertodt.tasks;

import net.runelite.api.Player;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;

public class ChopRoot extends WintertodtTask {
    public ChopRoot(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return isInside()
                && getEnergy() > 3
                && !Inventory.isFull()
                && shouldChop();
    }

    @Override
    public int execute() {
        Player local = Players.getLocal();
        if (!local.getWorldLocation().equals(WintertodtConstants.SW_SAFESPOT_COORD)) {
            Movement.walk(WintertodtConstants.SW_SAFESPOT_COORD);
            return -2;
        }

        if (isChopping()) {
            return -1;
        }

        getTree().interact("Chop");
        setInterrupted(false);
        return -3;
    }

    private boolean shouldChop() {
        return (Inventory.getCount("Bruma kindling") == 0 && Inventory.getCount("Bruma root") == 0)
                || (!isChopping() && getTree().distanceTo(Players.getLocal()) < 3);
    }

    private ITileObject getTree() {
        return TileObjects.getFirstAt(WintertodtConstants.SW_TREE_COORD, WintertodtConstants.SW_TREE_ID);
    }

    @Override
    public String toString() {
        return "Chopping root";
    }
}
