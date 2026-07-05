package net.solace.impl.entities;

import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.entities.ITileObjects;
import net.solace.api.entities.ITiles;

import java.util.ArrayList;
import java.util.List;

public class TileObjectsImpl extends TileEntitiesImpl<ITileObject> implements ITileObjects {
    public TileObjectsImpl(ITiles tiles, IClient client) {
        super(tiles, client, TileObjectsImpl::getTileObjects);
    }

    private static List<ITileObject> getTileObjects(ITile tile) {
        var out = new ArrayList<ITileObject>();
        if (tile == null) {
            return out;
        }

        var decorativeObject = tile.getDecorativeObject();
        if (decorativeObject != null && decorativeObject.getId() != -1) {
            out.add(decorativeObject);
        }

        var groundObject = tile.getGroundObject();
        if (groundObject != null && groundObject.getId() != -1) {
            out.add(groundObject);
        }

        var wallObject = tile.getWallObject();
        if (wallObject != null && wallObject.getId() != -1) {
            out.add(wallObject);
        }

        var gameObjects = tile.getIGameObjects();
        if (gameObjects != null) {
            for (var gameObject : gameObjects) {
                if (gameObject != null && gameObject.getId() != -1) {
                    out.add(gameObject);
                }
            }
        }

        return out;
    }
}
