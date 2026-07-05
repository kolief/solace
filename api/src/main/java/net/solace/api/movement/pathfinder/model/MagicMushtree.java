package net.solace.api.movement.pathfinder.model;

import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.widgets.InterfaceAddress;

public enum MagicMushtree {
    HOUSE(new WorldPoint(3764, 3879, 1), 39845892),
    VALLEY(new WorldPoint(3760, 3758, 0), 39845896),
    SWAMP(new WorldPoint(3676, 3755, 0), 39845900),
    MEADOW(new WorldPoint(3676, 3871, 0), 39845904);

    private final WorldPoint location;
    private final int component;

    public static List<Transport> getMushtreeTransports() {
        return Static.getSolaceConfig().magicMushtrees().stream().flatMap(source -> Static.getSolaceConfig().magicMushtrees().stream().filter(target -> !source.getLocation().equals((Object)target.getLocation())).map(target -> Static.getTransportLoader().mushtreeTransport(source.getLocation(), target.getLocation(), target.getComponent()))).collect(Collectors.toList());
    }

    @Deprecated(forRemoval=true)
    public InterfaceAddress getWidgetInfo() {
        return new InterfaceAddress(this.component);
    }

    private MagicMushtree(WorldPoint location, int component) {
        this.location = location;
        this.component = component;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public int getComponent() {
        return this.component;
    }
}

