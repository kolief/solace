package net.solace.impl.items.loadouts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EnumID;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IInventory;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutBuilder;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.items.loadouts.LoadoutManager;
import net.solace.api.magic.RunePouch;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class LoadoutFactoryImpl implements ILoadoutFactory {
    private final IEquipment equipment;
    private final IInventory inventory;
    private final IClient client;
    private final IClientThread clientThread;
    private final LoadoutManager loadoutManager;

    @Override
    public LoadoutBuilder newBuilder() {
        return new LoadoutBuilderImpl(loadoutManager);
    }

    @Override
    public LoadoutBuilder fromCurrentRunePouch() {
        var builder = newBuilder();
        var runes = getRunesFromRunePouch();
        runes.forEach(builder::item);
        return builder;
    }

    @Override
    public LoadoutBuilder fromCurrentEquipment() {
        var equips = equipment.getAll(x -> true);
        var equipmentToAdd = new ArrayList<LoadoutItem>();

        for (var item : equips) {
            equipmentToAdd.add(new LoadoutItem(
                    item.getId(),
                    item.getQuantity(),
                    item.isStackable(),
                    item.isNoted(),
                    LoadoutItem.Type.EQUIPMENT,
                    item.getSlot()
            ));
        }

        return newBuilder().items(equipmentToAdd);
    }

    @Override
    public LoadoutBuilder fromCurrentInventory() {
        var inv = inventory.getAll(x -> true);
        var inventoryToAdd = new ArrayList<LoadoutItem>();

        for (var item : inv) {
            inventoryToAdd.add(new LoadoutItem(
                    item.getId(),
                    item.getQuantity(),
                    item.isStackable(),
                    item.isNoted(),
                    LoadoutItem.Type.INVENTORY,
                    item.getSlot()
            ));
        }

        return newBuilder().items(inventoryToAdd);
    }

    @Override
    public LoadoutBuilder fromCurrentSetup() {
        var inventoryLoadout = fromCurrentInventory().build();
        var builder = fromCurrentEquipment()
                .items(inventoryLoadout.getInventory());

        if (inventoryLoadout.hasRunePouch()) {
            builder = builder.items(fromCurrentRunePouch().build().getRunePouch());
        }

        return builder;
    }

    private List<LoadoutItem> getRunesFromRunePouch() {
        List<LoadoutItem> runePouchRunesToAdd = new ArrayList<>();

        var runePouch = clientThread.invokeAndWait(() -> client.getWrapped().getEnum(EnumID.RUNEPOUCH_RUNE));

        var slots = RunePouch.RuneSlot.values();

        for (int i = 0; i < slots.length; i++) {
            var runeslot = slots[i];
            var runeId = runePouch.getIntValue(runeslot.getVarbit());
            var quantity = runeslot.getQuantity();

            if (runeId != 0 && quantity > 0) {
                log.debug("Adding rune pouch rune: {} x {}", runeslot.getRuneName(), quantity);
                runePouchRunesToAdd.add(new LoadoutItem(
                        runeId,
                        quantity,
                        16000,
                        true,
                        false,
                        LoadoutItem.Type.RUNE_POUCH,
                        i
                ));
            }
        }

        return runePouchRunesToAdd;
    }

    @Override
    public LoadoutBuilder fromLoadout(Loadout loadout) {
        var loadoutBuilder = newBuilder();
        if (loadout.isInventoryDisabled()) {
            loadoutBuilder.disableInventory();
        }

        if (loadout.isEquipmentDisabled()) {
            loadoutBuilder.disableEquipment();
        }

        if (loadout.isRunePouchDisabled()) {
            loadoutBuilder.disableRunePouch();
        }

        return loadoutBuilder.items(loadout.getItems());
    }
}
