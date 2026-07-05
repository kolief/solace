package net.solace.api.domain;

import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public interface Locatable {
    public WorldPoint getWorldLocation();

    public LocalPoint getLocalLocation();

    default public int distanceTo(Locatable other) {
        return this.getWorldLocation().distanceTo(other.getWorldLocation());
    }

    default public int distanceTo(WorldPoint other) {
        return this.getWorldLocation().distanceTo(other);
    }

    default public float distanceToHypotenuse(WorldPoint target) {
        if (this.getPlane() != target.getPlane()) {
            return Float.MAX_VALUE;
        }
        return this.distanceTo2DHypotenuse(target);
    }

    default public float distanceTo2DHypotenuse(WorldPoint target) {
        return (float)Math.hypot(this.getWorldX() - target.getX(), this.getWorldY() - target.getY());
    }

    default public int getWorldX() {
        return this.getWorldLocation().getX();
    }

    default public int getWorldY() {
        return this.getWorldLocation().getY();
    }

    public int getPlane();
}

