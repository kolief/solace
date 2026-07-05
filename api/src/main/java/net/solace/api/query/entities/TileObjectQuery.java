package net.solace.api.query.entities;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.query.entities.SceneEntityQuery;
import net.solace.api.query.results.SceneEntityQueryResults;
import org.apache.commons.lang3.ArrayUtils;

public class TileObjectQuery
extends SceneEntityQuery<ITileObject, TileObjectQuery> {
    private ITile[] tiles = null;
    private Class<? extends ITileObject>[] is = null;

    public TileObjectQuery(Supplier<List<ITileObject>> supplier) {
        super(supplier);
    }

    public TileObjectQuery tiles(ITile ... tiles) {
        this.tiles = tiles;
        return this;
    }

    @SafeVarargs
    public final TileObjectQuery is(Class<? extends ITileObject> ... classes) {
        this.is = classes;
        return this;
    }

    @Override
    protected SceneEntityQueryResults<ITileObject> results(List<ITileObject> list) {
        return new SceneEntityQueryResults<ITileObject>(list);
    }

    @Override
    public boolean test(ITileObject tileObject) {
        if (this.tiles != null && !ArrayUtils.contains((Object[])this.tiles, (Object)tileObject.getTile())) {
            return false;
        }
        if (this.is != null && Arrays.stream(this.is).noneMatch(clazz -> clazz.isInstance(tileObject))) {
            return false;
        }
        return super.test(tileObject);
    }
}

