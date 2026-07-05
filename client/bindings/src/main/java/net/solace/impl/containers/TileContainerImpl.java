package net.solace.impl.containers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Constants;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.containers.TileContainer;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.impl.domain.tiles.DecorativeObjectImpl;
import net.solace.impl.domain.tiles.GroundObjectImpl;
import net.solace.impl.domain.tiles.TileImpl;
import net.solace.impl.domain.tiles.WallObjectImpl;
import net.solace.impl.reflection.ReflectionManager;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class TileContainerImpl implements TileContainer {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ITile[][][] cache;

    private final IClient client;
    @Getter
    private final int worldViewId;
    @Getter
    private final int sizeX;
    @Getter
    private final int sizeY;

    public TileContainerImpl(IClient client, int worldViewId, int sizeX, int sizeY) {
        this.client = client;
        this.worldViewId = worldViewId;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.cache = new ITile[Constants.MAX_Z][sizeX][sizeY];
    }

    @Subscribe(priority = Integer.MAX_VALUE)
    private void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.LOGGED_IN) {
            var rlTiles = getWorldView().getScene().getTiles();

            for (var z = 0; z < Constants.MAX_Z; z++) {
                for (var x = 0; x < sizeX; x++) {
                    for (var y = 0; y < sizeY; y++) {
                        create(rlTiles[z][x][y]);
                    }
                }
            }
        } else {
            resetCache();
        }
    }

    @Subscribe(priority = Integer.MAX_VALUE)
    private void onGameTick(GameTick e) {
        var startMs = System.currentTimeMillis();
        var rlTiles = getWorldView().getScene().getTiles();

        for (var z = 0; z < Constants.MAX_Z; z++) {
            for (var x = 0; x < sizeX; x++) {
                for (var y = 0; y < sizeY; y++) {
                    var rlTile = rlTiles[z][x][y];
                    if (rlTile == null) {
                        cache[z][x][y] = null;
                    } else {
                        updateTile(rlTile, cache[z][x][y]);
                    }
                }
            }
        }
        log.trace("[TileContainer] onGameTick took {} ms", System.currentTimeMillis() - startMs);
    }

    @Override
    public ITile getAt(int x, int y) {
        return getAt(x, y, getWorldView().getPlane());
    }

    @Override
    public ITile getAt(int x, int y, int z) {
        lock.readLock().lock();
        try {
            return cache[z][x][y];
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ITile[][] getAll() {
        lock.readLock().lock();
        try {
            return cache[getWorldView().getPlane()];
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ITile[][][] getAllFloors() {
        lock.readLock().lock();
        try {
            return cache;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned e) {
        var tile = e.getTile();
        var tileItem = e.getItem();
        if (checkWorldView(tileItem)) {
            var newTile = create(tile);
            var newTileItem = newTile.getIGroundItems().stream()
                    .filter(i -> i.getWrapped() == tileItem)
                    .findFirst()
                    .orElse(null);

            if (newTileItem == null) {
                log.debug("Item spawned but item was null: {}, {}", tileItem, newTile.getIGroundItems());
                return;
            }

            post(new net.solace.api.events.ItemSpawned(newTile, newTileItem));
        }
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned e) {
        var tile = e.getTile();
        var tileItem = e.getItem();
        if (checkWorldView(tileItem)) {
            var scene = tile.getSceneLocation();
            var cachedTile = getAt(scene.getX(), scene.getY(), tile.getPlane());
            if (cachedTile == null) {
                log.debug("Item despawned but tile was null: {}", tileItem);
                return;
            }

            var cachedTileItem = cachedTile.getIGroundItems().stream()
                    .filter(i -> i.getWrapped() == tileItem)
                    .findFirst()
                    .orElse(null);

            if (cachedTileItem == null) {
                log.debug("Item despawned but item was null: {}, {}", tileItem, cachedTile.getIGroundItems());
                return;
            }

            post(new net.solace.api.events.ItemDespawned(cachedTile, cachedTileItem));

            create(tile);
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned e) {
        var object = e.getGameObject();
        if (checkWorldView(object)) {
            var tile = e.getTile();
            var newGameObject = createGameObjects(tile, object);
            if (newGameObject == null) {
                log.debug("GameObject spawned but object was null: {}", object);
                return;
            }

            var newTile = newGameObject.getTile();

            var event = new net.solace.api.events.GameObjectSpawned();
            event.setGameObject(newGameObject);
            event.setTile(newTile);
            post(event);
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        var object = event.getGameObject();
        if (checkWorldView(object)) {
            var tile = event.getTile();
            var scene = tile.getSceneLocation();
            var cachedTile = getAt(scene.getX(), scene.getY(), tile.getPlane());
            if (cachedTile == null) {
                log.debug("GameObject despawned but tile was null: {}", object);
                return;
            }

            var despawnedObject = cachedTile.getIGameObjects().stream()
                    .filter(i -> i.getWrapped() == object)
                    .findFirst()
                    .orElse(null);

            if (despawnedObject == null) {
                log.debug("GameObject despawned but object was null: {}, {}", object, cachedTile.getIGameObjects());
                return;
            }

            post(new net.solace.api.events.GameObjectDespawned(cachedTile, despawnedObject));

            clearGameObjects(cachedTile, despawnedObject);
        }
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        if (checkWorldView(event.getDecorativeObject())) {
            var tile = event.getTile();
            var newTile = create(tile);
            var decor = newTile.getDecorativeObject();
            post(new net.solace.api.events.DecorativeObjectSpawned(newTile, decor));
        }
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        if (checkWorldView(event.getDecorativeObject())) {
            var tile = event.getTile();
            var scene = tile.getSceneLocation();
            var cachedTile = getAt(scene.getX(), scene.getY(), tile.getPlane());
            if (cachedTile == null) {
                log.debug("DecorativeObject despawned but tile was null: {}", tile);
                return;
            }

            var cachedDecor = cachedTile.getDecorativeObject();
            if (cachedDecor == null) {
                log.debug("DecorativeObject despawned but object was null: {}, {}", tile, cachedTile.getDecorativeObject());
                return;
            }

            post(new net.solace.api.events.DecorativeObjectDespawned(cachedTile, cachedDecor));

            create(tile);
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        if (checkWorldView(event.getGroundObject())) {
            var tile = event.getTile();
            var newTile = create(tile);
            var groundObject = newTile.getGroundObject();
            if (groundObject == null) {
                log.debug("GroundObject spawned but object was null: {}, {}", tile, newTile.getGroundObject());
                return;
            }

            post(new net.solace.api.events.GroundObjectSpawned(newTile, groundObject));
        }
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        if (checkWorldView(event.getGroundObject())) {
            var tile = event.getTile();
            var scene = tile.getSceneLocation();
            var cachedTile = getAt(scene.getX(), scene.getY(), tile.getPlane());
            if (cachedTile == null) {
                log.debug("GroundObject despawned but tile was null: {}", tile);
                return;
            }

            var cachedGroundObject = cachedTile.getGroundObject();
            if (cachedGroundObject == null) {
                log.debug("GroundObject despawned but object was null: {}, {}", tile, cachedTile.getGroundObject());
                return;
            }

            post(new net.solace.api.events.GroundObjectDespawned(cachedTile, cachedGroundObject));

            create(tile);
        }
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event) {
        if (checkWorldView(event.getWallObject())) {
            var tile = event.getTile();
            var newTile = create(tile);
            var wallObject = newTile.getWallObject();
            if (wallObject == null) {
                log.debug("WallObject spawned but object was null: {}, {}", tile, newTile.getWallObject());
                return;
            }

            post(new net.solace.api.events.WallObjectSpawned(newTile, wallObject));
        }
    }

    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event) {
        if (checkWorldView(event.getWallObject())) {
            var tile = event.getTile();
            var scene = tile.getSceneLocation();
            var cachedTile = getAt(scene.getX(), scene.getY(), tile.getPlane());
            if (cachedTile == null) {
                log.debug("WallObject despawned but tile was null: {}", tile);
                return;
            }

            var cachedWallObject = cachedTile.getWallObject();
            if (cachedWallObject == null) {
                log.debug("WallObject despawned but object was null: {}, {}", tile, cachedTile.getWallObject());
                return;
            }

            post(new net.solace.api.events.WallObjectDespawned(cachedTile, cachedWallObject));

            create(tile);
        }
    }

    private ITile create(Tile tile) {
        if (tile == null) {
            return null;
        }

        var newTile = TileImpl.of(tile, client);
        cache[tile.getPlane()][tile.getSceneLocation().getX()][tile.getSceneLocation().getY()] = newTile;

        return newTile;
    }

    private void post(Object event) {
        client.getWrapped().getCallbacks().post(event);
    }

    private void resetCache() {
        for (var z = 0; z < Constants.MAX_Z; z++) {
            for (var x = 0; x < sizeX; x++) {
                for (var y = 0; y < sizeY; y++) {
                    var tile = cache[z][x][y];
                    if (tile == null) {
                        continue;
                    }

                    cache[z][x][y] = null;
                }
            }
        }
    }

    private void updateTile(Tile rlTile, ITile tile) {
        if (tile == null) {
            return;
        }

        var tileImpl = (TileImpl) tile;
        if (!Objects.equals(rlTile, tileImpl.getWrapped())) {
            tileImpl.setWrapped(rlTile);
        }

        tileImpl.getIGameObjects().forEach(ITileObject::updateComposition);

        var groundObject = (GroundObjectImpl) tileImpl.getGroundObject();
        if (groundObject != null) {
            groundObject.setWrapped(rlTile.getGroundObject());
            if (groundObject.getWrapped() == null) {
                tileImpl.removeGroundObject();
            } else {
                groundObject.updateComposition();
            }
        }

        var wallObject = (WallObjectImpl) tileImpl.getWallObject();
        if (wallObject != null) {
            wallObject.setWrapped(rlTile.getWallObject());
            if (wallObject.getWrapped() == null) {
                tileImpl.removeWallObject();
            } else {
                wallObject.updateComposition();
            }
        }

        var decorativeObject = (DecorativeObjectImpl) tileImpl.getDecorativeObject();
        if (decorativeObject != null) {
            decorativeObject.setWrapped(rlTile.getDecorativeObject());
            if (decorativeObject.getWrapped() == null) {
                tileImpl.removeDecorativeObject();
            } else {
                decorativeObject.updateComposition();
            }
        }
    }
    
    private void clearGameObjects(ITile originalTile, IGameObject despawnedObject) {
        if (originalTile == null) {
            return;
        }
        
        if (despawnedObject == null) {
            return;
        }
        
        var location = originalTile.getWorldLocation();
        var wv = despawnedObject.getWorldView();
        for (var wp : despawnedObject.getWorldArea().toWorldPointList()) {
            if (location.equals(wp)) {
                continue;
            }

            var local = LocalPoint.fromWorld(wv, wp);
            if (local == null) {
                continue;
            }

            var scene = new Point(local.getSceneX(), local.getSceneY());
            var tile = wv.getScene().getTiles()[wp.getPlane()][scene.getX()][scene.getY()];
            if (tile == null) {
                continue;
            }

            create(tile);
        }

        var local = LocalPoint.fromWorld(wv, location);
        if (local == null) {
            return;
        }

        var scene = new Point(local.getSceneX(), local.getSceneY());
        var tile = wv.getScene().getTiles()[location.getPlane()][scene.getX()][scene.getY()];
        if (tile == null) {
            return;
        }

        create(tile);
    }

    private IGameObject createGameObjects(Tile originalTile, GameObject originalObject) {
        var location = originalTile.getWorldLocation();
        var realTile = create(originalTile);
        var gameObject = realTile.getIGameObjects().stream()
                .filter(i -> i.getWrapped() == originalObject)
                .findFirst()
                .orElse(null);
        if (gameObject == null) {
            return null;
        }

        var wv = gameObject.getWorldView();
        for (var wp : gameObject.getWorldArea().toWorldPointList()) {
            if (location.equals(wp)) {
                continue;
            }

            var local = LocalPoint.fromWorld(wv, wp);
            if (local == null) {
                continue;
            }

            var scene = new Point(local.getSceneX(), local.getSceneY());
            var tile = wv.getScene().getTiles()[wp.getPlane()][scene.getX()][scene.getY()];
            if (tile == null) {
                continue;
            }

            create(tile);
        }

        return gameObject;
    }

    private boolean checkWorldView(TileObject object) {
        return object.getWorldView().getId() == worldViewId;
    }

    private boolean checkWorldView(TileItem tileItem) {
        int worldViewId = ReflectionManager.getField(tileItem, "TileItem", "worldViewId");
        return worldViewId == this.worldViewId;
    }

    private WorldView getWorldView() {
        return client.getWrapped().getWorldView(worldViewId);
    }
}
