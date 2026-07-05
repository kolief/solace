package net.solace.api.items;

public interface IDepositBox {
    public void depositInventory();

    public void depositEquipment();

    public void depositLootingBag();

    public void selectQuantityOne();

    public void selectQuantityFive();

    public void selectQuantityTen();

    public void selectQuantityX();

    public void selectQuantityAll();

    public boolean isOpen();

    public void close();
}

