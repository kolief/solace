package net.solace.loader.plugins.shops.tasks;

import net.runelite.api.GameState;
import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.shops.SolaceShopsPlugin;
import net.solace.sdk.game.Game;

public class Await extends PluginTask<SolaceShopsPlugin> {
    public Await(SolaceShopsPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getContext().getSelectedShop() == null
                || Game.getState() != GameState.LOGGED_IN;
    }

    @Override
    public int execute() {
        return 1000;
    }
}
