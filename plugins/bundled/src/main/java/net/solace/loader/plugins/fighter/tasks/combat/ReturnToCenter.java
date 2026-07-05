package net.solace.loader.plugins.fighter.tasks.combat;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;

@Slf4j
public class ReturnToCenter extends FighterTask {
    public ReturnToCenter(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var center = getContext().getCenter();

        return getConfig().returnToCenter()
                && center != null
                && Players.getLocal().distanceTo(center) >= 3
                && getBestTarget() == null;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Returning to center");
        var center = getContext().getCenter();
        var dest = Movement.getDestination();

        if (Movement.isWalking() || (dest != null && dest == center)) {
            return -1;
        }

        Movement.walkTo(center);
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}