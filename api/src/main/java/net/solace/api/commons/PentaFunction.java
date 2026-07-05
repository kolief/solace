package net.solace.api.commons;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface PentaFunction<A, B, C, D, E, R> {
    public R apply(A var1, B var2, C var3, D var4, E var5);

    default public <K> PentaFunction<A, B, C, D, E, K> andThen(Function<? super R, ? extends K> after) {
        Objects.requireNonNull(after);
        return (a, b, c, d, e) -> after.apply((R)this.apply(a, b, c, d, e));
    }
}

