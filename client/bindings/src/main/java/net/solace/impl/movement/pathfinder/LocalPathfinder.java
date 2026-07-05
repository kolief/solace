package net.solace.impl.movement.pathfinder;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.IWalker;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.pathfinder.LocalCollisionMap;
import net.solace.api.movement.pathfinder.model.Transport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Data
@Slf4j
public class LocalPathfinder implements Callable<TilePath> {
    private final Queue<Node> boundary = new LinkedList<>();
    private final Set<WorldPoint> visited = new HashSet<>();

    private final IWalker walker;
    private final LocalCollisionMap map;
    private final Map<WorldPoint, List<Transport>> transports;
    private final Node start;
    private WorldArea target;
    private List<WorldPoint> targetTiles;
    private Node nearest;

    public LocalPathfinder(IWalker walker, LocalCollisionMap map, Map<WorldPoint, List<Transport>> transports, WorldPoint start, WorldArea target) {
        this.walker = walker;
        this.map = map;
        this.transports = transports;
        this.target = target;
        this.targetTiles = target.toWorldPointList().stream()
                .map(source -> walker.getNearestWalkableTile(source, map))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        this.start = new Node(null, walker.getNearestWalkableTile(start, map));
        this.nearest = null;
        if (targetTiles.stream().allMatch(map::fullBlock)) {
            log.warn("Walking to a {}, pathfinder will be slow. {}", targetTiles.size() == 1 ? "blocked tile" : "fully blocked area", targetTiles);
        }
    }

    private void addNeighbors(Node node) {
        WorldPoint position = node.position;

        if (map.w(position)) {
            addNeighbor(node, position.dx(-1));
        }

        if (map.e(position)) {
            addNeighbor(node, position.dx(1));
        }

        if (map.s(position)) {
            addNeighbor(node, position.dy(-1));

            if (map.sw(position)) {
                addNeighbor(node, position.dx(-1).dy(-1));
            }

            if (map.se(position)) {
                addNeighbor(node, position.dx(1).dy(-1));
            }
        }

        if (map.n(position)) {
            addNeighbor(node, position.dy(1));

            if (map.nw(position)) {
                addNeighbor(node, position.dx(-1).dy(1));
            }

            if (map.ne(position)) {
                addNeighbor(node, position.dx(1).dy(1));
            }
        }

        transports.getOrDefault(position, new ArrayList<>())
                .forEach(transport -> addNeighbor(node, transport.getDestination()));
    }

    private void addNeighbor(Node node, WorldPoint neighbor) {
        if (!visited.add(neighbor)) {
            return;
        }

        boundary.add(new Node(node, neighbor));
    }

    public TilePath find() {
        long startTime = System.currentTimeMillis();
        TilePath path = find(5_000_000);
        WorldArea destination = path.isIncomplete() ? path.getDestinationArea() : target;
        String targetStr = targetTiles.size() == 1 ? target.toWorldPoint().toString() :
                String.format("WorldArea(x=%s, y=%s, width=%s, height=%s, plane=%s)",
                        destination.getX(), destination.getY(), destination.getWidth(), destination.getHeight(), destination.getPlane());
        String completeStr = path.isIncomplete() ? "Partial path" : "Full path";
        log.debug("{} calculation took {} ms to {}", completeStr, System.currentTimeMillis() - startTime, targetStr);
        return path;
    }

    public TilePath find(int maxSearch) {
        boundary.add(start);

        int bestDistance = Integer.MAX_VALUE;
        TilePath emptyPath = TilePath.empty();
        Set<WorldPoint> visitedTiles = new LinkedHashSet<>();

        while (!boundary.isEmpty()) {
            if (visited.size() >= maxSearch) {
                TilePath nearestPath = new TilePath(nearest.path(), true);
                nearestPath.setVisitedTiles(visitedTiles);
                return nearestPath;
            }

            Node node = boundary.poll();
            visitedTiles.add(node.position);

            if (target.contains(node.position)) {
                TilePath path = new TilePath(node.path(), false);
                path.setVisitedTiles(visitedTiles);
                return path;
            }

            int distance = node.position.distanceTo(target);
            if (nearest == null || distance < bestDistance) {
                nearest = node;
                bestDistance = distance;
            }

            addNeighbors(node);
        }

        if (nearest != null) {
            TilePath nearestPath = new TilePath(nearest.path(), true);
            nearestPath.setVisitedTiles(visitedTiles);
            return nearestPath;
        }

        emptyPath.setVisitedTiles(visitedTiles);
        return emptyPath;
    }

    @Override
    public TilePath call() throws Exception {
        return find();
    }

    @RequiredArgsConstructor
    private final class Node {
        private final Node previous;
        private final WorldPoint position;

        private List<WorldPoint> path() {
            List<WorldPoint> path = new LinkedList<>();
            Node node = this;

            while (node != null) {
                path.add(0, node.position);
                node = node.previous;
            }

            return new ArrayList<>(path);
        }
    }
}