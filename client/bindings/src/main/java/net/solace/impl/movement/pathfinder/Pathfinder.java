package net.solace.impl.movement.pathfinder;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.IWalker;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.model.CharterShip;
import net.solace.api.movement.pathfinder.model.IgnoredDoor;
import net.solace.api.movement.pathfinder.model.MagicCarpet;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static net.solace.api.movement.pathfinder.model.MovementConstants.ESCAPE_CAVES;
import static net.solace.api.movement.pathfinder.model.MovementConstants.FEROX_AREAS;
import static net.solace.api.movement.pathfinder.model.MovementConstants.GOD_WARS_WILDERNESS_CUBOID;
import static net.solace.api.movement.pathfinder.model.MovementConstants.MAIN_WILDERNESS_CUBOID;
import static net.solace.api.movement.pathfinder.model.MovementConstants.NOT_WILDERNESS_BLACK_KNIGHTS;
import static net.solace.api.movement.pathfinder.model.MovementConstants.SAFE_WILDERNESS_UNDERGROUND;
import static net.solace.api.movement.pathfinder.model.MovementConstants.WILDERNESS_ABOVE_GROUND;
import static net.solace.api.movement.pathfinder.model.MovementConstants.WILDERNESS_BOSS_AREAS;
import static net.solace.api.movement.pathfinder.model.MovementConstants.WILDERNESS_UNDERGROUND;
import static net.solace.api.movement.pathfinder.model.MovementConstants.WILDERNESS_UNDERGROUND_CUBOID;

@Data
@Slf4j
public class Pathfinder implements Callable<TilePath> {
    public static WorldArea KBD_LAIR = new WorldArea(2253, 4678, 37, 35, 0);
    private final CollisionMap map;
    private final Map<WorldPoint, List<WorldPoint>> transports;
    private final Map<WorldPoint, List<WorldPoint>> reverseTransports;
    private final HashMap<WorldPoint, Teleport> teleports;
    private final Set<Teleport> teleportsToMap;
    private final Queue<Node> boundary = new LinkedList<>();
    private final Queue<Node> targetBoundary = new LinkedList<>();
    private final Set<Integer> visited = new HashSet<>();
    private final Set<Integer> targetVisited = new HashSet<>();
    private final Map<Integer, Integer> bestGoldMap = new HashMap<>();
    private final Map<WorldPoint, Node> teleportDestNodes;
    private List<Node> start;
    private List<Node> end;
    private WorldArea target;
    private List<WorldPoint> targetTiles;
    private Node nearest;
    private boolean avoidWilderness;
    private int initialGold = 0;
    private final IWalker walker;
    private final WorldPoint playerLocation;
    private List<IgnoredDoor> ignoredDoors;


    public Pathfinder(IWalker walker, CollisionMap collisionMap, Map<WorldPoint, List<Transport>> transports, Collection<WorldPoint> start, WorldArea target, boolean avoidWilderness, int initialGold, WorldPoint playerLocation, HashMap<WorldPoint, Teleport> teleports, List<IgnoredDoor> ignoredDoors) {
        this.walker = walker;
        this.map = collisionMap;
        this.playerLocation = playerLocation;
        this.transports = new HashMap<>();
        this.reverseTransports = new HashMap<>();
        mapTransports(transports);
        this.target = target;
        this.initialGold = initialGold;

        final List<WorldPoint> worldPointList = target.toWorldPointList();
        this.targetTiles = new ArrayList<>(worldPointList.size());
        for (WorldPoint point : worldPointList) {
            if (point != null && (!collisionMap.fullBlock(point) || point.equals(playerLocation))) {
                this.targetTiles.add(point);
            }
        }

        if (targetTiles.isEmpty()) {
            for (WorldPoint source : worldPointList) {
                WorldPoint walkable = getNearestWalkableTile(source, collisionMap);
                if (walkable != null) {
                    this.targetTiles.add(walkable);
                }
            }
        }

        if (targetTiles.isEmpty()) {
            throw new IllegalStateException("No valid target tiles found for target: " + target);
        }

        this.ignoredDoors = ignoredDoors;
        this.teleports = new HashMap<>(teleports);
        this.teleportsToMap = new HashSet<>(teleports.values());

        this.teleportDestNodes = new HashMap<>();

        this.start = new ArrayList<>(start.size());
        for (WorldPoint source : start) {
            WorldPoint walkable = getNearestWalkableTile(source, collisionMap);
            if (walkable != null) {
                this.start.add(new Node(null, walkable, initialGold, null));
            }
        }
        this.end = new ArrayList<>(targetTiles.size());
        for (WorldPoint point : targetTiles) {
            this.end.add(new Node(null, point, initialGold, null));
        }
        this.nearest = null;
        this.avoidWilderness = avoidWilderness;

        boolean allBlocked = true;
        for (WorldPoint point : targetTiles) {
            if (!collisionMap.fullBlock(point)) {
                allBlocked = false;
                break;
            }
        }

        if (allBlocked) {
            log.warn("Walking to a {}, pathfinder will be slow. {}", targetTiles.size() == 1 ? "blocked tile" : "fully blocked area", targetTiles);
        }

        boundary.addAll(this.start);
        targetBoundary.addAll(this.end);
    }

