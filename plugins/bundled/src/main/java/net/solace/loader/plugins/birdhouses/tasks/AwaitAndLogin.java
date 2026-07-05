package net.solace.loader.plugins.birdhouses.tasks;

import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.loader.plugins.birdhouses.SolaceBirdHousesPlugin;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.GameThread;

public class AwaitAndLogin extends BirdHouseTask {
    public AwaitAndLogin(SolaceBirdHousesPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !Game.isLoggedIn();
    }

    @Override
    public int execute() {
        if (getAvailableBirdHouses().size() != 4) {
            return -1;
        }

        int sleep = GameThread.invokeAndWait(() -> getLoginEvent().login());
        if (sleep == -1000) {
            throw new PluginStoppedException();
        }

        return sleep;
    }

    @Override
    public String toString() {
        return "Waiting or logging in";
    }
}
