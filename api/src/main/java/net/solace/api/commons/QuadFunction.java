package net.solace.api.commons;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface QuadFunction<A, B, C, D, R> {
    public R apply(A var1, B var2, C var3, D var4);

    default public <K> QuadFunction<A, B, C, D, K> andThen(Function<? super R, ? extends K> after) {
        Objects.requireNonNull(after);
        return (a, b, c, d) -> after.apply((R)this.apply(a, b, c, d));
    }
}

