package net.solace.sdk.items;

import net.solace.api.Static;
import net.solace.api.items.IDepositBox;

public class DepositBox {
    private static final IDepositBox DEPOSIT_BOX = Static.getDepositBox();

    public static void depositInventory() {
        DEPOSIT_BOX.depositInventory();
    }

    public static void depositEquipment() {
        DEPOSIT_BOX.depositEquipment();
    }

    public static void depositLootingBag() {
        DEPOSIT_BOX.depositLootingBag();
    }

    public static void selectQuantityOne() {
        DEPOSIT_BOX.selectQuantityOne();
    }

    public static void selectQuantityFive() {
        DEPOSIT_BOX.selectQuantityFive();
    }

    public static void selectQuantityTen() {
        DEPOSIT_BOX.selectQuantityTen();
    }

    public static void selectQuantityX() {
        DEPOSIT_BOX.selectQuantityX();
    }

    public static void selectQuantityAll() {
        DEPOSIT_BOX.selectQuantityAll();
    }

    public static boolean isOpen() {
        return DEPOSIT_BOX.isOpen();
    }

    public static void close() {
        DEPOSIT_BOX.close();
    }
}

