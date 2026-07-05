package net.solace.sdk.entities;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.entities.ITileObjects;
import net.solace.api.query.entities.TileObjectQuery;
import net.solace.sdk.entities.Players;

public class TileObjects {
    private static final ITileObjects TILE_OBJECTS = Static.getTileObjects();

    public static TileObjectQuery query() {
        return new TileObjectQuery(() -> TileObjects.getAll(x -> true));
    }

    public static TileObjectQuery query(Supplier<List<ITileObject>> supplier) {
        return new TileObjectQuery(supplier);
    }

    public static TileObjectQuery query(ITile tile, int radius) {
        return new TileObjectQuery(() -> TileObjects.getSurrounding(tile, radius, x -> true));
    }

    public static TileObjectQuery query(WorldPoint tile, int radius) {
        return new TileObjectQuery(() -> TileObjects.getSurrounding(tile, radius, x -> true));
    }

    public static List<ITileObject> getAll(Predicate<? super ITileObject> filter) {
        return TILE_OBJECTS.getAll(filter);
    }

    public static List<ITileObject> getAll(Predicate<? super ITileObject> filter, boolean allPlanes) {
        return TILE_OBJECTS.getAll(filter, allPlanes);
    }

    public static List<ITileObject> getAll(int ... ids) {
        return TILE_OBJECTS.getAll(ids);
    }

    public static List<ITileObject> getAll(String ... names) {
        return TILE_OBJECTS.getAll(names);
    }

    public static ITileObject getNearest(WorldPoint worldPoint, Predicate<? super ITileObject> filter) {
        return (ITileObject)TILE_OBJECTS.getNearest(worldPoint, filter);
    }

    public static ITileObject getNearest(WorldPoint worldPoint, int ... ids) {
        return (ITileObject)TILE_OBJECTS.getNearest(worldPoint, ids);
    }

    public static ITileObject getNearest(WorldPoint worldPoint, String ... names) {
        return (ITileObject)TILE_OBJECTS.getNearest(worldPoint, names);
    }

    public static ITileObject getNearest(Predicate<? super ITileObject> filter) {
        return TileObjects.getNearest(Players.getLocal().getWorldLocation(), filter);
    }

    public static ITileObject getNearest(int ... ids) {
        return TileObjects.getNearest(Players.getLocal().getWorldLocation(), ids);
    }

    public static ITileObject getNearest(String ... names) {
        return TileObjects.getNearest(Players.getLocal().getWorldLocation(), names);
    }

    public static List<ITileObject> getAt(ITile tile, Predicate<? super ITileObject> filter) {
        return TILE_OBJECTS.getAt(tile, filter);
    }

    public static List<ITileObject> getAt(ITile tile, int ... ids) {
        return TILE_OBJECTS.getAt(tile, ids);
    }

    public static List<ITileObject> getAt(ITile tile, String ... names) {
        return TILE_OBJECTS.getAt(tile, names);
    }

    public static List<ITileObject> getAt(WorldPoint worldPoint, Predicate<? super ITileObject> filter) {
        return TILE_OBJECTS.getAt(worldPoint, filter);
    }

    public static List<ITileObject> getAt(WorldPoint worldPoint, int ... ids) {
        return TILE_OBJECTS.getAt(worldPoint, ids);
    }

    public static List<ITileObject> getAt(WorldPoint worldPoint, String ... names) {
        return TILE_OBJECTS.getAt(worldPoint, names);
    }

    public static ITileObject getFirstAt(ITile tile, Predicate<? super ITileObject> filter) {
        return (ITileObject)TILE_OBJECTS.getFirstAt(tile, filter);
    }

    public static ITileObject getFirstAt(ITile tile, int ... ids) {
        return (ITileObject)TILE_OBJECTS.getFirstAt(tile, ids);
    }

    public static ITileObject getFirstAt(ITile tile, String ... names) {
        return (ITileObject)TILE_OBJECTS.getFirstAt(tile, names);
    }

