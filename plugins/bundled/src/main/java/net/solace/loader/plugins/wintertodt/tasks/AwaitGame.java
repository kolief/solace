package net.solace.loader.plugins.wintertodt.tasks;

import net.runelite.api.coords.WorldPoint;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;

public class AwaitGame extends WintertodtTask {
    public AwaitGame(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return isInside() && !isGameStarted();
    }

    @Override
    public int execute() {
        if (Movement.isWalking()) {
            return -1;
        }

        WorldPoint idleLocation = WintertodtConstants.SW_BRAZIER_COORD.dy(-2);
        if (idleLocation.distanceTo(Players.getLocal().getWorldLocation()) > 3) {
            Movement.walkTo(idleLocation);
            return -1;
        }

        return 100;
    }
}
