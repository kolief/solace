package net.solace.api.sailing;

import java.util.Collection;
import net.solace.api.sailing.Ship;

public interface IShips {
    public Collection<Ship> getAll();

    public Ship getByWorldViewId(int var1);
}

