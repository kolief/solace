package net.solace.impl.domain.items;

import net.runelite.api.Item;
import net.runelite.api.MenuAction;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IBankItem;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;

public class BankItemImpl extends ItemImpl implements IBankItem {
    public BankItemImpl(Item wrapped, int slot, IWidget widget, IClient client) {
        super(wrapped, slot, widget, client, InventoryID.BANK);
    }

    @Override
    public void withdraw(int amount) {
        var action = getBankAction(amount, true);
        var actionIndex = getActionIndex(action);
        interact(actionIndex);
    }

    @Override
    public void withdrawAll() {
        var action = getBankAction(0, true);
        var actionIndex = getActionIndex(action);
        interact(actionIndex);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, int actionIndex) {
        return MENU_FACTORY.bankItem(getId(), getSlot())
                .interactMethod(interactMethod)
                .actionIndex(actionIndex)
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, MenuAction opcode) {
        return MENU_FACTORY.bankItem(getId(), getSlot())
                .interactMethod(interactMethod)
                .opcode(opcode)
                .build(getClickPoint());
    }
}
