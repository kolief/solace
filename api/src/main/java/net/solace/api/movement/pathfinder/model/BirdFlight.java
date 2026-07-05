package net.solace.api.movement.pathfinder.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.movement.pathfinder.model.MovementConstants;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.Comparison;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarType;

public enum BirdFlight {
    CIVITAS_ILLA_FORTIS("Civitas illa Fortis", new WorldPoint(1697, 3140, 0), 0, 1),
    THE_TEOMAT("The Teomat", new WorldPoint(1437, 3171, 0), 1, 2),
    SUNSET_COAST("Sunset Coast", new WorldPoint(1548, 2995, 0), 2, 3),
    HUNTER_GUILD("Hunter Guild", new WorldPoint(1585, 3053, 0), 3, 4),
    CAM_TORUM_ENTRANCE("Cam Torum Entrance", new WorldPoint(1446, 3108, 0), 4, 5, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 9955, 1))),
    COLOSSAL_WYRM_REMAINS("Colossal Wyrm Remains", new WorldPoint(1670, 2933, 0), 5, 6, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 9956, 1))),
    OUTER_FORTIS("Outer Fortis", new WorldPoint(1700, 3037, 0), 6, 7, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 9957, 1))),
    FORTIS_COLOSSEUM("Fortis Colosseum", new WorldPoint(1779, 3111, 0), 7, 8, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 9958, 1))),
    ALDARIN("Aldarin", new WorldPoint(1389, 2901, 0), 8, 9),
    QUETZACALLI_GORGE("Quetzacalli Gorge", new WorldPoint(1510, 3222, 0), 9, 10),
    SALVAGER_OVERLOOK("Salvager Overlook", new WorldPoint(1613, 3300, 0), 10, 11, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 11379, 1))),
    TAL_TEKLAN("Tal Teklan", new WorldPoint(1226, 3091, 0), 11, 12),
    AUBURNVALE("Auburnvale", new WorldPoint(1411, 3361, 0), 12, 13),
    KASTORI("Kastori", new WorldPoint(1344, 3022, 0), 13, 14, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 17757, 1)));

    private final WorldPoint location;
    private final int widgetId;
    private final int lastIndex;
    private final String name;
    private final Requirements requirements;

    private BirdFlight(String name, WorldPoint location, int widgetId, int lastIndex, Requirements requirements) {
        this.name = name;
        this.location = location;
        this.widgetId = widgetId;
        this.lastIndex = lastIndex;
        this.requirements = requirements;
    }

    private BirdFlight(String name, WorldPoint location, int widgetId, int lastIndex) {
        this(name, location, widgetId, lastIndex, new Requirements());
    }

    public static List<Transport> getBirdFlightTransports() {
        return Arrays.stream(BirdFlight.values()).filter(BirdFlight::canUse).flatMap(source -> Arrays.stream(BirdFlight.values()).filter(target -> !source.getLocation().equals((Object)target.getLocation())).filter(BirdFlight::canUse).map(target -> Static.getTransportLoader().birdFlight((BirdFlight)((Object)source), (BirdFlight)((Object)((Object)target))))).collect(Collectors.toList());
    }

    public static BirdFlight getLastDestination() {
        return Arrays.stream(BirdFlight.values()).filter(birdFlight -> birdFlight.lastIndex == Static.getVars().getBit(MovementConstants.LAST_BIRD_TRANSPORT)).findFirst().orElse(null);
    }

    public boolean canUse() {
        return this.requirements.fulfilled();
    }

    public IWidget getWidget() {
        return Static.getWidgets().get(874, 12, this.widgetId);
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public int getWidgetId() {
        return this.widgetId;
    }

    public int getLastIndex() {
        return this.lastIndex;
    }

    public String getName() {
        return this.name;
    }

    public Requirements getRequirements() {
        return this.requirements;
    }
}

