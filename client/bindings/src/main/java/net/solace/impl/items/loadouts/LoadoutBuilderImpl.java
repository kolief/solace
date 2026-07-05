package net.solace.impl.items.loadouts;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutBuilder;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.items.loadouts.LoadoutManager;

import java.util.Arrays;
import java.util.Collection;

@Slf4j
public class LoadoutBuilderImpl implements LoadoutBuilder {
    @Getter
    private final LoadoutItem[] inventory;
    @Getter
    private final LoadoutItem[] equipment;
    @Getter
    private final LoadoutItem[] runePouch;

    private final LoadoutManager loadoutManager;

    private boolean inventoryDisabled;
    private boolean equipmentDisabled;
    private boolean runePouchDisabled;

    LoadoutBuilderImpl(LoadoutManager defaultLoadoutManager) {
        this.loadoutManager = defaultLoadoutManager;

        this.inventory = new LoadoutItem[28];
        this.equipment = new LoadoutItem[14];
        this.runePouch = new LoadoutItem[4];
    }

    @Override
    public LoadoutBuilder item(
            int id,
            int quantity,
            boolean stackable,
            boolean noted,
            LoadoutItem.Type type,
            int slot
    ) {
        return item(id, quantity, quantity, stackable, noted, type, slot);
    }

    @Override
    public LoadoutBuilder item(
            int id,
            int quantity,
            int maxQuantity,
            boolean stackable,
            boolean noted,
            LoadoutItem.Type type,
            int slot
    ) {
        switch (type) {
            case INVENTORY:
                addInventoryItem(id, quantity, maxQuantity, stackable, noted, slot);
                break;
            case EQUIPMENT:
                addEquipmentItem(id, quantity, maxQuantity, stackable, slot);
                break;
            case RUNE_POUCH:
                addRunePouchItem(id, quantity, maxQuantity, slot);
                break;
        }

        return this;
    }

    @Override
    public LoadoutBuilder item(LoadoutItem item) {
        if (item == null) {
            return this;
        }
        return item(item.getId(), item.getQuantity(), item.getMaxQuantity(), item.isStackable(), item.isNoted(),
                item.getType(), item.getSlot());
    }

    @Override
    public LoadoutBuilder items(LoadoutItem... items) {
        Arrays.stream(items).forEach(this::item);
        return this;
    }

    @Override
    public LoadoutBuilder items(Collection<LoadoutItem> items) {
        items.forEach(this::item);
        return this;
    }

    @Override
    public LoadoutBuilder item(
            int id,
            int quantity,
            boolean isStackable,
            LoadoutItem.Type type,
            int slot
    ) {
        return item(id, quantity, quantity, isStackable, type, slot);
    }

    @Override
    public LoadoutBuilder item(
            int id,
            int quantity,
            int maxQuantity,
            boolean isStackable,
            LoadoutItem.Type type,
            int slot
    ) {
        return item(id, quantity, maxQuantity, isStackable, false, type, slot);
    }

    @Override
    public LoadoutBuilder item(int id, int quantity, LoadoutItem.Type type, int slot) {
        return item(id, quantity, quantity, type, slot);
    }

    @Override
    public LoadoutBuilder item(int id, int quantity, int maxQuantity, LoadoutItem.Type type, int slot) {
        return item(id, quantity, maxQuantity, false, false, type, slot);
    }

    @Override
    public LoadoutBuilder item(int id, LoadoutItem.Type type, int slot) {
        return item(id, 1, false, false, type, slot);
    }

    @Override
    public LoadoutBuilder item(int id, boolean isStackable, LoadoutItem.Type type, int slot) {
        return item(id, 1, isStackable, false, type, slot);
    }

    @Override
    public LoadoutBuilder inventoryItem(int id, int quantity, int maxQuantity, boolean isStackable, boolean isNoted, int slot) {
        return item(id, quantity, maxQuantity, isStackable, isNoted, LoadoutItem.Type.INVENTORY, slot);
    }

    @Override
    public LoadoutBuilder inventoryItem(int id, int quantity, boolean isStackable, boolean isNoted, int slot) {
        return inventoryItem(id, quantity, quantity, isStackable, isNoted, slot);
    }

