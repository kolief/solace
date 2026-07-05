package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;

public class EnterGame extends WintertodtTask {
    public EnterGame(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !isInside();
    }

    @Override
    public int execute() {
        return enterDoor();
    }

    @Override
    public String toString() {
        return "Entering game";
    }
}
