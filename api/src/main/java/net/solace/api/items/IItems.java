package net.solace.api.items;

import net.solace.api.domain.items.IItem;
import net.solace.api.items.ItemProvider;

public interface IItems<T extends IItem>
extends ItemProvider<T> {
}

