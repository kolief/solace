package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;

public class Hide extends WintertodtTask {

    public Hide(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getFoodCount() == 0 && getWarmthPercent() <= getConfig().eatHp()
                && isGameStarted() && isInside() && getPoints() > 0;
    }

    @Override
    public int execute() {
        if (WintertodtConstants.SAFE_SPOT.distanceTo(Players.getLocal().getWorldLocation()) > 5) {
            Movement.walkTo(WintertodtConstants.SAFE_SPOT);
        }

        return -3;
    }

    @Override
    public String toString() {
        return "Walking to safety";
    }
}
