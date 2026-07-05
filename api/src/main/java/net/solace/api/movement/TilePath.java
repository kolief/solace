package net.solace.api.movement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.movement.WalkOptions;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.movement.pathfinder.model.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TilePath
extends ArrayList<WorldPoint>
implements Comparable<TilePath> {
    private static final Logger log = LoggerFactory.getLogger(TilePath.class);
    private final boolean incomplete;
    private Set<WorldPoint> visitedTiles;
    private List<Teleport> teleports;
    private List<Transport> transports;
    private final WorldArea destination;
    private final double weight;

    public TilePath(List<WorldPoint> points, WorldArea destination, double weight, boolean incomplete) {
        this.addAll(points);
        this.destination = destination;
        this.incomplete = incomplete;
        this.visitedTiles = new LinkedHashSet<WorldPoint>();
        this.teleports = new ArrayList<Teleport>();
        this.transports = new ArrayList<Transport>();
        this.weight = weight;
    }

    public TilePath(boolean incomplete) {
        this(new ArrayList<WorldPoint>(), null, 0.0, incomplete);
    }

    public TilePath(Collection<WorldPoint> worldPoints, boolean incomplete) {
        this(new ArrayList<WorldPoint>(worldPoints), null, 0.0, incomplete);
    }

    public static TilePath empty() {
        return new TilePath(Collections.emptyList(), null, 0.0, true);
    }

    public static TilePath of(Collection<WorldPoint> points, WorldArea destination, boolean incomplete) {
        return new TilePath(new ArrayList<WorldPoint>(points), destination, 0.0, incomplete);
    }

    public WorldPoint getDestination() {
        if (this.destination != null) {
            return this.destination.toWorldPoint();
        }
        return this.isEmpty() ? null : (WorldPoint)this.get(this.size() - 1);
    }

    public WorldArea getDestinationArea() {
        if (this.destination != null) {
            return this.destination;
        }
        return this.isEmpty() ? null : ((WorldPoint)this.get(this.size() - 1)).toWorldArea();
    }

    public void addTeleport(Teleport teleport) {
        this.teleports.add(teleport);
    }

    public void addTransport(Transport transport) {
        this.transports.add(transport);
    }

    public void addVisitedTile(WorldPoint point) {
        this.visitedTiles.add(point);
    }

    public void walk() {
        this.walk(WalkOptions.builder().build());
    }

    public void walk(boolean useTransports) {
        this.walk(WalkOptions.builder().useTransports(useTransports).build());
    }

    public void walk(WalkOptions options) {
        WorldArea dest = this.getDestinationArea();
        if (dest == null) {
            log.debug("Destination is null in walk");
            return;
        }
        Static.getWalker().walkAlong(dest, this, options.isUseTransports() ? Static.getWalker().buildTransportLinks() : Collections.emptyMap(), options);
    }

    public TilePath getRemainingPath() {
        IPlayer local = Static.getPlayers().getLocal();
        WorldPoint playerLocation = local.getWorldLocation();
        WorldArea dest = this.getDestinationArea();
        if (dest == null) {
            log.debug("Destination is null in getRemainingPath");
            return TilePath.empty();
        }
        int closestIndex = -1;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.size(); ++i) {
            WorldPoint pathPoint = (WorldPoint)this.get(i);
            double distance = pathPoint.distanceTo(playerLocation);
            if (!(distance < closestDistance)) continue;
            closestDistance = distance;
            closestIndex = i;
        }
        if (closestIndex == -1) {
            return TilePath.empty();
        }
        TilePath path = TilePath.of(this.subList(closestIndex, this.size()), dest, false);
        path.getTransports().addAll(this.getTransports());
        path.getTeleports().addAll(this.getTeleports());
        return path;
    }

    public TilePath subList(int fromIndex, int toIndex) {
        return new TilePath(super.subList(fromIndex, toIndex), this.incomplete);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TilePath tilePath = (TilePath)o;
        return this.incomplete == tilePath.incomplete;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.incomplete);
    }

    @Override
    public int compareTo(TilePath other) {
        return Double.compare(this.weight, other.weight);
    }

    public boolean isIncomplete() {
        return this.incomplete;
    }

    public Set<WorldPoint> getVisitedTiles() {
        return this.visitedTiles;
    }

    public void setVisitedTiles(Set<WorldPoint> visitedTiles) {
        this.visitedTiles = visitedTiles;
    }

    public List<Teleport> getTeleports() {
        return this.teleports;
    }

    public void setTeleports(List<Teleport> teleports) {
        this.teleports = teleports;
    }

    public List<Transport> getTransports() {
        return this.transports;
    }

    public void setTransports(List<Transport> transports) {
        this.transports = transports;
    }

    public double getWeight() {
        return this.weight;
    }
}

