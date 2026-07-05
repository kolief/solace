package net.solace.impl.domain.tiles;

import net.runelite.api.DecorativeObject;
import net.runelite.api.Renderable;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.IDecorativeObject;
import net.solace.api.domain.tiles.ITile;

import java.awt.Shape;

public class DecorativeObjectImpl extends TileObjectImpl<DecorativeObject> implements IDecorativeObject {
    private DecorativeObjectImpl(DecorativeObject wrapped, ITile tile, IClient client) {
        super(wrapped, tile, client);
    }

    public static IDecorativeObject of(DecorativeObject decorativeObject, ITile tile, IClient client) {
        if (decorativeObject == null) {
            return null;
        }

        return new DecorativeObjectImpl(decorativeObject, tile, client);
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
    public Renderable getRenderable() {
        return wrapped.getRenderable();
    }

    @Override
    public Renderable getRenderable2() {
        return wrapped.getRenderable2();
    }

    @Override
    public int getXOffset() {
        return wrapped.getXOffset();
    }

    @Override
    public int getXOffset2() {
        return wrapped.getXOffset2();
    }

    @Override
    public int getYOffset() {
        return wrapped.getYOffset();
    }

    @Override
    public int getYOffset2() {
        return wrapped.getYOffset2();
    }

    @Override
    public int getConfig() {
        return wrapped.getConfig();
    }
}
