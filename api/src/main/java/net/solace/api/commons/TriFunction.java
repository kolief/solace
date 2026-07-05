package net.solace.api.commons;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    public R apply(T var1, U var2, V var3);

    default public <K> TriFunction<T, U, V, K> andThen(Function<? super R, ? extends K> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> after.apply((R)this.apply(t, u, v));
    }
}

