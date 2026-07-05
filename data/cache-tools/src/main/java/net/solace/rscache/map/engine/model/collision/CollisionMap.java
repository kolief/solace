package net.solace.rscache.map.engine.model.collision;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.solace.rscache.map.engine.model.map.Chunk;
import net.solace.rscache.map.engine.model.map.Tile;
import net.solace.rscache.map.engine.model.obj.GameObject;
import net.solace.rscache.map.engine.model.obj.ObjectShape;
import net.solace.rscache.map.engine.solace.Transport;
import net.solace.rscache.map.engine.solace.TransportLoader;
import net.solace.rscache.util.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Data
public class CollisionMap {
    private int doorCount;
    private int rockfallCount;
    private int otherCount;

    private final List<String> doorNames = List.of(
            "Door",
            "Gate",
            "Large door",
            "Castle door",
            "Tent door"
    );

    private final List<String> ignoredNames = List.of(
            "Rockfall"
    );

    private final List<String> ignoredActions = List.of(
            "Mine"
    );

    private final List<Tile> ignoreLocations = List.of(
            new Tile(2650, 3470, 0),
            new Tile(2649, 3470, 0),
            new Tile(3007, 3515, 0),
            new Tile(3007, 3516, 0),
            new Tile(2502, 3250, 0),
            new Tile(2174, 4724, 1),
            new Tile(2174, 4726, 1)
    );

    private final List<Integer> doorIds = List.of(
            1533,
            1568,
            1569
    );

    private final List<Integer> forcedIds = List.of(
            190
    );

    private final List<Transport> transports = TransportLoader.loadTransports();

    private final int[][] flags = new int[2048 * 2048 * 4][];

    public void printCounts() {
        log.info("Doors: {}, MLM Rockfall: {}, Other: {}", doorCount, rockfallCount, otherCount);
    }

    public int[] alloc(Chunk chunk) {
        var packed = chunk.packed();
        var current = flags[packed];
        if (current != null) {
            return current;
        }

        var newFlags = new int[8 * 8];
        flags[packed] = newFlags;
        return newFlags;
    }

    public int get(int x, int y, int plane) {
        var chunk = new Chunk(x >> 3, y >> 3, plane);
        var array = flags[chunk.packed()];
        if (array == null) {
            return 0;
        }

        return array[chunkIndex(x, y)];
    }

    public void set(int x, int y, int plane, int flag) {
        alloc(new Chunk(x >> 3, y >> 3, plane))[chunkIndex(x, y)] = flag;
    }

    public void add(int x, int y, int plane, int flag) {
        var flags = alloc(new Chunk(x >> 3, y >> 3, plane));
        var index = chunkIndex(x, y);
        var current = flags[index];
        flags[index] = current | flag;
    }

    public void add(Tile tile, int flag) {
        add(tile.x(), tile.y(), tile.plane(), flag);
    }

    public void remove(int x, int y, int plane, int flag) {
        var flags = alloc(new Chunk(x >> 3, y >> 3, plane));
        var index = chunkIndex(x, y);
        var current = flags[index];
        flags[index] = current & ~flag;
    }

    public void remove(Tile tile, int flag) {
        remove(tile.x(), tile.y(), tile.plane(), flag);
    }

    public void addObject(GameObject obj) {
        changeObject(obj);
    }

