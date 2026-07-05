package net.solace.api.query.entities;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.SceneEntity;
import net.solace.api.query.Query;
import net.solace.api.query.results.SceneEntityQueryResults;
import net.solace.api.util.SceneEntityCameraUtils;
import org.apache.commons.lang3.ArrayUtils;

public abstract class SceneEntityQuery<T extends SceneEntity, Q extends SceneEntityQuery<T, Q>>
extends Query<T, Q, SceneEntityQueryResults<T>> {
    private Integer maxDistance = null;
    private WorldPoint distanceSrc = null;
    private int[] ids = null;
    private String[] names = null;
    private String[] actions = null;
    private WorldPoint[] locations = null;
    private LocalPoint[] localLocations = null;
    private String nameContains = null;
    private WorldArea[] within = null;
    private String actionContains = null;
    private WorldView worldView = null;
    private Boolean onScreen = null;
    private Integer maxYawDelta = null;

    protected SceneEntityQuery(Supplier<List<T>> supplier) {
        super(supplier);
    }

    public Q ids(int ... ids) {
        this.ids = ids;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q names(String ... names) {
        this.names = names;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q nameContains(String text) {
        this.nameContains = text;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q actions(String ... actions) {
        this.actions = actions;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q actionContains(String text) {
        this.actionContains = text;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q locations(WorldPoint ... locations) {
        this.locations = locations;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q localLocations(LocalPoint ... localLocations) {
        this.localLocations = localLocations;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q distance(Locatable source, int maxDistance) {
        return this.distance(source.getWorldLocation(), maxDistance);
    }

    public Q distance(WorldPoint source, int maxDistance) {
        this.distanceSrc = source;
        this.maxDistance = maxDistance;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q distance(int maxDistance) {
        this.maxDistance = maxDistance;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q within(WorldArea ... areas) {
        this.within = areas;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q worldView(WorldView worldView) {
        this.worldView = worldView;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q onScreen() {
        this.onScreen = true;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q offScreen() {
        this.onScreen = false;
        return (Q)((SceneEntityQuery)this.self());
    }

    public Q maxYawDelta(int maxYawDelta) {
        this.maxYawDelta = maxYawDelta;
        return (Q)((SceneEntityQuery)this.self());
    }

    @Override
    public SceneEntityQueryResults<T> results() {
        SceneEntityCameraUtils.CameraSnapshot snapshot = this.requiresCameraSnapshot() ? SceneEntityCameraUtils.captureSnapshot() : null;
        return this.results(((List<T>) this.supplier.get()).stream().filter(entity -> this.test(entity, snapshot)).collect(Collectors.toList()));
    }

    @Override
    public boolean test(T t) {
        SceneEntityCameraUtils.CameraSnapshot snapshot = this.requiresCameraSnapshot() ? SceneEntityCameraUtils.captureSnapshot() : null;
        return this.test(t, snapshot);
    }

    private boolean test(T t, SceneEntityCameraUtils.CameraSnapshot snapshot) {
        if (this.ids != null && !ArrayUtils.contains((int[])this.ids, (int)t.getId())) {
            return false;
        }
        String entityName = t.getName();
        if (this.names != null) {
            if (entityName == null) {
                return false;
            }
            if (!ArrayUtils.contains((Object[])this.names, (Object)entityName)) {
                return false;
            }
        }
        if (this.nameContains != null) {
            if (entityName == null) {
                return false;
            }
            if (!entityName.contains(this.nameContains)) {
                return false;
            }
        }
        if (this.locations != null && !ArrayUtils.contains((Object[])this.locations, (Object)t.getWorldLocation())) {
            return false;
        }
        if (this.localLocations != null && !ArrayUtils.contains((Object[])this.localLocations, (Object)t.getLocalLocation())) {
            return false;
        }
        String[] entityActions = t.getActions();
        if (this.actions != null) {
            if (entityActions == null) {
                return false;
            }
            if (Arrays.stream(this.actions).noneMatch(Predicates.texts(entityActions))) {
                return false;
            }
        }
        if (this.actionContains != null) {
            if (entityActions == null) {
                return false;
            }
            if (Arrays.stream(entityActions).noneMatch(Predicates.textContains(this.actionContains))) {
                return false;
            }
        }
        if (this.maxDistance != null && this.distanceSrc != null && this.distanceSrc.distanceTo(t.getWorldLocation()) > this.maxDistance) {
            return false;
        }
        if (this.within != null) {
            boolean contains = false;
            for (WorldArea worldArea : this.within) {
                if (!worldArea.contains(t.getWorldLocation())) continue;
                contains = true;
            }
            if (!contains) {
                return false;
            }
        }
        if (this.worldView != null && this.worldView != t.getWorldView()) {
            return false;
        }
        if (this.requiresCameraSnapshot()) {
            int yawDelta;
            boolean visible;
            if (snapshot == null) {
                return false;
            }
            if (this.onScreen != null && (visible = SceneEntityCameraUtils.isOnScreen(t, snapshot)) != this.onScreen) {
                return false;
            }
            if (this.maxYawDelta != null && ((yawDelta = SceneEntityCameraUtils.getYawDelta(t, snapshot)) == Integer.MAX_VALUE || yawDelta > this.maxYawDelta)) {
                return false;
            }
        }
        return super.test(t);
    }

    private boolean requiresCameraSnapshot() {
        return this.onScreen != null || this.maxYawDelta != null;
    }
}

