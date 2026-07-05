package net.solace.loader.plugins.birdhouses.tasks;

import net.solace.loader.plugins.birdhouses.SolaceBirdHousesPlugin;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;


public class WaitAtBank extends BirdHouseTask {
    public WaitAtBank(SolaceBirdHousesPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !FOSSIL_ISLAND_CHEST_POINT.dx(-1).equals(Players.getLocal().getWorldLocation());
    }

    @Override
    public int execute() {
        if (Movement.isWalking()) {
            return -1;
        }

        Movement.walkTo(FOSSIL_ISLAND_CHEST_POINT.dx(-1));
        return -3;
    }

    @Override
    public String toString() {
        return "Waiting at bank";
    }
}
