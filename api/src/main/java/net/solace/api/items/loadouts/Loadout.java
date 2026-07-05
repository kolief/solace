package net.solace.api.items.loadouts;

import java.util.List;
import javax.annotation.Nullable;
import net.runelite.api.coords.WorldArea;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.api.plugins.config.ConfigManager;

public interface Loadout {
    public LoadoutItem[] getInventory();

    public LoadoutItem[] getEquipment();

    public LoadoutItem[] getRunePouch();

    public List<LoadoutItem> getItems();

    public void fetchFromBank(WorldArea var1);

    public void fetchEquipmentFromBank(WorldArea var1);

    public void fetchInventoryFromBank(WorldArea var1);

    public void fetchRunePouchFromBank(WorldArea var1);

    default public void fetchFromBank(BankLocation bankLocation) {
        this.fetchFromBank(bankLocation.getArea());
    }

    default public void fetchFromBank() {
        this.fetchFromBank(BankLocation.getNearest());
    }

    default public void fetchEquipmentFromBank(BankLocation bankLocation) {
        this.fetchEquipmentFromBank(bankLocation.getArea());
    }

    default public void fetchEquipmentFromBank() {
        this.fetchEquipmentFromBank(BankLocation.getNearest());
    }

    default public void fetchInventoryFromBank(BankLocation bankLocation) {
        this.fetchInventoryFromBank(bankLocation.getArea());
    }

    default public void fetchInventoryFromBank() {
        this.fetchInventoryFromBank(BankLocation.getNearest());
    }

    default public void fetchRunePouchFromBank(BankLocation bankLocation) {
        this.fetchRunePouchFromBank(bankLocation.getArea());
    }

    default public void fetchRunePouchFromBank() {
        this.fetchRunePouchFromBank(BankLocation.getNearest());
    }

    public boolean isLoadoutCompleted();

    public boolean isEquipmentCompleted();

    public boolean isInventoryCompleted();

    public boolean isRunePouchCompleted();

    public boolean isInventoryDisabled();

    public boolean isEquipmentDisabled();

    public boolean isRunePouchDisabled();

    public boolean hasRunePouch();

    @Nullable
    public LoadoutItem getEquipmentItem(int var1);

    @Nullable
    public LoadoutItem getEquipmentItemFromSlot(int var1);

    public void save(ConfigManager var1, String var2, String var3);
}

