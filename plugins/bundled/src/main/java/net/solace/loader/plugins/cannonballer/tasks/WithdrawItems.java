package net.solace.loader.plugins.cannonballer.tasks;

import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.cannonballer.SolaceCannonballerPlugin;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Inventory;

import java.util.HashMap;
import java.util.Map;

public class WithdrawItems extends PluginTask<SolaceCannonballerPlugin> {
    public WithdrawItems(SolaceCannonballerPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !getItemsToWithdraw().isEmpty();
    }

    @Override
    public int execute() {
        if (!Bank.isOpen()) {
            return getContext().openBank();
        }

        Map<Integer, Integer> itemsToWithdraw = getItemsToWithdraw();
        for (Map.Entry<Integer, Integer> item : itemsToWithdraw.entrySet()) {
            Bank.withdraw(item.getKey(), item.getValue());
        }

        return -2;
    }

    private Map<Integer, Integer> getItemsToWithdraw() {
        Map<Integer, Integer> itemsToWithdraw = new HashMap<>();

        for (Map.Entry<Integer, Integer> item : getContext().getInventorySetup().entrySet()) {
            if (!Inventory.contains(item.getKey())) {
                itemsToWithdraw.put(item.getKey(), item.getValue());
            }
        }

        return itemsToWithdraw;
    }
}
