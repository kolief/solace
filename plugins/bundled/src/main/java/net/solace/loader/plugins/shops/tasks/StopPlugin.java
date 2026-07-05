package net.solace.loader.plugins.shops.tasks;

import net.solace.api.plugins.PluginTask;
import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.loader.plugins.shops.SolaceShopsPlugin;

public class StopPlugin extends PluginTask<SolaceShopsPlugin> {
    public StopPlugin(SolaceShopsPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var ids = getContext().getItemIds();
        return getContext().getConfig().maxBuy() > 0
                && ids.stream().allMatch(x -> getContext().getPurchasedItems().getOrDefault(x, 0) >= getContext().getConfig().maxBuy());
    }

    @Override
    public int execute() {
        throw new PluginStoppedException("All items purchased, stopping plugin.");
    }
}
