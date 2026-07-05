package net.solace.impl.items;

import net.runelite.api.Item;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.items.IInventory;
import net.solace.impl.domain.items.InventoryItemImpl;
import net.solace.api.widgets.IWidgets;

public class InventoryImpl extends ItemsImpl<IInventoryItem> implements IInventory {
    public InventoryImpl(IWidgets widgets, IClient client) {
        super(widgets, client, InventoryID.INV, IInventoryItem.class, IInventoryItem[].class,
                InventoryImpl::map, 28);
    }

    private static IInventoryItem map(IWidgets widgets, IClient client, Item item, int slot) {
        if (item == null) {
            return null;
        }

        var inventoryWidget = widgets.get(InterfaceID.Inventory.ITEMS);
        if (inventoryWidget == null) {
            inventoryWidget = widgets.get(InterfaceID.BankDepositbox.INVENTORY);
        }

        if (inventoryWidget == null) {
            return null;
        }

        return new InventoryItemImpl(item, slot, inventoryWidget, client);
    }

    @Override
    public int getFreeSlots() {
        return 28 - getAll(x -> true).size();
    }

    @Override
    public boolean isFull() {
        return getFreeSlots() == 0;
    }
}
