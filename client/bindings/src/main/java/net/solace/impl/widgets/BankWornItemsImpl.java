package net.solace.impl.widgets;

import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.InterfaceID;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.BankWornItem;
import net.solace.api.widgets.EquipmentSlot;
import net.solace.api.widgets.IBankWornItems;
import net.solace.api.widgets.IWidgets;

import javax.annotation.Nullable;

@RequiredArgsConstructor
public class BankWornItemsImpl implements IBankWornItems {
    private static final int WORN_ITEMS_BUTTON_CHILD_ID = 126;
    private static final int HEAD_SLOT_CHILD_ID = 87;

    private final IWidgets widgets;

    @Override
    public boolean isOpen() {
        return widgets.isVisible(InterfaceID.BANKMAIN, HEAD_SLOT_CHILD_ID);
    }

    @Override
    public void open() {
        if (!isOpen()) {
            var button = getButton();
            if (button != null) {
                button.interact("Show worn items");
            }
        }
    }

    @Override
    public void close() {
        if (isOpen()) {
            var button = getButton();
            if (button != null) {
                button.interact("Hide worn items");
            }
        }
    }

    @Override
    @Nullable
    public BankWornItem getHead() {
        return fromSlot(EquipmentSlot.HEAD);
    }

    @Override
    @Nullable
    public BankWornItem getCape() {
        return fromSlot(EquipmentSlot.CAPE);
    }

    @Override
    @Nullable
    public BankWornItem getAmulet() {
        return fromSlot(EquipmentSlot.AMULET);
    }

    @Override
    @Nullable
    public BankWornItem getWeapon() {
        return fromSlot(EquipmentSlot.WEAPON);
    }

    @Override
    @Nullable
    public BankWornItem getBody() {
        return fromSlot(EquipmentSlot.BODY);
    }

    @Override
    @Nullable
    public BankWornItem getShield() {
        return fromSlot(EquipmentSlot.SHIELD);
    }

    @Override
    @Nullable
    public BankWornItem getLegs() {
        return fromSlot(EquipmentSlot.LEGS);
    }

    @Override
    @Nullable
    public BankWornItem getGloves() {
        return fromSlot(EquipmentSlot.GLOVES);
    }

    @Override
    @Nullable
    public BankWornItem getBoots() {
        return fromSlot(EquipmentSlot.BOOTS);
    }

    @Override
    @Nullable
    public BankWornItem getRing() {
        return fromSlot(EquipmentSlot.RING);
    }

    @Override
    @Nullable
    public BankWornItem getAmmo() {
        return fromSlot(EquipmentSlot.AMMO);
    }

    @Override
    @Nullable
    public BankWornItem fromSlot(EquipmentSlot slot) {
        var widgetSlot = getWidgetSlot(slot);
        var widget = widgets.get(InterfaceID.BANKMAIN, HEAD_SLOT_CHILD_ID + widgetSlot);
        if (widget == null) {
            return null;
        }

        return new BankWornItemImpl(widget, slot);
    }

    private int getWidgetSlot(EquipmentSlot equipmentSlot) {
        switch (equipmentSlot) {
            case LEGS:
                return 6;
            case GLOVES:
                return 7;
            case BOOTS:
                return 8;
            case RING:
                return 9;
            case AMMO:
                return 10;
            default:
                return equipmentSlot.getSlot();
        }
    }

    private IWidget getButton() {
        return widgets.get(InterfaceID.BANKMAIN, WORN_ITEMS_BUTTON_CHILD_ID);
    }
}
