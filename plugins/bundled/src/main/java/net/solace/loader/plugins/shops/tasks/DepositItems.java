package net.solace.loader.plugins.shops.tasks;

import net.solace.api.domain.items.IItem;
import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.shops.BankMode;
import net.solace.loader.plugins.shops.SolaceShopsPlugin;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.DepositBox;
import net.solace.sdk.items.Inventory;

public class DepositItems extends PluginTask<SolaceShopsPlugin> {
    public DepositItems(SolaceShopsPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return Inventory.isFull()
               && getContext().getConfig().bankMode() != BankMode.OFF;
    }

    @Override
    public int execute() {
        if (getContext().getConfig().bankMode() == BankMode.BANK) {
            return bank();
        }

        return -1;
    }

    private int bank() {
        if (Bank.isOpen()) {
            Inventory.getAll(item -> item.getId() != getContext().getCurrencyId()).stream()
                    .map(IItem::getId)
                    .distinct()
                    .forEach(Bank::depositAll);
            return -1;
        }

        if (DepositBox.isOpen()) {
            var itemToDeposit = Inventory.getFirst(x -> x.getId() != getContext().getCurrencyId());
            if (itemToDeposit != null) {
                itemToDeposit.interact("Deposit-All");
                return -1;
            }
        }

        getContext().openBank();
        return -1;
    }
}