    @Override
    public LoadoutBuilder inventoryItem(int id, int quantity, int maxQuantity, boolean isStackable, int slot) {
        return inventoryItem(id, quantity, maxQuantity, isStackable, false, slot);
    }

    @Override
    public LoadoutBuilder inventoryItem(int id, int quantity, int maxQuantity, int slot) {
        return inventoryItem(id, quantity, maxQuantity, false, slot);
    }

    @Override
    public LoadoutBuilder inventoryItem(int id, int quantity, boolean isStackable, int slot) {
        return inventoryItem(id, quantity, quantity, isStackable, slot);
    }

    @Override
    public LoadoutBuilder inventoryItem(int id, int quantity, int slot) {
        return inventoryItem(id, quantity, quantity, slot);
    }

    @Override
    public LoadoutBuilder inventoryItem(int id, int slot) {
        return inventoryItem(id, 1, slot);
    }

    @Override
    public LoadoutBuilder equipmentItem(int id, int quantity, int maxQuantity, boolean isStackable, int slot) {
        return item(id, quantity, maxQuantity, isStackable, LoadoutItem.Type.EQUIPMENT, slot);
    }

    @Override
    public LoadoutBuilder equipmentItem(int id, int quantity, boolean isStackable, int slot) {
        return equipmentItem(id, quantity, quantity, isStackable, slot);
    }

    @Override
    public LoadoutBuilder equipmentItem(int id, int quantity, int maxQuantity, int slot) {
        return equipmentItem(id, quantity, maxQuantity, false, slot);
    }

    @Override
    public LoadoutBuilder equipmentItem(int id, int quantity, int slot) {
        return equipmentItem(id, quantity, quantity, slot);
    }

    @Override
    public LoadoutBuilder equipmentItem(int id, int slot) {
        return equipmentItem(id, 1, slot);
    }

    @Override
    public LoadoutBuilder runePouchItem(int id, int quantity, int slot) {
        return runePouchItem(id, quantity, quantity, slot);
    }

    @Override
    public LoadoutBuilder runePouchItem(int id, int quantity, int maxQuantity, int slot) {
        return item(id, quantity, maxQuantity, true, LoadoutItem.Type.RUNE_POUCH, slot);
    }

    @Override
    public LoadoutBuilder disableInventory() {
        this.inventoryDisabled = true;
        return this;
    }

    @Override
    public LoadoutBuilder disableEquipment() {
        this.equipmentDisabled = true;
        return this;
    }

    @Override
    public LoadoutBuilder disableRunePouch() {
        this.runePouchDisabled = true;
        return this;
    }

    @Override
    public Loadout build() {
        var loadout = new LoadoutImpl(inventory, equipment, runePouch,
                inventoryDisabled, equipmentDisabled, runePouchDisabled, loadoutManager);
        loadout.shiftLoadoutItems();
        return loadout;
    }

    private void addInventoryItem(int id, int quantity, int maxQuantity, boolean isStackable, boolean isNoted, int slot) {
        if (slot < 0 || slot >= 28) {
            throw new IllegalArgumentException("Invalid inventory slot: " + slot);
        }

        this.inventory[slot] = new LoadoutItem(id, quantity, maxQuantity, isStackable, isNoted, LoadoutItem.Type.INVENTORY, slot);
    }

    private void addEquipmentItem(int id, int quantity, int maxQuantity, boolean isStackable, int slot) {
        if (slot < 0 || slot >= 14) {
            throw new IllegalArgumentException("Invalid equipment slot: " + slot);
        }

        this.equipment[slot] = new LoadoutItem(id, quantity, maxQuantity, isStackable, false, LoadoutItem.Type.EQUIPMENT, slot);
    }

    private void addRunePouchItem(int id, int quantity, int maxQuantity, int slot) {
        if (slot < 0 || slot >= 4) {
            throw new IllegalArgumentException("Invalid rune pouch slot: " + slot);
        }

        this.runePouch[slot] = new LoadoutItem(id, quantity, maxQuantity, true, false, LoadoutItem.Type.RUNE_POUCH, slot);
    }
}
