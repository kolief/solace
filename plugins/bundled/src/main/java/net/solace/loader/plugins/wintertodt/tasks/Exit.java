package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.sdk.items.Inventory;

public class Exit extends WintertodtTask {
    public Exit(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return isInside() && !isGameStarted() && (!getRequiredItems().isEmpty() || Inventory.isFull());
    }

    @Override
    public int execute() {
        return enterDoor();
    }

    @Override
    public String toString() {
        return "Exiting";
    }
}
