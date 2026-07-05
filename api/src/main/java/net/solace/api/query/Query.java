package net.solace.api.query;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class Query<T, Q, R>
implements Predicate<T> {
    protected final Supplier<List<T>> supplier;
    private Predicate<T> customFilter = null;

    protected Query(Supplier<List<T>> supplier) {
        this.supplier = supplier;
    }

    public R results() {
        return this.results(this.supplier.get().stream().filter(this).collect(Collectors.toList()));
    }

    public Q filter(Predicate<T> filter) {
        if (this.customFilter != null) {
            Predicate old = this.customFilter;
            this.customFilter = t -> old.test(t) && filter.test(t);
        } else {
            this.customFilter = filter;
        }
        return this.self();
    }

    @Override
    public boolean test(T t) {
        return this.customFilter == null || this.customFilter.test(t);
    }

    protected abstract R results(List<T> var1);

    protected Q self() {
        return (Q)this;
    }
}

