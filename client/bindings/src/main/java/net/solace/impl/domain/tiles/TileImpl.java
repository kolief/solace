package net.solace.impl.domain.tiles;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.ItemLayer;
import net.runelite.api.Point;
import net.runelite.api.SceneTileModel;
import net.runelite.api.SceneTilePaint;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.Calculations;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.IDecorativeObject;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.IGroundObject;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.domain.tiles.IWallObject;
import net.solace.impl.util.RuneLiteWrapperUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class TileImpl implements ITile {
    @Setter
    private Tile wrapped;
    private final IClient client;
    private IDecorativeObject decorativeObject;
    private final List<IGameObject> iGameObjects;
    private IGroundObject groundObject;
    private IWallObject wallObject;
    private final List<ITileItem> iGroundItems;
    private final ITile bridge;
    private final List<ITileObject> tileObjects;

    public TileImpl(Tile wrapped, IClient client) {
        this.wrapped = wrapped;
        this.client = client;
        this.bridge = of(wrapped.getBridge(), client);
        this.decorativeObject = DecorativeObjectImpl.of(wrapped.getDecorativeObject(), this, client);
        this.groundObject = GroundObjectImpl.of(wrapped.getGroundObject(), this, client);
        this.wallObject = WallObjectImpl.of(wrapped.getWallObject(), this, client);
        this.iGameObjects = gameObjects();
        this.iGroundItems = groundItems();
        this.tileObjects = tileObjects();
    }

    public static ITile of(Tile tile, IClient client) {
        if (tile == null) {
            return null;
        }

        return new TileImpl(tile, client);
    }

    @Override
    public GameObject[] getGameObjects() {
        return wrapped.getGameObjects();
    }

    @Override
    public ItemLayer getItemLayer() {
        return wrapped.getItemLayer();
    }

    public void removeGroundObject() {
        this.groundObject = null;
    }

    public void removeDecorativeObject() {
        this.decorativeObject = null;
    }

    public void removeWallObject() {
        this.wallObject = null;
    }

    @Override
    public void setGroundObject(GroundObject groundObject) {
        wrapped.setGroundObject(groundObject);
    }

    @Override
    public SceneTilePaint getSceneTilePaint() {
        return wrapped.getSceneTilePaint();
    }

    @Override
    public void setSceneTilePaint(SceneTilePaint sceneTilePaint) {
        wrapped.setSceneTilePaint(sceneTilePaint);
    }

    @Override
    public SceneTileModel getSceneTileModel() {
        return wrapped.getSceneTileModel();
    }

    @Override
    public void setSceneTileModel(SceneTileModel sceneTileModel) {
        wrapped.setSceneTileModel(sceneTileModel);
    }

    @Override
    public Point getSceneLocation() {
        return wrapped.getSceneLocation();
    }

    @Override
    public int getPlane() {
        return wrapped.getPlane();
    }

    @Override
    public int getRenderLevel() {
        return wrapped.getRenderLevel();
    }

    @Override
    public List<TileItem> getGroundItems() {
        return wrapped.getGroundItems();
    }

    @Override
    public WorldPoint getWorldLocation() {
        return wrapped.getWorldLocation();
    }

    @Override
    public LocalPoint getLocalLocation() {
        return wrapped.getLocalLocation();
    }

    @Override
    public int hashCode() {
        return RuneLiteWrapperUtil.getHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return RuneLiteWrapperUtil.isEqual(this, obj);
    }

    @Override
    public boolean isObstructed() {
        return Calculations.isObstacle(client.getWrapped(), getWorldLocation());
    }

    @Override
    public boolean isEmpty() {
        return hasNoGameObjects()
               && getDecorativeObject() == null
               && getWallObject() == null
               && getGroundObject() == null;
    }

    @Override
    public boolean hasLineOfSightTo(ITile other) {
        if (this.getPlane() != other.getPlane()) {
            return false;
        }

        var collisionData = client.getWrapped().getCollisionMaps();
        if (collisionData == null) {
            return false;
        }

        var z = this.getPlane();
        var collisionDataFlags = collisionData[z].getFlags();

        var p1 = this.getSceneLocation();
        var p2 = other.getSceneLocation();
        if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
            return true;
        }

        var dx = p2.getX() - p1.getX();
        var dy = p2.getY() - p1.getY();
        var dxAbs = Math.abs(dx);
        var dyAbs = Math.abs(dy);

        var xFlags = CollisionDataFlag.BLOCK_LINE_OF_SIGHT_FULL;
        var yFlags = CollisionDataFlag.BLOCK_LINE_OF_SIGHT_FULL;
        if (dx < 0) {
            xFlags |= CollisionDataFlag.BLOCK_LINE_OF_SIGHT_EAST;
        } else {
            xFlags |= CollisionDataFlag.BLOCK_LINE_OF_SIGHT_WEST;
        }
        if (dy < 0) {
            yFlags |= CollisionDataFlag.BLOCK_LINE_OF_SIGHT_NORTH;
        } else {
            yFlags |= CollisionDataFlag.BLOCK_LINE_OF_SIGHT_SOUTH;
        }

        if (dxAbs > dyAbs) {
            var x = p1.getX();
            var yBig = p1.getY() << 16; // The y position is represented as a bigger number to handle rounding
            var slope = (dy << 16) / dxAbs;
            yBig += 0x8000; // Add half of a tile
            if (dy < 0) {
                yBig--; // For correct rounding
            }
            var direction = dx < 0 ? -1 : 1;

            while (x != p2.getX()) {
                x += direction;
                var y = yBig >>> 16;
                if ((collisionDataFlags[x][y] & xFlags) != 0) {
                    // Collision while traveling on the x axis
                    return false;
                }
                yBig += slope;
                var nextY = yBig >>> 16;
                if (nextY != y && (collisionDataFlags[x][nextY] & yFlags) != 0) {
                    // Collision while traveling on the y axis
                    return false;
                }
            }
        } else {
            var y = p1.getY();
            var xBig = p1.getX() << 16; // The x position is represented as a bigger number to handle rounding
            var slope = (dx << 16) / dyAbs;
            xBig += 0x8000; // Add half of a tile
            if (dx < 0) {
                xBig--; // For correct rounding
            }
            var direction = dy < 0 ? -1 : 1;

            while (y != p2.getY()) {
                y += direction;
                var x = xBig >>> 16;
                if ((collisionDataFlags[x][y] & yFlags) != 0) {
                    // Collision while traveling on the y axis
                    return false;
                }
                xBig += slope;
                var nextX = xBig >>> 16;
                if (nextX != x && (collisionDataFlags[nextX][y] & xFlags) != 0) {
                    // Collision while traveling on the x axis
                    return false;
                }
            }
        }

        // No collision
        return true;
    }

    @Override
    public List<ITile> pathTo(ITile other) {
        var z = this.getPlane();
        if (z != other.getPlane()) {
            return null;
        }

        var collisionData = client.getWrapped().getCollisionMaps();
        if (collisionData == null) {
            return null;
        }

        var directions = new int[128][128];
        var distances = new int[128][128];
        var bufferX = new int[4096];
        var bufferY = new int[4096];

        // Initialise directions and distances
        for (var i = 0; i < 128; ++i) {
            for (var j = 0; j < 128; ++j) {
                directions[i][j] = 0;
                distances[i][j] = Integer.MAX_VALUE;
            }
        }

        var p1 = this.getSceneLocation();
        var p2 = other.getSceneLocation();

        var middleX = p1.getX();
        var middleY = p1.getY();
        var currentX = middleX;
        var currentY = middleY;
        var offsetX = 64;
        var offsetY = 64;
        // Initialise directions and distances for starting tile
        directions[offsetX][offsetY] = 99;
        distances[offsetX][offsetY] = 0;
        var index1 = 0;
        bufferX[0] = currentX;
        var index2 = 1;
        bufferY[0] = currentY;
        var collisionDataFlags = collisionData[z].getFlags();

        var isReachable = false;

        while (index1 != index2) {
            currentX = bufferX[index1];
            currentY = bufferY[index1];
            index1 = index1 + 1 & 4095;
            // currentX is for the local coordinate while currentMapX is for the index in the directions and distances arrays
            var currentMapX = currentX - middleX + offsetX;
            var currentMapY = currentY - middleY + offsetY;
            if ((currentX == p2.getX()) && (currentY == p2.getY())) {
                isReachable = true;
                break;
            }

            var currentDistance = distances[currentMapX][currentMapY] + 1;
            if (currentMapX > 0 && directions[currentMapX - 1][currentMapY] == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0) {
                // Able to move 1 tile west
                bufferX[index2] = currentX - 1;
                bufferY[index2] = currentY;
                index2 = index2 + 1 & 4095;
                directions[currentMapX - 1][currentMapY] = 2;
                distances[currentMapX - 1][currentMapY] = currentDistance;
            }

            if (currentMapX < 127 && directions[currentMapX + 1][currentMapY] == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0) {
                // Able to move 1 tile east
                bufferX[index2] = currentX + 1;
                bufferY[index2] = currentY;
                index2 = index2 + 1 & 4095;
                directions[currentMapX + 1][currentMapY] = 8;
                distances[currentMapX + 1][currentMapY] = currentDistance;
            }

            if (currentMapY > 0 && directions[currentMapX][currentMapY - 1] == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0) {
                // Able to move 1 tile south
                bufferX[index2] = currentX;
                bufferY[index2] = currentY - 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX][currentMapY - 1] = 1;
                distances[currentMapX][currentMapY - 1] = currentDistance;
            }

            if (currentMapY < 127 && directions[currentMapX][currentMapY + 1] == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0) {
                // Able to move 1 tile north
                bufferX[index2] = currentX;
                bufferY[index2] = currentY + 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX][currentMapY + 1] = 4;
                distances[currentMapX][currentMapY + 1] = currentDistance;
            }

            if (currentMapX > 0 && currentMapY > 0 && directions[currentMapX - 1][currentMapY - 1] == 0 && (collisionDataFlags[currentX - 1][currentY - 1] & 19136782) == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0) {
                // Able to move 1 tile south-west
                bufferX[index2] = currentX - 1;
                bufferY[index2] = currentY - 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX - 1][currentMapY - 1] = 3;
                distances[currentMapX - 1][currentMapY - 1] = currentDistance;
            }

            if (currentMapX < 127 && currentMapY > 0 && directions[currentMapX + 1][currentMapY - 1] == 0 && (collisionDataFlags[currentX + 1][currentY - 1] & 19136899) == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0) {
                // Able to move 1 tile north-west
                bufferX[index2] = currentX + 1;
                bufferY[index2] = currentY - 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX + 1][currentMapY - 1] = 9;
                distances[currentMapX + 1][currentMapY - 1] = currentDistance;
            }

            if (currentMapX > 0 && currentMapY < 127 && directions[currentMapX - 1][currentMapY + 1] == 0 && (collisionDataFlags[currentX - 1][currentY + 1] & 19136824) == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0) {
                // Able to move 1 tile south-east
                bufferX[index2] = currentX - 1;
                bufferY[index2] = currentY + 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX - 1][currentMapY + 1] = 6;
                distances[currentMapX - 1][currentMapY + 1] = currentDistance;
            }

            if (currentMapX < 127 && currentMapY < 127 && directions[currentMapX + 1][currentMapY + 1] == 0 && (collisionDataFlags[currentX + 1][currentY + 1] & 19136992) == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0) {
                // Able to move 1 tile north-east
                bufferX[index2] = currentX + 1;
                bufferY[index2] = currentY + 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX + 1][currentMapY + 1] = 12;
                distances[currentMapX + 1][currentMapY + 1] = currentDistance;
            }
        }
        if (!isReachable) {
            // Try find a different reachable tile in the 21x21 area around the target tile, as close as possible to the target tile
            var upperboundDistance = Integer.MAX_VALUE;
            var pathLength = Integer.MAX_VALUE;
            var checkRange = 10;
            var approxDestinationX = p2.getX();
            var approxDestinationY = p2.getY();
            for (var i = approxDestinationX - checkRange; i <= checkRange + approxDestinationX; ++i) {
                for (var j = approxDestinationY - checkRange; j <= checkRange + approxDestinationY; ++j) {
                    var currentMapX = i - middleX + offsetX;
                    var currentMapY = j - middleY + offsetY;
                    if (currentMapX >= 0 && currentMapY >= 0 && currentMapX < 128 && currentMapY < 128 && distances[currentMapX][currentMapY] < 100) {
                        var deltaX = 0;
                        if (i < approxDestinationX) {
                            deltaX = approxDestinationX - i;
                        } else if (i > approxDestinationX) {
                            deltaX = i - (approxDestinationX);
                        }

                        var deltaY = 0;
                        if (j < approxDestinationY) {
                            deltaY = approxDestinationY - j;
                        } else if (j > approxDestinationY) {
                            deltaY = j - (approxDestinationY);
                        }

                        var distanceSquared = deltaX * deltaX + deltaY * deltaY;
                        if (distanceSquared < upperboundDistance || distanceSquared == upperboundDistance && distances[currentMapX][currentMapY] < pathLength) {
                            upperboundDistance = distanceSquared;
                            pathLength = distances[currentMapX][currentMapY];
                            currentX = i;
                            currentY = j;
                        }
                    }
                }
            }
            if (upperboundDistance == Integer.MAX_VALUE) {
                // No path found
                return null;
            }
        }

        // Getting path from directions and distances
        bufferX[0] = currentX;
        bufferY[0] = currentY;
        var index = 1;
        int directionNew;
        int directionOld;
        for (directionNew = directionOld = directions[currentX - middleX + offsetX][currentY - middleY + offsetY]; p1.getX() != currentX || p1.getY() != currentY; directionNew = directions[currentX - middleX + offsetX][currentY - middleY + offsetY]) {
            if (directionNew != directionOld) {
                // "Corner" of the path --> new checkpoint tile
                directionOld = directionNew;
                bufferX[index] = currentX;
                bufferY[index++] = currentY;
            }

            if ((directionNew & 2) != 0) {
                ++currentX;
            } else if ((directionNew & 8) != 0) {
                --currentX;
            }

            if ((directionNew & 1) != 0) {
                ++currentY;
            } else if ((directionNew & 4) != 0) {
                --currentY;
            }
        }

        var checkpointTileNumber = 1;
        var tiles = client.getTiles();
        List<ITile> checkpointTiles = new ArrayList<>();
        while (index-- > 0) {
            checkpointTiles.add(tiles[this.getPlane()][bufferX[index]][bufferY[index]]);
            if (checkpointTileNumber == 25) {
                // Pathfinding only supports up to the 25 first checkpoint tiles
                break;
            }
            checkpointTileNumber++;
        }
        return checkpointTiles;
    }

    private List<ITileObject> tileObjects() {
        var out = new ArrayList<ITileObject>();
        if (getDecorativeObject() != null) {
            out.add(decorativeObject);
        }

        if (getGroundObject() != null) {
            out.add(groundObject);
        }

        if (getWallObject() != null) {
            out.add(wallObject);
        }

        out.addAll(iGameObjects);

        return out;
    }

    private List<IGameObject> gameObjects() {
        var gameObjects = wrapped.getGameObjects();
        if (gameObjects == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(gameObjects)
                .filter(Objects::nonNull)
                .map(gameObject -> GameObjectImpl.of(gameObject, this, client))
                .collect(Collectors.toList());
    }

    private List<ITileItem> groundItems() {
        var groundItems = wrapped.getGroundItems();
        if (groundItems == null) {
            return Collections.emptyList();
        }

        return groundItems.stream()
                .map(groundItem -> TileItemImpl.of(groundItem, this, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean hasNoGameObjects() {
        var gameObjects = getGameObjects();
        if (gameObjects == null) {
            return true;
        }

        for (var gameObject : gameObjects) {
            if (gameObject != null) {
                return false;
            }
        }

        return true;
    }
}
