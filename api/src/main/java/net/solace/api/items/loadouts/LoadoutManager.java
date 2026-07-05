package net.solace.api.items.loadouts;

import net.runelite.api.coords.WorldArea;
import net.solace.api.items.loadouts.Loadout;

public interface LoadoutManager {
    public void fetchFromBank(Loadout var1, WorldArea var2);

    public boolean isLoadoutCompleted(Loadout var1);

    public boolean isEquipmentCompleted(Loadout var1);

    public boolean isInventoryCompleted(Loadout var1);

    public boolean isRunePouchCompleted(Loadout var1);

    public void fetchEquipment(Loadout var1, WorldArea var2);

    public void fetchInventory(Loadout var1, WorldArea var2);

    public void fetchRunePouch(Loadout var1, WorldArea var2);
}