    private void changeObject(GameObject obj) {
        var data = obj.data();
        var tile = obj.tile();
        var shape = obj.shape();
        var rotation = obj.rotation();
        var clipType = data.getInteractType();
        var blockPath = data.getInteractType() != 0;
        var blockProjectile = data.isBlocksProjectile();

        // Skip adding collision for any object which is labeled as a type of door excluding the gnome stronghold gate -> id == 190
        if (isDoor(obj)
            && !forcedIds.contains(data.getId())
            && !ignoreLocations.contains(tile)) {
            doorCount++;
            return;
        }

        var actions = actionTexts(data);
        if (ignoredNames.contains(data.getName()) && actions.stream().anyMatch(ignoredActions::contains)) {
            rockfallCount++;
            return;
        }

        if (ArrayUtils.contains(ObjectShape.WALL_SHAPES, shape) && clipType != 0) {
            changeWall(tile, rotation, shape, blockProjectile, true);
            return;
        }

        if (ArrayUtils.contains(ObjectShape.NORMAL_SHAPES, shape) && clipType != 0) {
            var width = data.getSizeX();
            var length = data.getSizeY();
            if (rotation == 1 || rotation == 3) {
                width = data.getSizeY();
                length = data.getSizeX();
            }

            changeNormal(tile, width, length, blockPath, blockProjectile, true);
            return;
        }

        if (ArrayUtils.contains(ObjectShape.GROUND_DECOR_SHAPES, shape) && clipType == 1) {
            changeFloorDecor(tile, true);
        }
    }

