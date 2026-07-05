package net.solace.sdk.entities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.entities.ITileItems;
import net.solace.api.query.entities.TileItemQuery;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.Tiles;

public class TileItems {
    private static final ITileItems TILE_ITEMS = Static.getTileItems();

    public static TileItemQuery query() {
        return new TileItemQuery(() -> TileItems.getAll(x -> true));
    }

    public static TileItemQuery query(Supplier<List<ITileItem>> supplier) {
        return new TileItemQuery(supplier);
    }

    public static TileItemQuery query(ITile tile, int radius) {
        return new TileItemQuery(() -> TileItems.getSurrounding(tile, radius, x -> true));
    }

    public static TileItemQuery query(WorldPoint tile, int radius) {
        return new TileItemQuery(() -> TileItems.getSurrounding(tile, radius, x -> true));
    }

    public static List<ITileItem> getAll(Predicate<? super ITileItem> filter) {
        return TILE_ITEMS.getAll(filter);
    }

    public static List<ITileItem> getAll(int ... ids) {
        return TILE_ITEMS.getAll(ids);
    }

    public static List<ITileItem> getAll(String ... names) {
        return TILE_ITEMS.getAll(names);
    }

    public static List<ITileItem> getAllMine() {
        return TILE_ITEMS.getAll(x -> x.getOwnership() == 1);
    }

    public static List<ITileItem> getAllMine(Predicate<? super ITileItem> filter) {
        return TILE_ITEMS.getAll(x -> x.getOwnership() == 1 && filter.test((ITileItem)x));
    }

    public static ITileItem getNearest(WorldPoint worldPoint, Predicate<? super ITileItem> filter) {
        return (ITileItem)TILE_ITEMS.getNearest(worldPoint, filter);
    }

    public static ITileItem getNearest(WorldPoint worldPoint, int ... ids) {
        return (ITileItem)TILE_ITEMS.getNearest(worldPoint, ids);
    }

    public static ITileItem getNearest(WorldPoint worldPoint, String ... names) {
        return (ITileItem)TILE_ITEMS.getNearest(worldPoint, names);
    }

    public static ITileItem getNearest(Predicate<? super ITileItem> filter) {
        return TileItems.getNearest(Players.getLocal().getWorldLocation(), filter);
    }

    public static ITileItem getNearest(int ... ids) {
        return TileItems.getNearest(Players.getLocal().getWorldLocation(), ids);
    }

    public static ITileItem getNearest(String ... names) {
        return TileItems.getNearest(Players.getLocal().getWorldLocation(), names);
    }

    public static List<ITileItem> getAt(ITile tile, Predicate<? super ITileItem> filter) {
        return TILE_ITEMS.getAt(tile, filter);
    }

    public static List<ITileItem> getAt(ITile tile, int ... ids) {
        return TILE_ITEMS.getAt(tile, ids);
    }

    public static List<ITileItem> getAt(ITile tile, String ... names) {
        return TILE_ITEMS.getAt(tile, names);
    }

    public static List<ITileItem> getAt(WorldPoint worldPoint, Predicate<? super ITileItem> filter) {
        ITile tile = Tiles.getAt(worldPoint);
        if (tile == null) {
            return Collections.emptyList();
        }
        return TileItems.getAt(tile, filter);
    }

    public static List<ITileItem> getAt(WorldPoint worldPoint, int ... ids) {
        return TileItems.getAt(worldPoint, (Predicate<? super ITileItem>)Predicates.ids((int[])ids));
    }

    public static List<ITileItem> getAt(WorldPoint worldPoint, String ... names) {
        return TileItems.getAt(worldPoint, (Predicate<? super ITileItem>)Predicates.names((String[])names));
    }

    public static ITileItem getFirstAt(ITile tile, Predicate<? super ITileItem> filter) {
        return TileItems.getAt(tile, filter).stream().findFirst().orElse(null);
    }

    public static ITileItem getFirstAt(ITile tile, int ... ids) {
        return TileItems.getFirstAt(tile, (Predicate<? super ITileItem>)Predicates.ids((int[])ids));
    }

    public static ITileItem getFirstAt(ITile tile, String ... names) {
        return TileItems.getFirstAt(tile, (Predicate<? super ITileItem>)Predicates.names((String[])names));
    }

    public static ITileItem getFirstAt(WorldPoint worldPoint, Predicate<? super ITileItem> filter) {
        return TileItems.getFirstAt(Tiles.getAt(worldPoint), filter);
    }

    public static ITileItem getFirstAt(WorldPoint worldPoint, int ... ids) {
        return TileItems.getFirstAt(worldPoint, (Predicate<? super ITileItem>)Predicates.ids((int[])ids));
    }

