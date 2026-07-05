package net.solace.impl.domain.tiles;

import net.runelite.api.Renderable;
import net.runelite.api.WallObject;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.IWallObject;

import java.awt.Shape;

public class WallObjectImpl extends TileObjectImpl<WallObject> implements IWallObject {
    private WallObjectImpl(WallObject wrapped, ITile tile, IClient client) {
        super(wrapped, tile, client);
    }

    public static IWallObject of(WallObject wallObject, ITile tile, IClient client) {
        if (wallObject == null) {
            return null;
        }

        return new WallObjectImpl(wallObject, tile, client);
    }

    @Override
    public int getOrientationA() {
        return wrapped.getOrientationA();
    }

    @Override
    public int getOrientationB() {
        return wrapped.getOrientationB();
    }

    @Override
    public int getConfig() {
        return wrapped.getConfig();
    }

    @Override
    public Shape getConvexHull() {
        return wrapped.getConvexHull();
    }

    @Override
    public Shape getConvexHull2() {
        return wrapped.getConvexHull2();
    }

    @Override
    public Renderable getRenderable1() {
        return wrapped.getRenderable1();
    }

    @Override
    public Renderable getRenderable2() {
        return wrapped.getRenderable2();
    }
}
