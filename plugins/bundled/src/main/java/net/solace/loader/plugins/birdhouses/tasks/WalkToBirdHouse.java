package net.solace.loader.plugins.birdhouses.tasks;

import net.solace.loader.plugins.birdhouses.SolaceBirdHousesPlugin;
import net.solace.loader.plugins.birdhouses.model.BirdHouse;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;

public class WalkToBirdHouse extends BirdHouseTask {
    public WalkToBirdHouse(SolaceBirdHousesPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getNextBirdHouse()
                .map(BirdHouse::getWorldPoint)
                .map(point -> Players.getLocal().distanceTo(point) > 10)
                .orElse(false);
    }

    @Override
    public int execute() {
        if (!Movement.isWalking()) {
            getNextBirdHouse().ifPresent(house -> Movement.walkTo(house.getWorldPoint()));
        }

        return -1;
    }

    @Override
    public String toString() {
        return "Walking to birdhouse";
    }
}
