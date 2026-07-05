package net.solace.impl.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.containers.TileContainer;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.entities.ITiles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Getter
public class TilesImpl implements ITiles {
    private final IClient client;
    private final TileContainer tileContainer;

    @Override
    public ITile getAt(int plane, Point sceneLocation) {
        var x = sceneLocation.getX();
        var y = sceneLocation.getY();
        if (x < 0 || x >= getSizeX() || y < 0 || y >= getSizeY()) {
            return null;
        }

        return tileContainer.getAt(x, y, plane);
    }

    @Override
    public ITile getAt(WorldPoint worldPoint) {
        var wv = client.getWrapped().getWorldView(getWorldViewId());
        if (wv == null) {
            return null;
        }

        var local = LocalPoint.fromWorld(wv, worldPoint);
        if (local == null) {
            return null;
        }

        return getAt(worldPoint.getPlane(), new Point(local.getSceneX(), local.getSceneY()));
    }

    @Override
    public int getSizeX() {
        return tileContainer.getSizeX();
    }

    @Override
    public int getSizeY() {
        return tileContainer.getSizeY();
    }

    @Override
    public int getWorldViewId() {
        return tileContainer.getWorldViewId();
    }

    @Override
    public List<ITile> getAll(Predicate<? super ITile> filter) {
        var out = new ArrayList<ITile>();

        var tiles = getRaw();
        for (var row : tiles) {
            for (var tile : row) {
                if (tile != null && filter.test(tile)) {
                    out.add(tile);
                }
            }
        }

        return out;
    }

    @Override
    public ITile[][] getRaw() {
        return tileContainer.getAll();
    }

    @Override
    public ITile[][][] getRawFloors() {
        return tileContainer.getAllFloors();
    }
}
