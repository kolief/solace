package net.solace.impl.items.loadouts;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.items.loadouts.LoadoutManager;
import net.solace.api.plugins.config.ConfigManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.solace.impl.items.loadouts.LoadoutManagerImpl.RUNE_POUCH_IDS;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class LoadoutImpl implements Loadout {
    @SerializedName("inventory")
    private final LoadoutItem[] inventory;
    @SerializedName("equipment")
    private final LoadoutItem[] equipment;
    @SerializedName("runePouch")
    private final LoadoutItem[] runePouch;
    @SerializedName("inventoryDisabled")
    private final boolean inventoryDisabled;
    @SerializedName("equipmentDisabled")
    private final boolean equipmentDisabled;
    @SerializedName("runePouchDisabled")
    private final boolean runePouchDisabled;
    private final transient LoadoutManager loadoutManager;

    public List<LoadoutItem> getItems() {
        return Stream.of(
                        Objects.requireNonNullElse(inventory, new LoadoutItem[0]),
                        Objects.requireNonNullElse(equipment, new LoadoutItem[0]),
                        Objects.requireNonNullElse(runePouch, new LoadoutItem[0])
                )
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void fetchFromBank() {
        fetchFromBank(net.solace.api.movement.pathfinder.model.BankLocation.getNearest().getArea());
    }

    @Override
    public void fetchFromBank(WorldArea worldArea) {
        loadoutManager.fetchFromBank(this, worldArea);
    }

    @Override
    public void fetchEquipmentFromBank(WorldArea worldArea) {
        loadoutManager.fetchEquipment(this, worldArea);
    }

    @Override
    public void fetchInventoryFromBank(WorldArea worldArea) {
        loadoutManager.fetchInventory(this, worldArea);
    }

    @Override
    public void fetchRunePouchFromBank(WorldArea worldArea) {
        loadoutManager.fetchRunePouch(this, worldArea);
    }

    @Override
    public boolean isLoadoutCompleted() {
        return loadoutManager.isLoadoutCompleted(this);
    }

    @Override
    public boolean isEquipmentCompleted() {
        return loadoutManager.isEquipmentCompleted(this);
    }

    @Override
    public boolean isInventoryCompleted() {
        return loadoutManager.isInventoryCompleted(this);
    }

    @Override
    public boolean isRunePouchCompleted() {
        return loadoutManager.isRunePouchCompleted(this);
    }

    @Override
    public boolean hasRunePouch() {
        for (var loadoutItem : inventory) {
            for (var runePouchId : RUNE_POUCH_IDS) {
                if (loadoutItem != null && loadoutItem.getId() == runePouchId) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    @Nullable
    public LoadoutItem getEquipmentItem(int id) {
        return Arrays.stream(equipment)
                .filter(Objects::nonNull)
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    @Nullable
    public LoadoutItem getEquipmentItemFromSlot(int slot) {
        return Arrays.stream(equipment)
                .filter(Objects::nonNull)
                .filter(x -> x.getSlot() == slot)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(ConfigManager configManager, String configGroup, String configKey) {
        shiftLoadoutItems();
        configManager.setConfiguration(configGroup, configKey, this);
    }

    public void shiftLoadoutItems() {
        shiftLoadoutItems(inventory);
        shiftLoadoutItems(runePouch);
    }

    private void shiftLoadoutItems(LoadoutItem[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                for (int j = i + 1; j < items.length; j++) {
                    if (items[j] != null) {
                        items[i] = items[j];
                        items[i].setSlot(i);
                        items[j] = null;
                        break;
                    }
                }
            }
        }
    }
}