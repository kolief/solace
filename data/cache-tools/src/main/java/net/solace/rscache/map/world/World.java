package net.solace.rscache.map.world;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.definitions.LocationsDefinition;
import net.runelite.cache.definitions.MapDefinition;
import net.runelite.cache.region.Region;
import net.solace.api.movement.pathfinder.BitSet4D;
import net.solace.api.movement.pathfinder.GlobalCollisionMap;
import net.solace.rscache.map.cache.GameCache;
import net.solace.rscache.map.engine.model.collision.CollisionFlag;
import net.solace.rscache.map.engine.model.collision.CollisionMap;
import net.solace.rscache.map.engine.model.collision.Direction;
import net.solace.rscache.map.engine.model.collision.TranslatedMap;
import net.solace.rscache.map.engine.model.map.Tile;
import net.solace.rscache.map.engine.model.obj.GameObject;
import net.solace.rscache.map.xtea.XteaConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class World {
    private static final int MAX_PLANE = 4;
    private static final int BLOCKED_TILE_FLAG = 0x1;
    private static final int BRIDGE_TILE_FLAG = 0x2;

    private final GameCache cache;
    private final CollisionMap collisionMap = new CollisionMap();
    private final List<Tile> filteredTiles = new ArrayList<>();
    private final List<Integer> fairyRings = List.of(29495, 29560, 12094, 12003);

    private int fairyRingCount;
    private int filtered;
    private int totalObjects;

    public void load(XteaConfig xteaConfig) {
        for (var regionId : xteaConfig.getRegionIds()) {
            var pair = cache.getMaps().get(regionId);
            if (pair == null) {
                continue;
            }

            var map = pair.left();
            var loc = pair.right();
            var region = new Region(regionId);

            loadCollisionsForRegion(region, map, loc);
        }

        log.info("Fairy rings: {}", fairyRingCount);
        log.info("Filtered: {}", filtered);
        log.info("Total objects: {}", totalObjects);
        collisionMap.printCounts();
    }

    public boolean isObstacle(int flag) {
        if (flag == 0) {
            return false;
        }

        return check(flag, CollisionFlag.BLOCK_FULL);
    }

    public boolean check(int flag, int checkFlag) {
        return ((flag & checkFlag) != 0);
    }

    public boolean isWalled(int flag, Direction direction) {
        return check(flag, direction.getFlag());
    }

    private void loadCollisionsForRegion(Region region, MapDefinition map, LocationsDefinition loc) {
        var floorMask = new HashMap<Tile, Integer>();

        region.loadTerrain(map);
        region.loadLocations(loc);

        for (int plane = 0; plane < MAX_PLANE; plane++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    floorMask.put(new Tile(x, y, plane), (int) region.getTileSetting(plane, x, y));
                }
            }
        }

        var objects = new ArrayList<GameObject>();

        for (var location : loc.getLocations()) {
            var localX = location.getPosition().getX();
            var localY = location.getPosition().getY();

            if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) {
                continue;
            }

            var shape = location.getType();
            var rotation = location.getOrientation();
            var plane = location.getPosition().getZ();
            var localTile = new Tile(localX, localY, 1);
            var floor = floorMask.getOrDefault(localTile, 0);
            if ((floor & BRIDGE_TILE_FLAG) == BRIDGE_TILE_FLAG) {
                plane--;
            }

            if (plane >= 0) {
                var data = cache.getConfigs().getObjects().get(location.getId());
                if (data == null) {
                    throw new RuntimeException("Failed to find cache object config for object ID: " + location.getId());
                }

                var tile = new net.solace.rscache.map.engine.model.map.Region(region.getRegionID())
                        .toTile(plane)
                        .translate(localX, localY);

                if (fairyRings.contains(location.getId())) {
                    filteredTiles.add(tile);
                    fairyRingCount++;
                    log.info("Fairy Ring [{}]: new WorldPoint({}, {}, {}) {}", location.getId(), tile.x(), tile.y(), tile.plane(), getMapLink(tile));
                }

                if (tile.x() == 2914 && tile.y() == 5300 && tile.plane() == 1) {
                    filteredTiles.add(tile);
                    filtered++;
                    log.info("Filtered: new WorldPoint({}, {}, {}) {}", tile.x(), tile.y(), tile.plane(), getMapLink(tile));
                }

                objects.add(new GameObject(data, tile, shape, rotation));
            }
        }

        totalObjects += objects.size();

        for (var object : objects) {
            collisionMap.addObject(object);
        }

        for (int plane = 0; plane < MAX_PLANE; plane++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    var localTile = new Tile(x, y, plane);
                    var localMask = floorMask.getOrDefault(localTile, 0);
                    if ((localMask & BLOCKED_TILE_FLAG) != BLOCKED_TILE_FLAG) {
                        continue;
                    }

                    var adjustedPlane = plane;
                    var bridgeTile = new Tile(x, y, 1);
                    var bridgeMask = floorMask.getOrDefault(bridgeTile, 0);
                    if ((bridgeMask & BRIDGE_TILE_FLAG) == BRIDGE_TILE_FLAG) {
                        adjustedPlane--;
                    }

                    if (adjustedPlane >= 0) {
                        var tile = new net.solace.rscache.map.engine.model.map.Region(region.getRegionID())
                                .toTile(adjustedPlane)
                                .translate(x, y);
                        if (filteredTiles.contains(tile)) {
                            log.info("Filtered floor: new WorldPoint({}, {}, {}) {}", tile.x(), tile.y(), tile.plane(), getMapLink(tile));
                        }

                        collisionMap.add(tile, CollisionFlag.FLOOR);
                    }
                }
            }
        }
    }

    public byte[] dumpCollisionMap(XteaConfig xteaConfig) {
        var globalCollisionMap = new GlobalCollisionMap();
        var regions = globalCollisionMap.regions;
        for (int i = 0; i < regions.length; i++) {
            regions[i] = new BitSet4D(64, 64, 4, 2);
        }

        for (var filteredTile : filteredTiles) {
            collisionMap.set(filteredTile.x(), filteredTile.y(), filteredTile.plane(), 0);
        }

        for (var regionId : xteaConfig.getRegionIds()) {
            for (int plane = 0; plane < MAX_PLANE; plane++) {
                for (int x = 0; x < 64; x++) {
                    for (int y = 0; y < 64; y++) {
                        var tile = new net.solace.rscache.map.engine.model.map.Region(regionId)
                                .toTile(plane)
                                .translate(x, y);
                        var tileX = tile.x();
                        var tileY = tile.y();
                        var tilePlane = tile.plane();

                        var flag = collisionMap.get(tileX, tileY, tilePlane);

                        var north = collisionMap.get(tileX, tileY + 1, tilePlane);
                        var east = collisionMap.get(tileX + 1, tileY, tilePlane);

                        if (isObstacle(flag)) {
                            globalCollisionMap.set(tileX, tileY, tilePlane, 0, false);
                            globalCollisionMap.set(tileX, tileY, tilePlane, 1, false);
                            continue;
                        }

                        globalCollisionMap.set(tileX, tileY, tilePlane, 0, true);
                        globalCollisionMap.set(tileX, tileY, tilePlane, 1, true);

                        if (isWalled(flag, Direction.NORTH) || isObstacle(north)) {
                            globalCollisionMap.set(tileX, tileY, tilePlane, 0, false);
                        }

                        if (isWalled(flag, Direction.EAST) || isObstacle(east)) {
                            globalCollisionMap.set(tileX, tileY, tilePlane, 1, false);
                        }
                    }
                }
            }
        }

        var translated = new TranslatedMap(globalCollisionMap);
        return translated.gzipped();
    }

    private String getMapLink(Tile tile) {
        return "https://map.pyfa.dev/map?x=%s&y=%s&z=%s".formatted(tile.x(), tile.y(), tile.plane());
    }
}
