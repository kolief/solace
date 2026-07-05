package net.solace.api.widgets;

import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.EquipmentSlot;

public interface BankWornItem {
    public IWidget getWidget();

    public int getId();

    public int getQuantity();

    public EquipmentSlot getSlot();

    public void deposit();

    public void unequip();
}

