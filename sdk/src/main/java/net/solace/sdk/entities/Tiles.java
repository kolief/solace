package net.solace.sdk.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.entities.ITiles;
import net.solace.sdk.game.Client;

public class Tiles {
    private static final ITiles TILES = Static.getTiles();

    public static ITile getAt(int plane, Point sceneLocation) {
        return TILES.getAt(plane, sceneLocation);
    }

    public static ITile getAt(WorldPoint worldPoint) {
        return TILES.getAt(worldPoint);
    }

    public static List<ITile> getAll(Predicate<? super ITile> filter) {
        return TILES.getAll(filter);
    }

    public static List<ITile> getAll() {
        return TILES.getAll(x -> true);
    }

    public static ITile[][] getRaw() {
        return TILES.getRaw();
    }

    public static ITile[][][] getRawFloors() {
        return TILES.getRawFloors();
    }

    public static ITile getHoveredTile() {
        return Client.getSelectedSceneTile();
    }

    public static List<ITile> getSurrounding(WorldPoint worldPoint, int radius) {
        ArrayList<ITile> out = new ArrayList<ITile>();
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                out.add(Tiles.getAt(worldPoint.dx(x).dy(y)));
            }
        }
        return out;
    }
}

