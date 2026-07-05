package net.solace.api.entities;

import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.TileEntity;
import net.solace.api.entities.SceneEntityProvider;

public interface TileEntityProvider<T extends TileEntity>
extends SceneEntityProvider<T> {
    public List<T> getAll(Predicate<? super T> var1, boolean var2);

    public List<T> getAt(ITile var1, Predicate<? super T> var2);

    default public List<T> getAt(ITile tile, int ... anyIds) {
        return this.getAt(tile, Predicates.ids(anyIds));
    }

    default public List<T> getAt(ITile tile, String ... anyNames) {
        return this.getAt(tile, Predicates.names(anyNames));
    }

    public List<T> getAt(WorldPoint var1, Predicate<? super T> var2);

    default public List<T> getAt(WorldPoint worldPoint, int ... ids) {
        return this.getAt(worldPoint, Predicates.ids(ids));
    }

    default public List<T> getAt(WorldPoint worldPoint, String ... names) {
        return this.getAt(worldPoint, Predicates.names(names));
    }

    default public T getFirstAt(ITile tile, Predicate<? super T> filter) {
        return (T)((TileEntity)this.getAt(tile, filter).stream().findFirst().orElse(null));
    }

    default public T getFirstAt(ITile tile, int ... ids) {
        return this.getFirstAt(tile, Predicates.ids(ids));
    }

    default public T getFirstAt(ITile tile, String ... names) {
        return this.getFirstAt(tile, Predicates.names(names));
    }

    default public T getFirstAt(WorldPoint worldPoint, Predicate<? super T> filter) {
        return (T)((TileEntity)this.getAt(worldPoint, filter).stream().findFirst().orElse(null));
    }

    default public T getFirstAt(WorldPoint worldPoint, int ... ids) {
        return this.getFirstAt(worldPoint, Predicates.ids(ids));
    }

    default public T getFirstAt(WorldPoint worldPoint, String ... names) {
        return this.getFirstAt(worldPoint, Predicates.names(names));
    }

    public List<T> getSurrounding(ITile var1, int var2, Predicate<? super T> var3);

    default public List<T> getSurrounding(ITile tile, int radius, int ... anyIds) {
        return this.getSurrounding(tile, radius, Predicates.ids(anyIds));
    }

    default public List<T> getSurrounding(ITile tile, int radius, String ... anyNames) {
        return this.getSurrounding(tile, radius, Predicates.names(anyNames));
    }

    public List<T> getSurrounding(WorldPoint var1, int var2, Predicate<? super T> var3);

    default public List<T> getSurrounding(WorldPoint worldPoint, int radius, int ... ids) {
        return this.getSurrounding(worldPoint, radius, Predicates.ids(ids));
    }

    default public List<T> getSurrounding(WorldPoint worldPoint, int radius, String ... names) {
        return this.getSurrounding(worldPoint, radius, Predicates.names(names));
    }

    default public T getFirstSurrounding(ITile tile, int radius, Predicate<? super T> filter) {
        return (T)((TileEntity)this.getSurrounding(tile, radius, filter).stream().findFirst().orElse(null));
    }

    default public T getFirstSurrounding(ITile tile, int radius, int ... ids) {
        return this.getFirstSurrounding(tile, radius, Predicates.ids(ids));
    }

    default public T getFirstSurrounding(ITile tile, int radius, String ... names) {
        return this.getFirstSurrounding(tile, radius, Predicates.names(names));
    }

    default public T getFirstSurrounding(WorldPoint worldPoint, int radius, Predicate<? super T> filter) {
        return (T)((TileEntity)this.getSurrounding(worldPoint, radius, filter).stream().findFirst().orElse(null));
    }

    default public T getFirstSurrounding(WorldPoint worldPoint, int radius, int ... ids) {
        return this.getFirstSurrounding(worldPoint, radius, Predicates.ids(ids));
    }

    default public T getFirstSurrounding(WorldPoint worldPoint, int radius, String ... names) {
        return this.getFirstSurrounding(worldPoint, radius, Predicates.names(names));
    }

    public List<T> getIn(WorldArea var1, Predicate<? super T> var2);

    default public List<T> getIn(WorldArea area, int ... anyIds) {
        return this.getIn(area, Predicates.ids(anyIds));
    }

    default public List<T> getIn(WorldArea area, String ... anyNames) {
        return this.getIn(area, Predicates.names(anyNames));
    }

    default public T getFirstIn(WorldArea area, Predicate<? super T> filter) {
        return (T)((TileEntity)this.getIn(area, filter).stream().findFirst().orElse(null));
    }

    default public T getFirstIn(WorldArea area, int ... ids) {
        return this.getFirstIn(area, Predicates.ids(ids));
    }

    default public T getFirstIn(WorldArea area, String ... names) {
        return this.getFirstIn(area, Predicates.names(names));
    }

    default public T getFirstIn(WorldArea area) {
        return (T)this.getFirstIn(area, x -> true);
    }

    public T getNearestIn(WorldArea var1, Predicate<? super T> var2);

    default public T getNearestIn(WorldArea area, int ... ids) {
        return this.getNearestIn(area, Predicates.ids(ids));
    }

    default public T getNearestIn(WorldArea area, String ... names) {
        return this.getNearestIn(area, Predicates.names(names));
    }
}

