package net.solace.sdk.items.loadouts;

import net.solace.api.Static;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutBuilder;

public class LoadoutFactory {
    private static final ILoadoutFactory BUILDER = Static.getLoadoutFactory();

    public static LoadoutBuilder newBuilder() {
        return BUILDER.newBuilder();
    }

    public static LoadoutBuilder fromCurrentRunePouch() {
        return BUILDER.fromCurrentRunePouch();
    }

    public static LoadoutBuilder fromCurrentEquipment() {
        return BUILDER.fromCurrentEquipment();
    }

    public static LoadoutBuilder fromCurrentInventory() {
        return BUILDER.fromCurrentInventory();
    }

    public static LoadoutBuilder fromCurrentSetup() {
        return BUILDER.fromCurrentSetup();
    }

    public static LoadoutBuilder fromCurrentLoadout(Loadout loadout) {
        return BUILDER.fromLoadout(loadout);
    }
}

