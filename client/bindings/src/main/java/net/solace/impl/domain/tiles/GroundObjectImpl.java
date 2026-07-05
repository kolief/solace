package net.solace.impl.domain.tiles;

import net.runelite.api.GroundObject;
import net.runelite.api.Renderable;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.IGroundObject;
import net.solace.api.domain.tiles.ITile;

import java.awt.Shape;

public class GroundObjectImpl extends TileObjectImpl<GroundObject> implements IGroundObject {
    private GroundObjectImpl(GroundObject wrapped, ITile tile, IClient client) {
        super(wrapped, tile, client);
    }

    public static IGroundObject of(GroundObject groundObject, ITile tile, IClient client) {
        if (groundObject == null) {
            return null;
        }

        return new GroundObjectImpl(groundObject, tile, client);
    }

    @Override
    public Renderable getRenderable() {
        return wrapped.getRenderable();
    }

    @Override
    public Shape getConvexHull() {
        return wrapped.getConvexHull();
    }

    @Override
    public int getConfig() {
        return wrapped.getConfig();
    }
}
