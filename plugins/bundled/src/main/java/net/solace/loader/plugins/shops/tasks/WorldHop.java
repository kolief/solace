package net.solace.loader.plugins.shops.tasks;

import net.runelite.api.World;
import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.shops.SolaceShopsPlugin;
import net.solace.sdk.game.Worlds;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Shop;
import net.solace.sdk.widgets.Widgets;

public class WorldHop extends PluginTask<SolaceShopsPlugin> {
    public WorldHop(SolaceShopsPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getContext().isShouldHop();
    }

    @Override
    public int execute() {
        if (Shop.isOpen() || Bank.isOpen()) {
            Widgets.closeInterfaces();
            return -1;
        }

        Worlds.hopTo(Worlds.getRandom(this::getRandomWorld));
        return -2;
    }

    private boolean getRandomWorld(World world) {
        boolean p2p;
        if (Worlds.isMembers(Worlds.getCurrent())) {
            p2p = Worlds.isMembers(world);
        } else {
            p2p = !Worlds.isMembers(world);
        }

        return Worlds.isNormal(world) && world.getId() != Worlds.getCurrentId() && p2p && world.getPlayerCount() <= 1500;
    }


    @Override
    public boolean subscribe() {
        return true;
    }
}