    public static int getWildernessLevelFrom(WorldPoint point) {
        int regionID = point.getRegionID();
        if (regionID != 12700 && regionID != 12187) {
            if (Arrays.stream(WILDERNESS_BOSS_AREAS).anyMatch(x -> x.contains(point))) {
                return 21;
            } else if (ESCAPE_CAVES.contains(point)) {
                return 33;
            } else if (MAIN_WILDERNESS_CUBOID.contains(point)) {
                return NOT_WILDERNESS_BLACK_KNIGHTS.contains(point.getX(), point.getY()) ? 0 : (point.getY() - 3520) / 8 + 1;
            } else if (GOD_WARS_WILDERNESS_CUBOID.contains(point)) {
                return (point.getY() - 9920) / 8 - 1;
            } else {
                return WILDERNESS_UNDERGROUND_CUBOID.contains(point) ? (point.getY() - 9920) / 8 + 1 : 0;
            }
        } else {
            return 0;
        }
    }

    private boolean isInWilderness(WorldPoint location) {
        return location.isInArea2D(WILDERNESS_ABOVE_GROUND, WILDERNESS_UNDERGROUND) && !isInFerox(location) && !isInSafeUnderground(location);
    }

    private boolean isInFerox(WorldPoint location) {
        return Arrays.stream(FEROX_AREAS).anyMatch(feroxArea -> feroxArea.contains(location));
    }

    private boolean isInSafeUnderground(WorldPoint location) {
        return Arrays.stream(SAFE_WILDERNESS_UNDERGROUND).anyMatch(safeUnderground -> safeUnderground.contains(location));
    }

    private WorldPoint getNearestWalkableTile(WorldPoint source, CollisionMap collisionMap) {
        if (source.equals(playerLocation)) {
            return source;
        }

        return walker.getNearestWalkableTile(source, collisionMap);
    }

    private void mapTransports(Map<WorldPoint, List<Transport>> transports) {
        List<TransportPair> pairs = transports.values().stream()
                .flatMap(Collection::stream)
                .map(transport -> new TransportPair(transport.getSource(), transport.getDestination()))
                .collect(Collectors.toList());

        for (TransportPair pair : pairs) {
            this.transports.computeIfAbsent(pair.source, k -> new ArrayList<>()).add(pair.destination);
            this.reverseTransports.computeIfAbsent(pair.destination, k -> new ArrayList<>()).add(pair.source);
        }
    }

    private void addNeighbors(Node node, Queue<Node> boundary, Set<Integer> visited, Map<WorldPoint, List<WorldPoint>> transports) {
        WorldPoint position = node.position;
        boolean isForwardSearch = boundary == this.boundary && visited == this.visited;

        if (map.w(position)) {
            addNeighbor(node, position.dx(-1), boundary, visited, null);
        }

        if (map.e(position)) {
            addNeighbor(node, position.dx(1), boundary, visited, null);
        }

        if (map.s(position)) {
            addNeighbor(node, position.dy(-1), boundary, visited, null);

            if (map.sw(position)) {
                addNeighbor(node, position.dx(-1).dy(-1), boundary, visited, null);
            }

            if (map.se(position)) {
                addNeighbor(node, position.dx(1).dy(-1), boundary, visited, null);
            }
        }

        if (map.n(position)) {
            addNeighbor(node, position.dy(1), boundary, visited, null);

            if (map.nw(position)) {
                addNeighbor(node, position.dx(-1).dy(1), boundary, visited, null);
            }

            if (map.ne(position)) {
                addNeighbor(node, position.dx(1).dy(1), boundary, visited, null);
            }
        }

        List<WorldPoint> destinations = transports.get(position);
        if (destinations != null) {
            destinations.forEach(neighbor -> addNeighbor(node, neighbor, boundary, visited, null));
        }

        if (isForwardSearch && !KBD_LAIR.contains(position)) {
            Iterator<Teleport> it = teleportsToMap.iterator();
            while (it.hasNext()) {
                Teleport t = it.next();
                int wildernessLevel = getWildernessLevelFrom(position);

                if ((wildernessLevel != 0 || !isInWilderness(position))
                        && wildernessLevel <= t.getMaximumWildernessLevel()
                        && position.distanceTo(t.getDestination()) >= 30) {
                    addNeighbor(node, t.getDestination(), boundary, visited, t);
                    it.remove();
                }
            }
        }
    }

