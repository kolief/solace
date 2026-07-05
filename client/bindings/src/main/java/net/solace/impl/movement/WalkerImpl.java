package net.solace.impl.movement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.WidgetInfo;
import net.solace.api.commons.Predicates;
import net.solace.api.commons.Rand;
import net.solace.api.coords.Area;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.domain.tiles.IWallObject;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileObjects;
import net.solace.api.entities.ITiles;
import net.solace.api.game.IGame;
import net.solace.api.game.IHouse;
import net.solace.api.game.IVars;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.items.IBank;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IGrandExchange;
import net.solace.api.items.IInventory;
import net.solace.api.movement.IReachable;
import net.solace.api.movement.IWalker;
import net.solace.api.movement.TilePath;
import net.solace.api.movement.WalkOptions;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.ITeleportLoader;
import net.solace.api.movement.pathfinder.ITransportLoader;
import net.solace.api.movement.pathfinder.LocalCollisionMap;
import net.solace.api.movement.pathfinder.model.CharterShip;
import net.solace.api.movement.pathfinder.model.FairyRing;
import net.solace.api.movement.pathfinder.model.GnomeGlider;
import net.solace.api.movement.pathfinder.model.MagicCarpet;
import net.solace.api.movement.pathfinder.model.MovementConstants;
import net.solace.api.movement.pathfinder.model.PohPool;
import net.solace.api.movement.pathfinder.model.SpiritTree;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.movement.pathfinder.model.TeleportSpell;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.MinigameTeleport;
import net.solace.impl.movement.pathfinder.LocalPathfinder;
import net.solace.impl.movement.pathfinder.Pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class WalkerImpl implements IWalker {
    private static final int MAX_NEAREST_SEARCH_ITERATIONS = 10;

    @Getter
    @Setter
    private TilePath lastPath;
    @Getter
    @Setter
    private TilePath currentPath;
    private WorldArea lastDestination;

    private final SolaceConfig solaceConfig;
    private final IClient client;
    private final IGame game;
    private final IWidgets widgets;
    private final IVars vars;
    private final IClientThread clientThread;
    private final IHouse house;
    private final IBank bank;
    private final IGrandExchange grandExchange;
    private final IPlayers players;
    private final INPCs npcs;
    private final ITileObjects tileObjects;
    private final ITransportLoader transportLoader;
    private final ITeleportLoader teleportLoader;
    private final IEquipment equipment;
    private final IInventory inventory;
    private final IDialog dialog;
    private final ITiles tiles;
    private final IReachable reachable;
    private final WalkerManager walkerManager;

    @Override
    public void walk(WorldPoint worldPoint) {
        var local = client.getLocalPlayer();
        var walkPoint = worldPoint;
        var destinationTile = tiles.getAt(worldPoint);
        // Check if tile is in loaded client scene
        if (destinationTile == null) {
            log.debug("Destination {} is not in scene", worldPoint);
            var nearestInScene = tiles.getAll(x -> true)
                    .stream()
                    .min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(local.getWorldLocation())))
                    .orElse(null);
            if (nearestInScene == null) {
                log.debug("Couldn't find nearest walkable tile");
                return;
            }

            walkPoint = nearestInScene.getWorldLocation();
            log.info("Calculated new destination {} as provided tile is null", walkPoint);
        }

        LocalPoint localPoint = LocalPoint.fromWorld(client.getWrapped(), walkPoint);
        if (localPoint == null) {
            throw new IllegalStateException("Failed to convert walkPoint to localPoint, walking aborted.");
        }

        Point canv = Perspective.localToCanvas(client.getWrapped(), localPoint, walkPoint.getPlane());
        int x = canv != null ? canv.getX() : -1;
        int y = canv != null ? canv.getY() : -1;

        log.debug("Walking to: {} (local: {})", walkPoint, localPoint);

        client.interact(
                AutomatedMenu.builder()
                        .identifier(0)
                        .opcode(MenuAction.WALK)
                        .param0(localPoint.getSceneX())
                        .param1(localPoint.getSceneY())
                        .clickPoint(new Coordinate(x, y))
                        .build()
        );
    }

    @Override
    public void walk(WorldPoint worldPoint, WalkOptions options) {
        walk(worldPoint);
    }

    @Override
    public boolean walkTo(WorldArea destination, WalkOptions options) {
        return walkTo(destination, options, 1200, 3);
    }

    @Override
    public int getWalkerDelay() {
        return walkerManager.getWalkerDelay();
    }

    @Override
    public void setWalkerDelay(int delay) {
        walkerManager.setWalkerDelay(delay);
    }

    @Override
    public boolean shouldUseCanvasClick(Point canvas, LocalPoint localPoint, WorldPoint worldPoint, Point minimapPoint, IPlayer player, WalkOptions options) {
        return canvas != null && canvas.getX() >= 0 && canvas.getY() >= 0;
    }

    private boolean walkTo(WorldArea destination, WalkOptions options, long timeoutMs, int maxPaths) {
        var local = client.getLocalPlayer();
        if (destination.contains(local.getWorldLocation())) {
            return true;
        }

        int walkerDelay = walkerManager.getWalkerDelay();
        if (walkerDelay > 0) {
            return false;
        }

        if (game.isInCutscene()
                || game.getState() == GameState.LOADING
                || widgets.isVisible(299, 0)
                || local.getPoseAnimation() == 6936) {
            return false;
        }

        var transports = buildTransportLinks(options);
        var teleports = new LinkedHashMap<WorldPoint, Teleport>();
        List<WorldPoint> startPoints = new ArrayList<>();

        if (options.isUseTeleports()) {
            var localTeleports = buildTeleportLinks(destination);
            teleports = filterTeleportsByOptions(localTeleports, options, local);
        }

        final var playerLocation = isWebbed(local.getWorldLocation()) ? getNearestWalkableTile(local.getWorldLocation()) : local.getWorldLocation();

        startPoints.add(playerLocation);

        log.debug("Player location: {} destination: {}, startPoints: {}",
                local.getWorldLocation(),
                destination.toWorldPoint(),
                startPoints
        );

        var sameDest = lastDestination != null && Objects.equals(destination, lastDestination);

        if (!sameDest) {
            lastPath = null;
        }

        var path = lastPath != null ? lastPath : buildPath(startPoints, destination, options, teleports, transports);

        lastDestination = destination;
        currentPath = path;

        if (path == null || path.isEmpty()) {
            log.error("Path is empty");
            return false;
        }

        if (handleDialogInterfaces()) {
            log.debug("Handled open dialog interfaces");
            return false;
        }

        int teleportDelay = walkerManager.getTeleportDelay();
        if (path.size() >= 2) {
            var offPath = path.get(1).distanceTo(local.getWorldLocation()) > 5 && !house.isInside();

            log.debug("Off path: {}", offPath);

            if (offPath || house.isInside()) {
                var start = path.get(1);
                var teleport = teleports.get(start);
                if (teleport != null) {
                    if (teleportDelay > 0) {
                        return false;
                    }

                    if (!handlePool(local, teleport)) {
                        return false;
                    }

                    if (players.getLocal().isAnimating() && !players.getLocal().isHealthBarVisible() || players.getLocal().getPoseAnimation() != players.getLocal().getIdlePoseAnimation()) {
                        return false;
                    }

                    var teleported = false;

                    log.info("Casting teleport to travel {} -> {} (poh: {})", players.getLocal().getWorldLocation(), teleport.getDestination(), teleport.isPoh());

                    var teleDelay = teleport.getTeleportDelay();

                    if (teleDelay > 0) {
                        walkerManager.setTeleportDelay(teleDelay);
                    }

                    var teleWalkerDelay = teleport.getWalkerDelay();

                    if (teleWalkerDelay > 0) {
                        walkerManager.setWalkerDelay(teleWalkerDelay);
                    }

                    try {
                        teleported = teleport.getHandler().call();
                    } catch (Exception e) {
                        log.error("Error while teleporting to: {}", teleport.getDestination(), e);
                    }

                    if (teleported) {
                        log.debug("Successfully cast teleport: {} (poh: {})", teleport.getDestination(), teleport.isPoh());
                        if (!teleport.isPoh()) {
                            lastPath = path;
                        }

                        return false;
                    } else {
                        log.warn("Failed to cast teleport: {} (poh: {})", teleport.getDestination(), teleport.isPoh());
                    }
                } else {
                    var offPathMessage = house.isInside() ? "We are in POH, no teleport found: {}" : "We are off path, no teleport found: {}";
                    log.warn(offPathMessage, start);
                }

                path = buildPath(startPoints, destination, options, teleports, transports);
            }
        }

        lastPath = null;

        return walkAlong(destination, path, transports, options) && !path.isIncomplete();
    }

    @Override
    public void setTeleportDelay(int delay) {
        walkerManager.setTeleportDelay(delay);
    }

    @Override
    public int getTeleportDelay() {
        return walkerManager.getTeleportDelay();
    }

    @Override
    public int getTransportDelay() {
        return walkerManager.getTransportDelay();
    }

    @Override
    public void setTransportDelay(int delay) {
        walkerManager.setTransportDelay(delay);
    }

    @Override
    public boolean walkTo(WorldArea destination, CollisionMap collisionMap, boolean useTeleports) {
        var options = WalkOptions.builder()
                .useTeleports(useTeleports)
                .collisionMap(collisionMap)
                .build();

        return walkTo(destination, options);
    }

    @Override
    public TilePath buildPath(
            Collection<WorldPoint> startPoints,
            WorldArea destination,
            WalkOptions options,
            HashMap<WorldPoint, Teleport> teleports,
            Map<WorldPoint, List<Transport>> transports
    ) {
        var ignoredDoors = transportLoader.getLastIgnoredDoors();

        if (transports.isEmpty() && options.isUseTransports()) {
            transports = buildTransportLinks(options);
        }

        var map = options.getCollisionMap();
        if (map instanceof LocalCollisionMap) {
            WorldPoint startPosition;
            if (startPoints.isEmpty()) {
                startPosition = players.getLocal().getWorldLocation();
            } else {
                startPosition = startPoints.iterator().next();
            }

            return new LocalPathfinder(
                    this,
                    (LocalCollisionMap) map,
                    transports,
                    startPosition,
                    destination
            ).find();
        }

        return new Pathfinder(
                this,
                map,
                transports,
                startPoints,
                destination,
                options.isAvoidWilderness(),
                inventory.getCount(true, ItemID.COINS),
                players.getLocal().getWorldLocation(),
                teleports,
                ignoredDoors
        ).find();
    }

    @Override
    public TilePath buildPath(
            Collection<WorldPoint> startPoints,
            List<WorldArea> targetAreas,
            WalkOptions options,
            HashMap<WorldPoint, Teleport> teleports,
            Map<WorldPoint, List<Transport>> transports
    ) {
        if (targetAreas == null || targetAreas.isEmpty()) {
            return TilePath.empty();
        }

        TilePath best = null;
        for (WorldArea area : targetAreas) {
            var path = buildPath(startPoints, area, options, teleports, transports);
            if (path == null || path.isEmpty()) {
                continue;
            }
            if (best == null || path.size() < best.size()) {
                best = path;
            }
        }
        return best != null ? best : TilePath.empty();
    }

    @Override
    public boolean walkAlong(WorldArea destination, TilePath path, Map<WorldPoint, List<Transport>> transports, WalkOptions options) {
        var remainingPath = remainingPath(path);

        if (handleTransports(destination, remainingPath, transports)) {
            return false;
        }

        return stepAlong(destination, remainingPath, options);
    }

    @Override
    public Map<WorldPoint, List<Transport>> buildTransportLinks(WalkOptions options) {
        Map<WorldPoint, List<Transport>> out = new HashMap<>();
        if (!options.isUseTransports()) {
            return out;
        }

        if (options.isUseCache()) {
            transportLoader.refreshTransports(options.getCachedItems());
        }

        var charters = CharterShip.getCharterShipTransports().stream().map(Transport::getSource).collect(Collectors.toSet());
        var gnomeGliders = GnomeGlider.getTransports().stream().map(Transport::getSource).collect(Collectors.toSet());
        var magicCarpets = MagicCarpet.getCarpetTransports().stream().map(Transport::getSource).collect(Collectors.toSet());

        for (var transport : transportLoader.buildTransports(options.isUseCache())) {
            if (!options.isUseCharterShips() && charters.contains(transport.getSource())) {
                continue;
            }

            if (!options.isUseGnomeGliders() && gnomeGliders.contains(transport.getSource())) {
                continue;
            }

            if (!options.isUseMagicCarpets() && magicCarpets.contains(transport.getSource())) {
                continue;
            }

            out.computeIfAbsent(transport.getSource(), x -> new ArrayList<>()).add(transport);
        }

        return out;
    }

    @Override
    public LinkedHashMap<WorldPoint, Teleport> buildTeleportLinks(WorldArea destination, WalkOptions options) {
        var out = new LinkedHashMap<WorldPoint, Teleport>();

        if (!options.isUseTeleports()) {
            return out;
        }

        teleportLoader.refreshTeleports(options.getCachedItems());

        var teleports = teleportLoader.buildTeleports(!options.getCachedItems().isEmpty());

        final var filteredTeleports = teleports.stream()
                .filter(x -> x.getDestination().distanceTo(players.getLocal().getWorldLocation()) >= 30 || x.isForceLoad())
                .collect(Collectors.toList());

        var fairyRingLocations = Arrays.stream(FairyRing.getAll())
                .map(FairyRing::getLocation)
                .toArray(WorldPoint[]::new);

        var spiritTreeLocations = Arrays.stream(SpiritTree.getAll())
                .map(SpiritTree::getPosition)
                .toArray(WorldPoint[]::new);

        var specialLocations = Stream.concat(Arrays.stream(fairyRingLocations), Arrays.stream(spiritTreeLocations))
                .toArray(WorldPoint[]::new);

        var outOfMap = (destination.getY() > 4158 || destination.getY() < 2400);

        filteredTeleports.forEach(teleport ->
        {
            var distanceWeight = normalizeDistance(teleport.getDestination().distanceTo(destination));
            var specialWeight = calculateSpecialProximityWeight(teleport, specialLocations);
            var proximityWeight = teleport.getDestination().distanceTo(destination) <= 20 ? 0.4 : 0;
            var planeTeleports = teleport.getDestination().getY() > 4158 ? 0.3 : 0;
            var diffPlane = teleport.getDestination().getPlane() != destination.getPlane() ? 0.3 : 0;
            var totalWeight = distanceWeight + proximityWeight + specialWeight + planeTeleports + diffPlane;

            teleport.setWeight(totalWeight);
        });

        filteredTeleports.sort(Comparator.comparingDouble(Teleport::getWeight).reversed()
                .thenComparing(Teleport::getPriority));

        var numberOfTeleportsToConsider = (outOfMap || house.isInside()) ? filteredTeleports.size() : calculateSelectedTeleportCount(filteredTeleports.size());
        var selectedTeleports = filteredTeleports.subList(0, Math.min(numberOfTeleportsToConsider, filteredTeleports.size()));

        var minigameTeleports = Arrays.stream(MinigameTeleport.values()).map(MinigameTeleport::getLocation).collect(Collectors.toSet());
        var homeTeleports = Stream.of(TeleportSpell.ARCEUUS_HOME_TELEPORT, TeleportSpell.LUMBRIDGE_HOME_TELEPORT, TeleportSpell.LUNAR_HOME_TELEPORT, TeleportSpell.EDGEVILLE_HOME_TELEPORT).map(TeleportSpell::getPoint).collect(Collectors.toSet());

        for (var teleport : selectedTeleports) {
            if (!options.isUseHomeTeleports() && teleport.isTimedTeleport() && homeTeleports.contains(teleport.getDestination())) {
                continue;
            }

            if (!options.isUseMinigameTeleports() && teleport.isTimedTeleport() && minigameTeleports.contains(teleport.getDestination())) {
                continue;
            }

            out.putIfAbsent(teleport.getDestination(), teleport);
        }

        log.debug("{} teleports loaded: {}", out.size(), out.keySet());

        return out;
    }

    @Override
    public LinkedHashMap<WorldPoint, Teleport> buildExperimentalTeleportLinks(WorldArea destination, WalkOptions options) {
        return buildTeleportLinks(destination, options);
    }

    @Override
    public HashMap<WorldPoint, Teleport> buildUnfilteredTeleportLinks(WalkOptions options) {
        var out = new HashMap<WorldPoint, Teleport>();
        if (!options.isUseTeleports()) {
            return out;
        }

        teleportLoader.refreshTeleports(options.getCachedItems());
        for (var teleport : teleportLoader.buildTeleports(!options.getCachedItems().isEmpty())) {
            out.put(teleport.getDestination(), teleport);
        }

        return out;
    }

    @Override
    public WorldPoint getNearestWalkableTile(WorldPoint source, CollisionMap collisionMap, Predicate<WorldPoint> filter) {
        if (!collisionMap.fullBlock(source) && filter.test(source)) {
            return source;
        }

        int currentIteration = 1;
        for (int radius = currentIteration; radius < MAX_NEAREST_SEARCH_ITERATIONS; radius++) {
            for (int x = -radius; x < radius; x++) {
                for (int y = -radius; y < radius; y++) {
                    WorldPoint p = source.dx(x).dy(y);
                    if (collisionMap.fullBlock(p) || !filter.test(p)) {
                        continue;
                    }
                    return p;
                }
            }
        }

        log.debug("Could not find a walkable tile near {}", source);
        return null;
    }

    private boolean stepAlong(WorldArea destination, TilePath path, WalkOptions options) {
        var reachablePath = reachablePath(path);
        if (reachablePath.isEmpty()) {
            return false;
        }

        var nextTileIdx = reachablePath.size() - 1;

        if (nextTileIdx <= options.getMinStepDistance()) {
            return step(destination, reachablePath.get(nextTileIdx));
        }

        if (nextTileIdx > options.getMaxStepDistance()) {
            nextTileIdx = options.getMaxStepDistance();
        }

        var targetDistance = Rand.nextInt(options.getMinStepDistance(), nextTileIdx);
        return step(destination, reachablePath.get(targetDistance));
    }

    private TilePath reachablePath(TilePath remainingPath) {
        var out = new TilePath(remainingPath.isEmpty());

        log.debug("Remaining path: {}", remainingPath);

        for (var p : remainingPath) {
            var tile = tiles.getAt(p);
            if (tile == null) {
                break;
            }

            out.add(p);
        }

        return out;
    }

    private boolean step(WorldArea walkerDestination, WorldPoint destination) {
        var local = players.getLocal();

        var undeadTree = getUndeadTree(local);
        if (undeadTree != null) {
            var undeadTreeTile = tiles.getAt(undeadTree.getWorldLocation());
            var localTile = tiles.getAt(local.getWorldLocation());
            var targetTile = tiles.getAt(destination);
            var surroundingPlayer = Area.offsetFrom(local.getWorldArea(), 1)
                    .toWorldPointList()
                    .stream().filter(reachable::isWalkable)
                    .collect(Collectors.toList());

            var nearestWalkableTile = surroundingPlayer.stream().filter(x -> {
                var tile = tiles.getAt(x);
                if (tile == null) {
                    return false;
                }

                if (x.equals(undeadTree.getWorldLocation())) {
                    return false;
                }

                var fromPlayer = List.of(localTile.getWorldLocation());
                var pathToTile = buildPath(fromPlayer, tile.getWorldLocation().toWorldArea(), WalkOptions.builder().build());
                if (pathToTile.isEmpty() || pathToTile.contains(undeadTreeTile.getWorldLocation())) {
                    return false;
                }

                var fromTile = List.of(tile.getWorldLocation());
                var pathFromTileToTarget = buildPath(fromTile, targetTile.getWorldLocation().toWorldArea(), WalkOptions.builder().build());

                return !pathFromTileToTarget.isEmpty() && !pathFromTileToTarget.contains(undeadTreeTile.getWorldLocation());
            }).findFirst().orElse(null);

            if (nearestWalkableTile != null) {
                log.warn("Stepping away from undead tree to {}", nearestWalkableTile);
                walk(nearestWalkableTile);
                return false;
            }

            log.warn("Could not find walkable tile near undead tree");
            return false;
        }

        destination = destination.isInScene(client.getWrapped())
                ? getNearestWalkableTile(destination, new LocalCollisionMap(false))
                : getNearestWalkableTile(destination);
        log.debug("Stepping towards {}", destination);

        if (local.getWorldLocation().equals(destination)) {
            log.info("Arrived at step destination: {}. Walker Destination: {}", destination, walkerDestination.toWorldPoint());
            return false;
        }

        walk(destination);
        return true;
    }

    private INPC getUndeadTree(IPlayer player) {
        return npcs.getNearest(tree -> {
            var name = tree.getName();
            return name != null
                    && (name.equalsIgnoreCase("tree") || name.equalsIgnoreCase("undead tree"))
                    && (Objects.equals(player, tree.getInteracting()) || tree.distanceTo2DHypotenuse(player.getWorldLocation()) == 1.0);
        });
    }

    private boolean handleTransports(WorldArea destination, TilePath path, Map<WorldPoint, List<Transport>> transports) {
        int transportDelay = walkerManager.getTransportDelay();
        if (transportDelay > 0) {
            return true;
        }

        return clientThread.invokeAndWait(() ->
        {
            if (dialog.isOpen()) {
                if (dialog.canContinue()) {
                    dialog.continueSpace();
                    return true;
                }
            }

            if (solaceConfig.proceedWarning()) {
                // Edgeville/ardy wilderness lever warning

                var leverWarningWidget = widgets.get(229, 1);
                if (widgets.isVisible(leverWarningWidget)) {
                    log.debug("Handling Wilderness lever warning widget");
                    dialog.continueSpace();
                    return true;
                }

                // Wilderness ditch warning
                var wildyDitchWidget = widgets.get(475, 11);
                if (widgets.isVisible(wildyDitchWidget)) {
                    log.debug("Handling Wilderness warning widget");
                    wildyDitchWidget.interact("Enter Wilderness");
                    return true;
                }

                if (dialog.getOptions().stream()
                        .anyMatch(widget -> widget.getText() != null && widget.getText().contains("Eeep! The Wilderness"))) {
                    log.debug("Handling wilderness warning dialog");
                    dialog.chooseOption("Yes, I'm brave.");
                    return true;
                }

                if (dialog.isOpen()) {
                    var title = dialog.getOptionTitle();
                    if (title != null && title.getText().contains("Pay 100,000 coins")) {
                        log.debug("Paying 100k coins as fee.");
                        return dialog.chooseOption("Yes.");
                    }

                    if (MovementConstants.isInStronghold()) {
                        if (dialog.chooseOption(MovementConstants.STRONGHOLD_ANSWERS::contains)) {
                            log.debug("Answering stronghold security questions.");
                            return true;
                        }
                    }
                }

                IWidget parentWarningWidget;

                if (client.isResized()) {
                    var isModern = vars.getBit(VarbitID.RESIZABLE_STONE_ARRANGEMENT) == 1;
                    parentWarningWidget = isModern
                            ? widgets.get(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE.getGroupId(), 16)
                            : widgets.get(WidgetInfo.RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX.getGroupId(), 16);
                } else {
                    parentWarningWidget = widgets.get(WidgetInfo.FIXED_VIEWPORT.getGroupId(), 40);
                }

                if (widgets.isVisible(parentWarningWidget)) {
                    var warningWidget = Arrays.stream(parentWarningWidget.getNestedChildren()).filter(Objects::nonNull)
                            .filter(widget -> widget.getText() != null && widget.getText().contains("Warning!"))
                            .findFirst()
                            .orElse(null);

                    if (widgets.isVisible(warningWidget)) {
                        var confirmWidget = Arrays.stream(parentWarningWidget.getNestedChildren()).filter(Objects::nonNull)
                                .filter(widget -> widget.hasAction("Yes") || widget.hasAction("Jump!"))
                                .findFirst()
                                .orElse(null);

                        if (widgets.isVisible(confirmWidget)) {
                            log.debug("Handling confirm warning widget");
                            client.interact(
                                    AutomatedMenu.builder()
                                            .identifier(1)
                                            .opcode(MenuAction.CC_OP)
                                            .param0(-1)
                                            .param1(confirmWidget.getId())
                                            .build()
                            );
                            return true;
                        }
                    }
                }
            }

            for (var i = 0; i < Math.min(20, solaceConfig.maxStepDistance()); i++) {
                if (i + 1 >= path.size()) {
                    break;
                }

                var a = path.get(i);
                var b = path.get(i + 1);

                var tileA = tiles.getAt(a);
                var tileB = tiles.getAt(b);

                var distanceBetweenTiles = a.distanceTo(b) >= 1;

                if (distanceBetweenTiles || (tileA != null && tileB != null && !reachable.isWalkable(b))) {
                    var transport = transports.getOrDefault(a, List.of()).stream()
                            .filter(t -> t.getSource().equals(a) && t.getDestination().equals(b))
                            .min(Comparator.comparing(t -> t.getDestination().distanceTo(destination)))
                            .orElse(null);

                    if (transport != null) {
                        if (players.getLocal().isMoving()) {
                            return true;
                        }
                        log.info("Trying to use transport at {} to move {} -> {}", transport.getSource(), a, b);
                        var result = false;

                        try {
                            result = transport.getHandler().call();
                        } catch (Exception e) {
                            log.error("Error handling transport", e);
                        }

                        if (result) {
                            walkerManager.setTransportDelay(transport.getDelay());
                            return result;
                        }
                    }
                }

                // MLM Rocks
                var rockfall = tileObjects.getFirstAt(a, "Rockfall");
                var hasPickaxe = inventory.contains(Predicates.nameContains("pickaxe")) || equipment.contains(Predicates.nameContains("pickaxe"));
                if (rockfall != null && hasPickaxe) {
                    log.debug("Handling MLM rockfall");
                    if (!players.getLocal().isIdle()) {
                        return true;
                    }
                    rockfall.interact("Mine");
                    return true;
                }

                if (tileA == null) {
                    return false;
                }

                // Diagonal door bullshit
                if (Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() + b.getY()) > 1 && a.getPlane() == b.getPlane()) {
                    var wall = tileObjects.getFirstAt(tileA, it -> !(it instanceof IWallObject) && it.getName() != null && it.getName().equals("Door")
                    );
                    if (wall != null && wall.hasAction("Open")) {
                        if (players.getLocal().isMoving()) {
                            return true;
                        }
                        log.debug("Handling diagonal door at {}", wall.getWorldLocation());
                        wall.interact("Open");
                        return true;
                    }
                }

                if (tileB == null) {
                    return false;
                }

                // Normal doors
                if (handleDoor(tileA, tileB)) {
                    return true;
                }

                if (handleDoor(tileB, tileA)) {
                    return true;
                }
            }

            log.debug("Could not find any valid transports to use, walking");
            return false;
        });
    }

    private boolean handleDoor(ITile first, ITile second) {
        if (reachable.isDoored(first, second)) {
            if (players.getLocal().isMoving()) {
                return true;
            }
            var wall = first.getWallObject();

            if (wall == null || wall.getActions() == null) {
                return false;
            }

            var actions = Arrays.stream(wall.getActions())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.debug("Handling door {} -> {}, {}", first.getWorldLocation(), second.getWorldLocation(), actions);

            wall.interact("Open", "Pass");
            return true;
        }

        return false;
    }

    private TilePath remainingPath(TilePath path) {
        var local = players.getLocal();
        if (local == null) {
            return TilePath.empty();
        }

        log.debug("Remaining path: {}", path.size());
        return path;
    }

    private boolean isWebbed(WorldPoint location) {
        return tileObjects.getFirstAt(location, x -> x.getName() != null
                && (x.getName().equalsIgnoreCase("Slashed web") || x.getName().equalsIgnoreCase("Web"))
                && x.hasAction("Slash")) != null;
    }

    private static double normalizeDistance(int distance) {
        var MAX_DISTANCE = 2500;
        double normalizedDistance = Math.min(distance, MAX_DISTANCE);

        var weight = 1.0 - (normalizedDistance / MAX_DISTANCE);

        return Math.max(0, Math.min(weight, 1));
    }

    private static int calculateSelectedTeleportCount(int totalTeleports) {
        final var MIN_SELECTION = 50;

        if (totalTeleports <= MIN_SELECTION) {
            return totalTeleports;
        }

        return Math.min(totalTeleports / 3, totalTeleports);
    }

    private static double calculateSpecialProximityWeight(Teleport teleport, WorldPoint[] specialLocations) {
        var destination = teleport.getDestination();

        if (Arrays.asList(specialLocations).contains(destination)) {
            return 0;
        }

        var minDistance = Arrays.stream(specialLocations)
                .mapToInt(location -> location.distanceTo(destination))
                .min().orElse(Integer.MAX_VALUE);

        return minDistance < 30 ? 0.3 : 0;
    }

    private boolean handlePool(IPlayer player, Teleport teleport) {
        if (solaceConfig.usePool()) {
            log.debug("Trying to use restore object before teleporting");
            var pohPool = PohPool.get();
            if (pohPool != null) {
                log.debug("Pool found: {}", pohPool);
                var teleportObject = getTeleportObject(teleport);
                if (teleportObject != null) {
                    log.debug("Teleport object found: {}", teleportObject.getName());
                }
                if (!pohPool.isUsed() && (teleportObject == null || teleportObject.distanceTo(player.getWorldLocation()) > 1)) {
                    if (players.getLocal().isMoving()) {
                        return false;
                    }
                    pohPool.getObject().interact("Drink", "Pray");
                    walkerManager.setWalkerDelay(3);
                    return false;
                } else {
                    log.debug("Restore object already used, skipping usage");
                }
            }
        }
        return true;
    }

    private LinkedHashMap<WorldPoint, Teleport> filterTeleportsByOptions(LinkedHashMap<WorldPoint, Teleport> teleports, WalkOptions options, IPlayer local) {
        var targetingNpc = npcs.getNearest(x -> {
            var interacting = x.getInteracting();

            return interacting != null && Objects.equals(interacting, local) && x.hasAction("Attack");
        });

        var shouldUseTimedTeleport = !local.isHealthBarVisible() && targetingNpc == null;

        if (vars.getBit(VarbitID.TELEBLOCK_CYCLES) > 0) {
            teleports.values().forEach(t -> t.setMaximumWildernessLevel(0));
        }

        if (!options.isUseHomeTeleports() || !shouldUseTimedTeleport) {
            teleports.values().removeIf(Teleport::isHomeTeleport);
        }

        if (!options.isUseMinigameTeleports() || !shouldUseTimedTeleport) {
            teleports.values().removeIf(Teleport::isMinigameTeleport);
        }

        return teleports;
    }

    private ITileObject getTeleportObject(Teleport teleport) {
        if (teleport.getObjectIdRequirements() != null) {
            return tileObjects.getNearest(teleport.getObjectIdRequirements());
        }

        if (teleport.getObjectNameRequirements() != null) {
            return tileObjects.getNearest(x -> {
                var name = x.getName();
                return name != null && Arrays.stream(teleport.getObjectNameRequirements()).anyMatch(name::contains);
            });
        }

        return null;
    }

    private boolean handleDialogInterfaces() {
        if (bank.isOpen() || grandExchange.isOpen()) {
            widgets.closeInterfaces();
            return true;
        }

        if (dialog.isEnterInputOpen()) {
            dialog.forceClose();
            return true;
        }

        return false;
    }
}
