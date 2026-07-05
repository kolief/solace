package net.solace.loader.plugins.shops.tasks;

import net.solace.api.domain.items.IItem;
import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.shops.SolaceShopsPlugin;
import net.solace.sdk.items.Inventory;

import java.util.function.Predicate;

public class OpenRunePacks extends PluginTask<SolaceShopsPlugin> {
    private static final Predicate<IItem> PACK_PREDICATE = item ->
            item.getName().endsWith(" pack") && item.hasAction("Open");

    public OpenRunePacks(SolaceShopsPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getContext().shouldOpenPacks()
                && (Inventory.isFull() || getContext().isShouldHop())
                && Inventory.contains(PACK_PREDICATE);
    }

    @Override
    public int execute() {
        Inventory.getAll(PACK_PREDICATE)
                .forEach(item -> item.interact("Open"));
        return 1000;
    }
}
