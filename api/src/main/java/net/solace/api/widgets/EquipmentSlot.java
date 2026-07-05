package net.solace.api.widgets;

import net.solace.api.widgets.InterfaceAddress;

public enum EquipmentSlot {
    HEAD(0, 25362447, 786500),
    CAPE(1, 25362448, 786501),
    AMULET(2, 25362449, 786502),
    WEAPON(3, 25362450, 786503),
    BODY(4, 25362451, 786504),
    SHIELD(5, 25362452, 786505),
    LEGS(7, 25362453, 786506),
    GLOVES(9, 25362454, 786507),
    BOOTS(10, 25362455, 786508),
    RING(12, 25362456, 786509),
    AMMO(13, 25362457, 786510);

    private final int slot;
    private final int equipmentComponent;
    private final int bankComponent;

    @Deprecated(forRemoval=true)
    public InterfaceAddress getInterfaceAddress() {
        return new InterfaceAddress(this.equipmentComponent);
    }

    public static EquipmentSlot fromSlotIndex(int slot) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getSlot() != slot) continue;
            return equipmentSlot;
        }
        return null;
    }

    private EquipmentSlot(int slot, int equipmentComponent, int bankComponent) {
        this.slot = slot;
        this.equipmentComponent = equipmentComponent;
        this.bankComponent = bankComponent;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getEquipmentComponent() {
        return this.equipmentComponent;
    }

    public int getBankComponent() {
        return this.bankComponent;
    }
}