    private void changeWall(Tile tile, int rotation, int shape, boolean blockProjectile, boolean add) {
        switch (shape) {
            case ObjectShape.WALL -> {
                switch (rotation) {
                    case 0 -> {
                        change(tile, CollisionFlag.WALL_WEST, add);
                        change(tile.translate(-1, 0), CollisionFlag.WALL_EAST, add);
                        if (blockProjectile) {
                            change(tile, CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER, add);
                            change(tile.translate(-1, 0), CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 1 -> {
                        change(tile, CollisionFlag.WALL_NORTH, add);
                        change(tile.translate(0, 1), CollisionFlag.WALL_SOUTH, add);
                        if (blockProjectile) {
                            change(tile, CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER, add);
                            change(tile.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 2 -> {
                        change(tile, CollisionFlag.WALL_EAST, add);
                        change(tile.translate(1, 0), CollisionFlag.WALL_WEST, add);
                        if (blockProjectile) {
                            change(tile, CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER, add);
                            change(tile.translate(1, 0), CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 3 -> {
                        change(tile, CollisionFlag.WALL_SOUTH, add);
                        change(tile.translate(0, -1), CollisionFlag.WALL_NORTH, add);
                        if (blockProjectile) {
                            change(tile, CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER, add);
                            change(tile.translate(0, -1), CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER, add);
                        }
                    }
                }
            }

            case ObjectShape.WALL_CORNER_DIAG, ObjectShape.WALL_CORNER -> {
                switch (rotation) {
                    case 0 -> {
                        change(tile, CollisionFlag.WALL_NORTH_WEST, add);
                        change(tile.translate(-1, 1), CollisionFlag.WALL_SOUTH_EAST, add);
                        if (blockProjectile) {
                            change(tile, CollisionFlag.WALL_NORTH_WEST_PROJECTILE_BLOCKER, add);
                            change(tile.translate(-1, 1), CollisionFlag.WALL_SOUTH_EAST_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 1 -> {
                        change(tile, CollisionFlag.WALL_NORTH_EAST, add);
                        change(tile.translate(1, 1), CollisionFlag.WALL_SOUTH_WEST, add);
                        if (blockProjectile) {
                            change(tile, CollisionFlag.WALL_NORTH_EAST_PROJECTILE_BLOCKER, add);
                            change(tile.translate(1, 1), CollisionFlag.WALL_SOUTH_WEST_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 2 -> {
                        change(tile, CollisionFlag.WALL_SOUTH_EAST, add);
                        change(tile.translate(1, -1), CollisionFlag.WALL_NORTH_WEST, add);
                        if (blockProjectile) {
                            change(tile, CollisionFlag.WALL_SOUTH_EAST_PROJECTILE_BLOCKER, add);
                            change(tile.translate(1, -1), CollisionFlag.WALL_NORTH_WEST_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 3 -> {
                        change(tile, CollisionFlag.WALL_SOUTH_WEST, add);
                        change(tile.translate(-1, -1), CollisionFlag.WALL_NORTH_EAST, add);
                        if (blockProjectile) {
                            change(tile, CollisionFlag.WALL_SOUTH_WEST_PROJECTILE_BLOCKER, add);
                            change(tile.translate(-1, -1), CollisionFlag.WALL_NORTH_EAST_PROJECTILE_BLOCKER, add);
                        }
                    }
                }
            }

            case ObjectShape.UNFINISHED_WALL -> {
                switch (rotation) {
                    case 0 -> {
                        change(tile, CollisionFlag.WALL_WEST | CollisionFlag.WALL_NORTH, add);
                        change(tile.translate(-1, 0), CollisionFlag.WALL_EAST, add);
                        change(tile.translate(0, 1), CollisionFlag.WALL_SOUTH, add);
                        if (blockProjectile) {
                            var flag =
                                    CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER | CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER;
                            change(tile, flag, add);
                            change(tile.translate(-1, 0), CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER, add);
                            change(tile.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 1 -> {
                        change(tile, CollisionFlag.WALL_NORTH | CollisionFlag.WALL_EAST, add);
                        change(tile.translate(0, 1), CollisionFlag.WALL_SOUTH, add);
                        change(tile.translate(1, 0), CollisionFlag.WALL_WEST, add);
                        if (blockProjectile) {
                            var flag =
                                    CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER | CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER;
                            change(tile, flag, add);
                            change(tile.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER, add);
                            change(tile.translate(1, 0), CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 2 -> {
                        change(tile, CollisionFlag.WALL_EAST | CollisionFlag.WALL_SOUTH, add);
                        change(tile.translate(1, 0), CollisionFlag.WALL_WEST, add);
                        change(tile.translate(0, -1), CollisionFlag.WALL_NORTH, add);
                        if (blockProjectile) {
                            var flag =
                                    CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER | CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER;
                            change(tile, flag, add);
                            change(tile.translate(1, 0), CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER, add);
                            change(tile.translate(0, -1), CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER, add);
                        }
                    }

                    case 3 -> {
                        change(tile, CollisionFlag.WALL_SOUTH | CollisionFlag.WALL_WEST, add);
                        change(tile.translate(0, -1), CollisionFlag.WALL_NORTH, add);
                        change(tile.translate(-1, 0), CollisionFlag.WALL_EAST, add);
                        if (blockProjectile) {
                            var flag =
                                    CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER | CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER;
                            change(tile, flag, add);
                            change(tile.translate(0, -1), CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER, add);
                            change(tile.translate(-1, 0), CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER, add);
                        }
                    }
                }
            }
        }
    }

    private void changeNormal(Tile tile, int width, int length, boolean blockPath, boolean blockProjectile, boolean add) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                var translatedTile = tile.translate(x, y, 0);
                change(translatedTile, CollisionFlag.OBJECT, add);

                if (blockProjectile) {
                    change(translatedTile, CollisionFlag.OBJECT_PROJECTILE_BLOCKER, add);
                }

                if (blockPath) {
                    change(translatedTile, CollisionFlag.OBJECT_ROUTE_BLOCKER, add);
                }
            }
        }
    }

    private void changeFloorDecor(Tile tile, boolean add) {
        change(tile, CollisionFlag.FLOOR_DECORATION, add);
    }

    private void change(Tile tile, int mask, boolean add) {
        if (add) {
            add(tile, mask);
        } else {
            remove(tile, mask);
        }
    }

    private boolean isDoor(GameObject obj) {
        return (doorNames.contains(obj.data().getName()) || doorIds.contains(obj.data().getId()))
               && actionTexts(obj.data()).contains("Open")
               && !isTransport(obj);
    }

    private boolean isTransport(GameObject obj) {
        return transports.stream().anyMatch(t ->
                t.objectId() == obj.id() && t.source().toTile().equals(obj.tile()) && t.action().equals("Open"));
    }

    private int chunkIndex(int x, int y) {
        return (x & 0x7) | ((y & 0x7) << 3);
    }

    private static List<String> actionTexts(net.runelite.cache.definitions.ObjectDefinition data) {
        var ops = data.getOps();
        if (ops == null || ops.getOps() == null) {
            return List.of();
        }
        return ops.getOps().stream()
                .filter(Objects::nonNull)
                .map(op -> op.text)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
