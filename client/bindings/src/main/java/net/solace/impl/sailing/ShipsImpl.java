package net.solace.impl.sailing;

import lombok.RequiredArgsConstructor;
import net.solace.api.sailing.IShips;
import net.solace.api.sailing.Ship;
import net.solace.impl.containers.ShipContainer;

import java.util.Collection;

@RequiredArgsConstructor
public class ShipsImpl implements IShips {
    private final ShipContainer shipContainer;

    @Override
    public Collection<Ship> getAll() {
        return shipContainer.getShips().values();
    }

    @Override
    public Ship getByWorldViewId(int id) {
        return shipContainer.getShips().get(id);
    }
}
