package net.solace.impl.entities;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Constants;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.TileEntity;
import net.solace.api.entities.ITiles;
import net.solace.api.entities.TileEntityProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class TileEntitiesImpl<T extends TileEntity> implements TileEntityProvider<T> {
    private final ITiles tiles;
    private final IClient client;
    private final Function<? super ITile, List<T>> tileMapper;

    @Override
    public List<T> getAt(ITile tile, Predicate<? super T> filter) {
        if (tile == null) {
            return Collections.emptyList();
        }

        var out = new ArrayList<>(tileMapper.apply(tile));

        var bridge = tile.getBridge();
        if (bridge != null) {
            out.addAll(tileMapper.apply(bridge));
        }

        return out.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAt(WorldPoint location, Predicate<? super T> filter) {
        var tile = tiles.getAt(location);
        return getAt(tile, filter);
    }

    @Override
    public List<T> getAll(Predicate<? super T> filter) {
        return getAll(filter, false);
    }

    @Override
    public List<T> getAll(Predicate<? super T> filter, boolean allPlanes) {
        var out = new ArrayList<T>();
        var raw = tiles.getRawFloors();
        int currentPlane = allPlanes ? 0 : getWorldView().getPlane();
        int maxPlane = allPlanes ? Constants.MAX_Z : currentPlane + 1;

        for (int z = currentPlane; z < maxPlane; z++) {
            for (int x = 0; x < tiles.getSizeX(); x++) {
                for (int y = 0; y < tiles.getSizeY(); y++) {
                    var tile = raw[z][x][y];
                    if (tile != null) {
                        out.addAll(getFilteredObjectsOnTile(tile, filter));
                    }
                }
            }
        }

        return out;
    }

    @Override
    public List<T> getSurrounding(ITile tile, int radius, Predicate<? super T> filter) {
        var out = new ArrayList<T>();
        var plane = tile.getPlane();
        var sceneLocation = tile.getSceneLocation();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                var newTile = tiles.getAt(plane, new Point(sceneLocation.getX() + x, sceneLocation.getY() + y));
                if (newTile != null) {
                    out.addAll(getFilteredObjectsOnTile(newTile, filter));
                }
            }
        }

        return out;
    }

    @Override
    public List<T> getSurrounding(WorldPoint worldPoint, int radius, Predicate<? super T> filter) {
        var out = new ArrayList<T>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                var tile = tiles.getAt(worldPoint.dx(x).dy(y));
                if (tile != null) {
                    out.addAll(getFilteredObjectsOnTile(tile, filter));
                }
            }
        }

        return out;
    }

    @Override
    public List<T> getIn(WorldArea area, Predicate<? super T> filter) {
        var out = new ArrayList<T>();
        for (var worldPoint : area.toWorldPointList()) {
            var localPoint = LocalPoint.fromWorld(getWorldView(), worldPoint);
            if (localPoint != null) {
                var scenePoint = new Point(localPoint.getSceneX(), localPoint.getSceneY());
                var tile = tiles.getAt(worldPoint.getPlane(), scenePoint);
                if (tile != null) {
                    out.addAll(getFilteredObjectsOnTile(tile, filter));
                }
            }
        }

        return out;
    }

    @Override
    public T getNearestIn(WorldArea area, Predicate<? super T> filter) {
        return getIn(area, filter).stream()
                .min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation())))
                .orElse(null);
    }

    @Override
    public T getNearest(Predicate<? super T> filter) {
        return getNearest(client.getLocalPlayer().getWorldLocation(), filter);
    }

    @Override
    public T getNearest(String... names) {
        return getNearest(client.getLocalPlayer().getWorldLocation(), names);
    }

    @Override
    public T getNearest(int... ids) {
        return getNearest(client.getLocalPlayer().getWorldLocation(), ids);
    }

    /**
     * Game Objects may have a larger area than other objects, meaning they can be present on multiple tiles.
     * This method simply filters out the objects that are not on the tile they are supposed to be on by checking
     * if the object's real scene location is the same as the tile's scene location.
     * <p>
     * We do not filter when doing getAt, because we may want to query objects that are on any of the 'area' tiles.
     *
     * @param tile   the tile to filter objects on
     * @param filter the filter to apply
     * @return the filtered objects
     */
    private List<T> getFilteredObjectsOnTile(ITile tile, Predicate<? super T> filter) {
        return getAt(tile, filter).stream()
                .filter(Objects::nonNull)
                .filter(obj -> !(obj instanceof IGameObject) ||
                        ((IGameObject) obj).getSceneMinLocation().equals(tile.getSceneLocation()))
                .collect(Collectors.toList());
    }

    private WorldView getWorldView() {
        return client.getWrapped().getWorldView(tiles.getWorldViewId());
    }
}
