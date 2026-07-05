package net.solace.api.widgets;

import javax.annotation.Nullable;
import net.solace.api.widgets.BankWornItem;
import net.solace.api.widgets.EquipmentSlot;

public interface IBankWornItems {
    public boolean isOpen();

    public void open();

    public void close();

    @Nullable
    public BankWornItem getHead();

    @Nullable
    public BankWornItem getCape();

    @Nullable
    public BankWornItem getAmulet();

    @Nullable
    public BankWornItem getWeapon();

    @Nullable
    public BankWornItem getBody();

    @Nullable
    public BankWornItem getShield();

    @Nullable
    public BankWornItem getLegs();

    @Nullable
    public BankWornItem getGloves();

    @Nullable
    public BankWornItem getBoots();

    @Nullable
    public BankWornItem getRing();

    @Nullable
    public BankWornItem getAmmo();

    @Nullable
    public BankWornItem fromSlot(EquipmentSlot var1);
}

