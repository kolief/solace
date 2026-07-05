package net.solace.api.movement;

import net.runelite.api.coords.WorldPoint;
import net.solace.api.interact.InteractMethod;
import net.solace.api.movement.pathfinder.model.sailing.SailingDirection;
import net.solace.api.sailing.Ship;

public interface ISailing {
    public SailingDirection getDirection();

    public boolean isMoving();

    public boolean isNavigating();

    public void setDirection(InteractMethod var1, SailingDirection var2);

    default public void setDirection(SailingDirection direction) {
        this.setDirection(null, direction);
    }

    public void setDirection(InteractMethod var1, WorldPoint var2);

    default public void setDirection(WorldPoint target) {
        this.setDirection(null, target);
    }

    public boolean navigate(InteractMethod var1);

    default public boolean navigate() {
        return this.navigate(null);
    }

    public boolean stopNavigating(InteractMethod var1);

    default public boolean stopNavigating() {
        return this.stopNavigating(null);
    }

    public boolean setSails(InteractMethod var1);

    default public boolean setSails() {
        return this.setSails(null);
    }

    public boolean unsetSails(InteractMethod var1);

    default public boolean unsetSails() {
        return this.unsetSails(null);
    }

    public boolean increaseSpeed(InteractMethod var1);

    default public boolean increaseSpeed() {
        return this.increaseSpeed(null);
    }

    public boolean decreaseSpeed(InteractMethod var1);

    default public boolean decreaseSpeed() {
        return this.decreaseSpeed(null);
    }

    public boolean reverse(InteractMethod var1);

    default public boolean reverse() {
        return this.reverse(null);
    }

    public Ship getShip();

    public boolean isOnBoat();
}