    private void addNeighbor(Node node, WorldPoint neighbor, Queue<Node> boundary, Set<Integer> visited, Teleport teleport) {
        int cost = CharterShip.getCharterShipCost(node.position, neighbor);

        if (cost == 0) {
            cost = MagicCarpet.getCarpetCost(node.position, neighbor);
        }

        int packedNeighbor = packWorldPoint(neighbor);
        if (cost == 0 && !visited.add(packedNeighbor)) {
            return;
        }

        if (cost > 0) {
            if (cost > node.goldAvailable) {
                return;
            }

            int remainingGold = node.goldAvailable - cost;
            Integer previousBestGold = bestGoldMap.get(packedNeighbor);

            if (previousBestGold != null && remainingGold <= previousBestGold + 200) {
                return;
            }

            bestGoldMap.put(packedNeighbor, remainingGold);
        }

        boolean isIgnoredDoor = ignoredDoors.stream().anyMatch(x ->
            x.blocks(node.position, neighbor)
        );

        if (isIgnoredDoor) {
            return;
        }

        if (avoidWilderness && isInWilderness(neighbor) && !isInWilderness(node.position) && targetTiles.stream().noneMatch(this::isInWilderness)) {
            return;
        }

        boundary.add(new Node(node, neighbor, node.goldAvailable - cost, teleport));
    }

    public TilePath find() {
        long startTime = System.currentTimeMillis();
        TilePath path = findPath();
        WorldArea destination = path.isEmpty() ? target : path.getDestinationArea();
        String targetStr = targetTiles.size() == 1 ? target.toWorldPoint().toString() :
                String.format("WorldArea(x=%s, y=%s, width=%s, height=%s, plane=%s)",
                        destination.getX(), destination.getY(), destination.getWidth(), destination.getHeight(), destination.getPlane());
        String completeStr = path.isIncomplete() ? "Partial path" : "Full path";
        log.debug("{} calculation took {} ms to {}", completeStr, System.currentTimeMillis() - startTime, targetStr);
        return path;
    }

    private TilePath findPath() {
        while (!boundary.isEmpty() && !targetBoundary.isEmpty()) {
            Node start = boundary.poll();

            if (targetVisited.contains(packWorldPoint(start.position))) {
                Node first = CollectionUtils.getFirst(targetBoundary, node -> node.position.equals(start.position));

                if (first == null && teleports.containsKey(start.position)) {
                    first = teleportDestNodes.get(start.position);
                }

                if (first != null) {
                    List<Node> targetPath = first.path();
                    targetPath.remove(0);
                    List<Node> path = CollectionUtils.joinToList(CollectionUtils.reversedList(start.path()), targetPath);
                    List<WorldPoint> worldPath = new ArrayList<>(path.size());
                    List<Teleport> teleportPath = new ArrayList<>();
                    for (Node node : path) {
                        worldPath.add(node.position);
                        if (node.teleport != null) {
                            teleportPath.add(node.teleport);
                        }
                    }
                    log.debug("Found path from start to target: {}", worldPath);
                    var tilePath = new TilePath(worldPath, false);
                    tilePath.setTeleports(teleportPath);
                    return tilePath;
                }
            } else {
                addNeighbors(start, boundary, visited, transports);
            }

            Node end = targetBoundary.poll();
            if (visited.contains(packWorldPoint(end.position))) {
                Node first = CollectionUtils.getFirst(boundary, node -> node.position.equals(end.position));
                if (first != null) {
                    List<Node> endPath = end.path();
                    endPath.remove(0);
                    List<Node> path = CollectionUtils.joinToList(CollectionUtils.reversedList(first.path()), endPath);
                    List<WorldPoint> worldPath = new ArrayList<>(path.size());
                    List<Teleport> teleportPath = new ArrayList<>();
                    for (Node node : path) {
                        worldPath.add(node.position);
                        if (node.teleport != null) {
                            teleportPath.add(node.teleport);
                        }
                    }
                    var tilePath = new TilePath(worldPath, false);
                    tilePath.setTeleports(teleportPath);
                    log.debug("Found path from target to start: {}", worldPath);
                    return tilePath;
                }
            } else {
                addNeighbors(end, targetBoundary, targetVisited, reverseTransports);
                if (teleports.containsKey(end.position)) {
                    teleportDestNodes.put(end.position, end);
                }
            }
        }

        throw new IllegalStateException("Could not find any paths to: " + targetTiles);
    }

    @Override
    public TilePath call() throws Exception {
        return find();
    }

    private static int packWorldPoint(WorldPoint wp) {
        return (wp.getPlane() << 28) | ((wp.getY() & 0x7FFF) << 13) | (wp.getX() & 0x1FFF);
    }

    @RequiredArgsConstructor
    private static class TransportPair {
        private final WorldPoint source;
        private final WorldPoint destination;
    }

    private final class Node {
        private final Node previous;
        private final WorldPoint position;
        private final int weight;
        private final int goldAvailable;
        @Getter
        private final Teleport teleport;

        public Node(Node previous, WorldPoint position, int weight, int goldAvailable, Teleport teleport) {
            this.previous = previous;
            this.position = position;
            this.weight = weight;
            this.goldAvailable = goldAvailable;
            this.teleport = teleport;
        }

        private Node(Node previous, WorldPoint position, int goldAvailable, Teleport teleport) {
            this(previous, position, 0, goldAvailable, teleport);
        }

        private List<Node> path() {
            List<Node> path = new LinkedList<>();
            Node node = this;

            while (node != null) {
                path.add(0, node);
                node = node.previous;
            }

            return CollectionUtils.reversedList(path);
        }
    }
}
