package net.solace.api.items;

import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.items.IItems;

public interface IInventory
extends IItems<IInventoryItem> {
    public int getFreeSlots();

    public boolean isFull();
}