    public static ITileItem getFirstAt(WorldPoint worldPoint, String ... names) {
        return TileItems.getFirstAt(worldPoint, (Predicate<? super ITileItem>)Predicates.names((String[])names));
    }

    public static List<ITileItem> getSurrounding(ITile tile, int radius, Predicate<? super ITileItem> filter) {
        return TILE_ITEMS.getSurrounding(tile, radius, filter);
    }

    public static List<ITileItem> getSurrounding(ITile tile, int radius, int ... ids) {
        return TILE_ITEMS.getSurrounding(tile, radius, ids);
    }

    public static List<ITileItem> getSurrounding(ITile tile, int radius, String ... names) {
        return TILE_ITEMS.getSurrounding(tile, radius, names);
    }

    public static List<ITileItem> getSurrounding(WorldPoint worldPoint, int radius, Predicate<? super ITileItem> filter) {
        ITile tile = Tiles.getAt(worldPoint);
        if (tile == null) {
            return Collections.emptyList();
        }
        return TileItems.getSurrounding(tile, radius, filter);
    }

    public static List<ITileItem> getSurrounding(WorldPoint worldPoint, int radius, int ... ids) {
        return TileItems.getSurrounding(worldPoint, radius, (Predicate<? super ITileItem>)Predicates.ids((int[])ids));
    }

    public static List<ITileItem> getSurrounding(WorldPoint worldPoint, int radius, String ... names) {
        return TileItems.getSurrounding(worldPoint, radius, (Predicate<? super ITileItem>)Predicates.names((String[])names));
    }

    public static ITileItem getFirstSurrounding(ITile tile, int radius, Predicate<? super ITileItem> filter) {
        return TileItems.getSurrounding(tile, radius, filter).stream().findFirst().orElse(null);
    }

    public static ITileItem getFirstSurrounding(ITile tile, int radius, int ... ids) {
        return TileItems.getFirstSurrounding(tile, radius, (Predicate<? super ITileItem>)Predicates.ids((int[])ids));
    }

    public static ITileItem getFirstSurrounding(ITile tile, int radius, String ... names) {
        return TileItems.getFirstSurrounding(tile, radius, (Predicate<? super ITileItem>)Predicates.names((String[])names));
    }

    public static ITileItem getFirstSurrounding(WorldPoint worldPoint, int radius, Predicate<? super ITileItem> filter) {
        return TileItems.getFirstSurrounding(Tiles.getAt(worldPoint), radius, filter);
    }

    public static ITileItem getFirstSurrounding(WorldPoint worldPoint, int radius, int ... ids) {
        return TileItems.getFirstSurrounding(worldPoint, radius, (Predicate<? super ITileItem>)Predicates.ids((int[])ids));
    }

    public static ITileItem getFirstSurrounding(WorldPoint worldPoint, int radius, String ... names) {
        return TileItems.getFirstSurrounding(worldPoint, radius, (Predicate<? super ITileItem>)Predicates.names((String[])names));
    }

    public static List<ITileItem> getIn(WorldArea area, Predicate<? super ITileItem> filter) {
        return TILE_ITEMS.getIn(area, filter);
    }

    public static List<ITileItem> getIn(WorldArea area, int ... ids) {
        return TILE_ITEMS.getIn(area, ids);
    }

    public static List<ITileItem> getIn(WorldArea area, String ... names) {
        return TILE_ITEMS.getIn(area, names);
    }

    public static ITileItem getFirstIn(WorldArea area, Predicate<? super ITileItem> filter) {
        return TileItems.getIn(area, filter).stream().findFirst().orElse(null);
    }

    public static ITileItem getFirstIn(WorldArea area, int ... ids) {
        return TileItems.getFirstIn(area, Predicates.ids((int[])ids));
    }

    public static ITileItem getFirstIn(WorldArea area, String ... names) {
        return TileItems.getFirstIn(area, Predicates.names((String[])names));
    }

    public static ITileItem getFirstIn(WorldArea area) {
        return TileItems.getFirstIn(area, x -> true);
    }

    public static ITileItem getNearestIn(WorldArea area, Predicate<? super ITileItem> filter) {
        return TileItems.getIn(area, filter).stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()))).orElse(null);
    }

    public static ITileItem getNearestIn(WorldArea area, int ... ids) {
        return TileItems.getNearestIn(area, Predicates.ids((int[])ids));
    }

    public static ITileItem getNearestIn(WorldArea area, String ... names) {
        return TileItems.getNearestIn(area, Predicates.names((String[])names));
    }
}

