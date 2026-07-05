package net.solace.loader.plugins.cannonballer.tasks;

import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.cannonballer.SolaceCannonballerPlugin;
import net.solace.sdk.game.Game;

public class Await extends PluginTask<SolaceCannonballerPlugin> {
    public Await(SolaceCannonballerPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getContext().getSelectedBank() == null
                || getContext().getSelectedFurnace() == null
                || !Game.isLoggedIn();
    }

    @Override
    public int execute() {
        return 1000;
    }
}
