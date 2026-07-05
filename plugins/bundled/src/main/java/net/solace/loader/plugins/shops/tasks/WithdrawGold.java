package net.solace.loader.plugins.shops.tasks;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.plugins.PluginTask;
import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.loader.plugins.shops.BankMode;
import net.solace.loader.plugins.shops.SolaceShopsPlugin;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.DepositBox;
import net.solace.sdk.items.Inventory;

@Slf4j
public class WithdrawGold extends PluginTask<SolaceShopsPlugin> {
    public WithdrawGold(SolaceShopsPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return Inventory.getCount(true, getContext().getCurrencyId()) < getContext().getConfig().minGold();
    }

    @Override
    public int execute() {
        if (getContext().getConfig().bankMode() == BankMode.OFF) {
            throw new PluginStoppedException("Out of gold, stopping plugin.");
        }

        if (Bank.isOpen()) {
            if (Bank.getCount(true, getContext().getCurrencyId()) < getContext().getConfig().minGold()) {
                throw new PluginStoppedException("Out of gold, stopping plugin.");
            }

            Bank.withdraw(getContext().getCurrencyId(), getContext().getConfig().maxGold());
            return -2;
        }

        if (DepositBox.isOpen()) {
            throw new PluginStoppedException("Out of currency, stopping plugin.");
        }

        getContext().openBank();
        return 3000;
    }
}