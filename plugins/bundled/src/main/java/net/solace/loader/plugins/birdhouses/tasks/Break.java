package net.solace.loader.plugins.birdhouses.tasks;

import net.solace.loader.plugins.birdhouses.SolaceBirdHousesPlugin;
import net.solace.loader.plugins.birdhouses.SolaceBirdHousesConfig;
import net.solace.sdk.game.Game;

import javax.inject.Inject;

public class Break extends BirdHouseTask {
    @Inject
    private SolaceBirdHousesConfig config;

    public Break(SolaceBirdHousesPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return config.logout();
    }

    @Override
    public int execute() {
        if (Game.isLoggedIn()) {
            Game.logout();
        }

        return -1;
    }

    @Override
    public boolean inject() {
        return true;
    }

    @Override
    public String toString() {
        return "Breaking";
    }
}
