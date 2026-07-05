package net.solace.impl.domain.tiles;

import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Renderable;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.ITile;

import java.awt.Shape;

public class GameObjectImpl extends TileObjectImpl<GameObject> implements IGameObject {
    private GameObjectImpl(GameObject wrapped, ITile tile, IClient client) {
        super(wrapped, tile, client);
    }

    public static IGameObject of(GameObject gameObject, ITile tile, IClient client) {
        if (gameObject == null) {
            return null;
        }

        return new GameObjectImpl(gameObject, tile, client);
    }

    @Override
    public WorldArea getWorldArea() {
        if (!getLocalLocation().isInScene()) {
            return null;
        }

        var localSWTile = new LocalPoint(
                getLocalLocation().getX() - sizeX() * Perspective.LOCAL_TILE_SIZE / 2,
                getLocalLocation().getY() - sizeY() * Perspective.LOCAL_TILE_SIZE / 2
        );

        var localNETile = new LocalPoint(
                getLocalLocation().getX() + sizeX() * Perspective.LOCAL_TILE_SIZE / 2,
                getLocalLocation().getY() + sizeY() * Perspective.LOCAL_TILE_SIZE / 2
        );

        var swLocation = WorldPoint.fromLocal(
                client.getWrapped(),
                localSWTile
        );

        var neLocation = WorldPoint.fromLocal(
                client.getWrapped(),
                localNETile
        );

        return new WorldArea(
                swLocation.getX(),
                swLocation.getY(),
                neLocation.getX() - swLocation.getX(),
                neLocation.getY() - swLocation.getY(),
                swLocation.getPlane()
        );
    }

    @Override
    public int sizeX() {
        return wrapped.sizeX();
    }

    @Override
    public int sizeY() {
        return wrapped.sizeY();
    }

    @Override
    public Point getSceneMinLocation() {
        return wrapped.getSceneMinLocation();
    }

    @Override
    public Point getSceneMaxLocation() {
        return wrapped.getSceneMaxLocation();
    }

    @Override
    public Shape getConvexHull() {
        return wrapped.getConvexHull();
    }

    @Override
    public int getOrientation() {
        return wrapped.getOrientation();
    }

    @Override
    public Renderable getRenderable() {
        return wrapped.getRenderable();
    }

    @Override
    public int getModelOrientation() {
        return wrapped.getModelOrientation();
    }

    @Override
    public int getConfig() {
        return wrapped.getConfig();
    }
}
