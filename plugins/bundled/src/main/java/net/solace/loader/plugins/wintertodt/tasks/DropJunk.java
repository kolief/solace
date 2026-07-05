package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.sdk.items.Inventory;

public class DropJunk extends WintertodtTask {
    public DropJunk(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return Inventory.contains("Jug");
    }

    @Override
    public int execute() {
        Inventory.getFirst("Jug").drop();
        return -1;
    }

    @Override
    public String toString() {
        return "Dropping junk";
    }
}
