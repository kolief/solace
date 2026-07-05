package net.solace.api.items.loadouts;

import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutBuilder;

public interface ILoadoutFactory {
    public LoadoutBuilder newBuilder();

    public LoadoutBuilder fromCurrentRunePouch();

    public LoadoutBuilder fromCurrentEquipment();

    public LoadoutBuilder fromCurrentInventory();

    public LoadoutBuilder fromCurrentSetup();

    public LoadoutBuilder fromLoadout(Loadout var1);
}

