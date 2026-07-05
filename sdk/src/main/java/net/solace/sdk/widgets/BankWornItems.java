package net.solace.sdk.widgets;

import net.solace.api.Static;
import net.solace.api.widgets.BankWornItem;
import net.solace.api.widgets.EquipmentSlot;
import net.solace.api.widgets.IBankWornItems;

public class BankWornItems {
    private static final IBankWornItems BANK_WORN_ITEMS = Static.getBankWornItems();

    public static boolean isOpen() {
        return BANK_WORN_ITEMS.isOpen();
    }

    public static void open() {
        BANK_WORN_ITEMS.open();
    }

    public static void close() {
        BANK_WORN_ITEMS.close();
    }

    public static BankWornItem getHead() {
        return BANK_WORN_ITEMS.getHead();
    }

    public static BankWornItem getCape() {
        return BANK_WORN_ITEMS.getCape();
    }

    public static BankWornItem getAmulet() {
        return BANK_WORN_ITEMS.getAmulet();
    }

    public static BankWornItem getWeapon() {
        return BANK_WORN_ITEMS.getWeapon();
    }

    public static BankWornItem getBody() {
        return BANK_WORN_ITEMS.getBody();
    }

    public static BankWornItem getShield() {
        return BANK_WORN_ITEMS.getShield();
    }

    public static BankWornItem getLegs() {
        return BANK_WORN_ITEMS.getLegs();
    }

    public static BankWornItem getGloves() {
        return BANK_WORN_ITEMS.getGloves();
    }

    public static BankWornItem getBoots() {
        return BANK_WORN_ITEMS.getBoots();
    }

    public static BankWornItem getRing() {
        return BANK_WORN_ITEMS.getRing();
    }

    public static BankWornItem getAmmo() {
        return BANK_WORN_ITEMS.getAmmo();
    }

    public static BankWornItem fromSlot(EquipmentSlot slot) {
        return BANK_WORN_ITEMS.fromSlot(slot);
    }
}

