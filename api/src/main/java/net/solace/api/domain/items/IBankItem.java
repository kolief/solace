package net.solace.api.domain.items;

import net.solace.api.domain.items.IItem;

public interface IBankItem
extends IItem {
    public void withdraw(int var1);

    public void withdrawAll();
}

