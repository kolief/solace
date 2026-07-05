package net.solace.api.domain;

public interface Transformable<C> {
    public int getActualId();

    public C getTransformedComposition();
}

