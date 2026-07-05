package net.solace.impl.widgets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.BankWornItem;
import net.solace.api.widgets.EquipmentSlot;

@RequiredArgsConstructor
@Getter
public class BankWornItemImpl implements BankWornItem {
    private final IWidget widget;
    private final EquipmentSlot slot;

    @Override
    public int getId() {
        var child = widget.getChild(1);
        if (child == null) {
            return -1;
        }

        return child.getItemId();
    }

    @Override
    public int getQuantity() {
        var child = widget.getChild(1);
        if (child == null) {
            return -1;
        }

        return child.getItemQuantity();
    }

    @Override
    public void deposit() {
        widget.interact("Bank");
    }

    @Override
    public void unequip() {
        widget.interact("Remove");
    }
}
