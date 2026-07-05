package net.solace.api.movement.pathfinder.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Minecart {
    ARCEUUS(new WorldPoint(1670, 3833, 0), "Arceuus"),
    FARMING_GUILD(new WorldPoint(1218, 3737, 0), "Farming Guild"),
    HOSIDIUS_SOUTH(new WorldPoint(1808, 3479, 0), "Hosidius South"),
    HOSIDIUS_WEST(new WorldPoint(1655, 3543, 0), "Hosidius West"),
    KINGSTOWN(new WorldPoint(1699, 3660, 0), "Kingstown"),
    KOUREND_WOODLAND(new WorldPoint(1572, 3466, 0), "Kourend Woodland"),
    LOVAKENGJ(new WorldPoint(1518, 3733, 0), "Lovakengj"),
    MOUNT_QUIDAMORTEM(new WorldPoint(1255, 3548, 0), "Mount Quidamortem"),
    NORTHERN_TUNDRAS(new WorldPoint(1648, 3931, 0), "Northern Tundras"),
    PORT_PISCARILIUS(new WorldPoint(1761, 3710, 0), "Port Piscarilius"),
    SHAYZIEN_EAST(new WorldPoint(1590, 3620, 0), "Shayzien East"),
    SHAYZIEN_WEST(new WorldPoint(1415, 3577, 0), "Shayzien West");

    private static final Logger log;
    private final WorldPoint location;
    private final String target;
    private final Requirements requirements;

    private Minecart(WorldPoint location, String target, Requirements requirements) {
        this.location = location;
        this.target = target;
        this.requirements = requirements;
    }

    private Minecart(WorldPoint location, String target) {
        this(location, target, new Requirements());
    }

    public static List<Transport> getMinecartTransports() {
        return Arrays.stream(Minecart.values()).flatMap(source -> Arrays.stream(Minecart.values()).filter(target -> !source.getLocation().equals((Object)target.getLocation())).filter(target -> source.canUse() && target.canUse()).map(target -> Static.getTransportLoader().minecartTransport(source.getLocation(), target.getLocation(), target.getTarget()))).collect(Collectors.toList());
    }

    public boolean canUse() {
        if (this == LOVAKENGJ) {
            return Static.getPlayers().getLocal().getCombatLevel() > 54;
        }
        if (this == PORT_PISCARILIUS) {
            return Static.getPlayers().getLocal().getCombatLevel() > 12;
        }
        return this.requirements.fulfilled();
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public String getTarget() {
        return this.target;
    }

    public Requirements getRequirements() {
        return this.requirements;
    }

    static {
        log = LoggerFactory.getLogger(Minecart.class);
    }
}

