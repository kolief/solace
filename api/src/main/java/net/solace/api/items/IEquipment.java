package net.solace.api.items;

import net.solace.api.domain.items.IItem;
import net.solace.api.items.IItems;
import net.solace.api.widgets.EquipmentSlot;

public interface IEquipment
extends IItems<IItem> {
    public IItem fromSlot(EquipmentSlot var1);
}

