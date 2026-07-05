package net.solace.api.entities;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.SceneEntity;
import net.solace.api.entities.EntityProvider;

public interface SceneEntityProvider<T extends SceneEntity>
extends EntityProvider<T> {
    @Override
    public List<T> getAll(Predicate<? super T> var1);

    default public List<T> getAll(String ... anyNames) {
        return this.getAll(Predicates.names(anyNames));
    }

    default public List<T> getAll(int ... anyIds) {
        return this.getAll(Predicates.ids(anyIds));
    }

    default public T getNearest(WorldPoint to, Predicate<? super T> filter) {
        return (T)((SceneEntity)this.getAll(x -> x.getId() != -1 && filter.test(x)).stream().min(Comparator.comparingDouble(x -> x.getWorldLocation().distanceTo(to))).orElse(null));
    }

    default public T getNearest(WorldPoint to, String ... anyNames) {
        return this.getNearest(to, Predicates.names(anyNames));
    }

    default public T getNearest(WorldPoint to, int ... anyIds) {
        return this.getNearest(to, Predicates.ids(anyIds));
    }

    public T getNearest(Predicate<? super T> var1);

    public T getNearest(String ... var1);

    public T getNearest(int ... var1);
}

