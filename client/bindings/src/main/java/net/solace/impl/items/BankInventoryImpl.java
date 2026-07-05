package net.solace.impl.items;

import net.runelite.api.Item;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.game.IClient;
import net.solace.api.items.IBankInventory;
import net.solace.impl.domain.items.BankInventoryItemImpl;
import net.solace.api.domain.items.IBankInventoryItem;
import net.solace.api.widgets.IWidgets;

public class BankInventoryImpl extends ItemsImpl<IBankInventoryItem> implements IBankInventory {
    public BankInventoryImpl(IWidgets widgets, IClient client) {
        super(widgets, client, InventoryID.INV, IBankInventoryItem.class, IBankInventoryItem[].class,
                BankInventoryImpl::map, 28);
    }

    private static BankInventoryItemImpl map(IWidgets widgets, IClient client,
                                             Item item, int slot) {
        if (item == null) {
            return null;
        }

        var bankWidget = widgets.get(InterfaceID.Bankside.ITEMS);
        if (bankWidget == null) {
            return null;
        }

        return new BankInventoryItemImpl(item, slot, bankWidget, client);
    }
}