    public static ITileObject getFirstAt(WorldPoint worldPoint, Predicate<? super ITileObject> filter) {
        return (ITileObject)TILE_OBJECTS.getFirstAt(worldPoint, filter);
    }

    public static ITileObject getFirstAt(WorldPoint worldPoint, int ... ids) {
        return (ITileObject)TILE_OBJECTS.getFirstAt(worldPoint, ids);
    }

    public static ITileObject getFirstAt(WorldPoint worldPoint, String ... names) {
        return (ITileObject)TILE_OBJECTS.getFirstAt(worldPoint, names);
    }

    public static List<ITileObject> getSurrounding(ITile tile, int radius, Predicate<? super ITileObject> filter) {
        return TILE_OBJECTS.getSurrounding(tile, radius, filter);
    }

    public static List<ITileObject> getSurrounding(ITile tile, int radius, int ... ids) {
        return TILE_OBJECTS.getSurrounding(tile, radius, ids);
    }

    public static List<ITileObject> getSurrounding(ITile tile, int radius, String ... names) {
        return TILE_OBJECTS.getSurrounding(tile, radius, names);
    }

    public static List<ITileObject> getSurrounding(WorldPoint worldPoint, int radius, Predicate<? super ITileObject> filter) {
        return TILE_OBJECTS.getSurrounding(worldPoint, radius, filter);
    }

    public static List<ITileObject> getSurrounding(WorldPoint worldPoint, int radius, int ... ids) {
        return TILE_OBJECTS.getSurrounding(worldPoint, radius, ids);
    }

    public static List<ITileObject> getSurrounding(WorldPoint worldPoint, int radius, String ... names) {
        return TILE_OBJECTS.getSurrounding(worldPoint, radius, names);
    }

    public static ITileObject getFirstSurrounding(ITile tile, int radius, Predicate<? super ITileObject> filter) {
        return (ITileObject)TILE_OBJECTS.getFirstSurrounding(tile, radius, filter);
    }

    public static ITileObject getFirstSurrounding(ITile tile, int radius, int ... ids) {
        return (ITileObject)TILE_OBJECTS.getFirstSurrounding(tile, radius, ids);
    }

    public static ITileObject getFirstSurrounding(ITile tile, int radius, String ... names) {
        return (ITileObject)TILE_OBJECTS.getFirstSurrounding(tile, radius, names);
    }

    public static ITileObject getFirstSurrounding(WorldPoint worldPoint, int radius, Predicate<? super ITileObject> filter) {
        return (ITileObject)TILE_OBJECTS.getFirstSurrounding(worldPoint, radius, filter);
    }

    public static ITileObject getFirstSurrounding(WorldPoint worldPoint, int radius, int ... ids) {
        return (ITileObject)TILE_OBJECTS.getFirstSurrounding(worldPoint, radius, ids);
    }

    public static ITileObject getFirstSurrounding(WorldPoint worldPoint, int radius, String ... names) {
        return (ITileObject)TILE_OBJECTS.getFirstSurrounding(worldPoint, radius, names);
    }

    public static List<ITileObject> getIn(WorldArea area, Predicate<? super ITileObject> filter) {
        return TILE_OBJECTS.getIn(area, filter);
    }

    public static List<ITileObject> getIn(WorldArea area, int ... ids) {
        return TILE_OBJECTS.getIn(area, ids);
    }

    public static List<ITileObject> getIn(WorldArea area, String ... names) {
        return TILE_OBJECTS.getIn(area, names);
    }

    public static ITileObject getNearestIn(WorldArea area, Predicate<? super ITileObject> filter) {
        return TileObjects.getIn(area, filter).stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()))).orElse(null);
    }

    public static ITileObject getNearestIn(WorldArea area, int ... ids) {
        return TileObjects.getNearestIn(area, Predicates.ids((int[])ids));
    }

    public static ITileObject getNearestIn(WorldArea area, String ... names) {
        return TileObjects.getNearestIn(area, Predicates.names((String[])names));
    }
}

