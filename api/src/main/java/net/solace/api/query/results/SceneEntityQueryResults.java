package net.solace.api.query.results;

import java.util.Comparator;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.SceneEntity;
import net.solace.api.query.results.Distance;
import net.solace.api.query.results.QueryResults;
import net.solace.api.util.SceneEntityCameraUtils;

public class SceneEntityQueryResults<T extends SceneEntity>
extends QueryResults<T, SceneEntityQueryResults<T>> {
    public SceneEntityQueryResults(List<T> results) {
        super(results);
    }

    public SceneEntityQueryResults<T> sortedByDistance(WorldPoint to, Distance distance) {
        return (SceneEntityQueryResults)this.sorted(Comparator.comparingDouble(entity -> {
            switch (distance) {
                case HYPOTENUSE: {
                    return entity.distanceToHypotenuse(to);
                }
                case HYPOTENUSE2D: {
                    return entity.distanceTo2DHypotenuse(to);
                }
            }
            return entity.distanceTo(to);
        }));
    }

    public SceneEntityQueryResults<T> sortedByDistance(WorldPoint to) {
        return this.sortedByDistance(to, Distance.DEFAULT);
    }

    public SceneEntityQueryResults<T> sortedByDistance(Locatable to, Distance distance) {
        return this.sortedByDistance(to.getWorldLocation(), distance);
    }

    public SceneEntityQueryResults<T> sortedByDistance(Locatable to) {
        return this.sortedByDistance(to.getWorldLocation(), Distance.DEFAULT);
    }

    public SceneEntityQueryResults<T> sortedByYawDelta() {
        SceneEntityCameraUtils.CameraSnapshot snapshot = SceneEntityCameraUtils.captureSnapshot();
        if (snapshot == null) {
            return this;
        }
        return (SceneEntityQueryResults)this.sorted(Comparator.comparingInt(entity -> SceneEntityCameraUtils.getYawDelta(entity, snapshot)));
    }

    public SceneEntityQueryResults<T> sortedByInteractionCost() {
        SceneEntityCameraUtils.CameraSnapshot snapshot = SceneEntityCameraUtils.captureSnapshot();
        if (snapshot == null) {
            return this;
        }
        return this.sortedByInteractionCost(snapshot, snapshot.getPlayerWorldLocation());
    }

    public SceneEntityQueryResults<T> sortedByInteractionCost(Locatable to) {
        return this.sortedByInteractionCost(to.getWorldLocation());
    }

    public SceneEntityQueryResults<T> sortedByInteractionCost(WorldPoint to) {
        SceneEntityCameraUtils.CameraSnapshot snapshot = SceneEntityCameraUtils.captureSnapshot();
        if (snapshot == null) {
            return this.sortedByDistance(to);
        }
        return this.sortedByInteractionCost(snapshot, to);
    }

    public T nearest(Locatable to, Distance distance) {
        return (T)((SceneEntity)this.sortedByDistance(to, distance).first());
    }

    public T nearest(WorldPoint to) {
        return this.nearest(to, Distance.DEFAULT);
    }

    public T nearest(WorldPoint to, Distance distance) {
        return (T)((SceneEntity)this.sortedByDistance(to, distance).first());
    }

    public T nearest(Locatable locatable) {
        return this.nearest(locatable, Distance.DEFAULT);
    }

    public T bestCameraCandidate() {
        return (T)((SceneEntity)this.sortedByInteractionCost().first());
    }

    public T bestCameraCandidate(Locatable locatable) {
        return (T)((SceneEntity)this.sortedByInteractionCost(locatable).first());
    }

    public T farthest(Locatable locatable, Distance distance) {
        return (T)((SceneEntity)this.sortedByDistance(locatable, distance).last());
    }

    public T farthest(Locatable locatable) {
        return this.farthest(locatable, Distance.DEFAULT);
    }

    public T farthest(WorldPoint to, Distance distance) {
        return (T)((SceneEntity)this.sortedByDistance(to, distance).last());
    }

    public T farthest(WorldPoint to) {
        return this.farthest(to, Distance.DEFAULT);
    }

    private SceneEntityQueryResults<T> sortedByInteractionCost(SceneEntityCameraUtils.CameraSnapshot snapshot, WorldPoint to) {
        return this.sorted(Comparator.comparingInt((T entity) -> SceneEntityCameraUtils.isOnScreen(entity, snapshot) ? 0 : 1).thenComparingInt((T entity) -> SceneEntityCameraUtils.getYawDelta(entity, snapshot)).thenComparingDouble((T entity) -> to != null ? (double) entity.distanceTo(to) : 0.0));
    }
}

