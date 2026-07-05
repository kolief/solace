package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.api.domain.tiles.ITileObject;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.items.Inventory;

public class RepairBrazier extends WintertodtTask {
    public RepairBrazier(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getConfig().repair() && isInside() && Inventory.contains("Hammer")
                && getBrokenBrazier() != null && getBrokenBrazier().distanceTo(Players.getLocal()) <= 3;
    }

    @Override
    public int execute() {
        getBrokenBrazier().interact("Fix");
        setInterrupted(false);
        return -4;
    }

    private ITileObject getBrokenBrazier() {
        return TileObjects.getFirstAt(WintertodtConstants.SW_BRAZIER_COORD, obj -> obj.hasAction("Fix"));
    }

    @Override
    public String toString() {
        return "Repairing brazier";
    }
}
