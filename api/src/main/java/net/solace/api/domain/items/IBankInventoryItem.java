package net.solace.api.domain.items;

import net.solace.api.domain.items.IItem;

public interface IBankInventoryItem
extends IItem {
    public void deposit(int var1);

    public void depositAll();
}

