package net.solace.sdk.sailing;

import java.util.Collection;
import net.solace.api.Static;
import net.solace.api.sailing.IShips;
import net.solace.api.sailing.Ship;

public class Ships {
    private static final IShips SHIPS = Static.getShips();

    public Collection<Ship> getAll() {
        return SHIPS.getAll();
    }
}

