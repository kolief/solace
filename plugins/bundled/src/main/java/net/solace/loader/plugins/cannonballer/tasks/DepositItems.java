package net.solace.loader.plugins.cannonballer.tasks;

import net.runelite.api.gameval.ItemID;
import net.solace.api.domain.items.IItem;
import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.cannonballer.SolaceCannonballerPlugin;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Inventory;

import java.util.List;
import java.util.stream.Collectors;

public class DepositItems extends PluginTask<SolaceCannonballerPlugin> {
    public DepositItems(SolaceCannonballerPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !getItemsToDeposit().isEmpty();
    }

    @Override
    public int execute() {
        if (!Bank.isOpen()) {
            return getContext().openBank();
        }

        List<Integer> itemsToWithdraw = getItemsToDeposit();
        for (Integer id : itemsToWithdraw) {
            Bank.depositAll(id);
        }

        return -2;
    }

    private List<Integer> getItemsToDeposit() {
        List<Integer> itemsToDeposit = getContext().getInventorySetup().values().stream()
                .filter(integer -> integer < 0)
                .map(Math::abs)
                .collect(Collectors.toList());

        if (!itemsToDeposit.isEmpty() || Inventory.isFull()) {
            itemsToDeposit.addAll(Inventory.getAll(this::unneededItem).stream()
                    .map(IItem::getId)
                    .filter(id -> id != ItemID.MCANNONBALL)
                    .collect(Collectors.toList()));
        }

        return itemsToDeposit;
    }

    private boolean unneededItem(IItem item) {
        return !getContext().getInventorySetup().containsKey(item.getId());
    }
}
