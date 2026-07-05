package net.solace.impl.items;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.game.IClient;
import net.solace.api.items.IEquipment;
import net.solace.impl.domain.items.EquipmentItemImpl;
import net.solace.api.domain.items.IItem;
import net.solace.api.widgets.EquipmentSlot;
import net.solace.api.widgets.IWidgets;

@Slf4j
public class EquipmentImpl extends ItemsImpl<IItem> implements IEquipment {
    public EquipmentImpl(IWidgets widgets, IClient client) {
        super(widgets, client, InventoryID.WORN, IItem.class, IItem[].class, EquipmentImpl::map, 14);
    }

    private static EquipmentItemImpl map(IWidgets widgets, IClient client,
                                         Item item, int slot) {
        if (item == null) {
            return null;
        }

        var equipmentSlot = EquipmentSlot.fromSlotIndex(slot);
        if (equipmentSlot == null) {
            return null;
        }

        var equipmentWidget = widgets.get(equipmentSlot.getInterfaceAddress());
        if (equipmentWidget == null) {
            return null;
        }

        return new EquipmentItemImpl(item, slot, equipmentWidget, client);
    }

    @Override
    public IItem fromSlot(EquipmentSlot slot) {
        return getFirst(x -> x.getWidget().getId() == slot.getInterfaceAddress().getPackedId());
    }
}
