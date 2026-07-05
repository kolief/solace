package net.solace.api.entities;

import java.util.List;
import java.util.function.Predicate;

public interface EntityProvider<T> {
    public List<T> getAll(Predicate<? super T> var1);
}

