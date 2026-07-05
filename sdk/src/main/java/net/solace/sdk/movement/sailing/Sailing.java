package net.solace.sdk.movement.sailing;

import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.interact.InteractMethod;
import net.solace.api.movement.ISailing;
import net.solace.api.movement.pathfinder.model.sailing.SailingDirection;
import net.solace.api.sailing.Ship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sailing {
    private static final Logger log = LoggerFactory.getLogger(Sailing.class);
    private static final ISailing SAILING = Static.getSailing();

    public static SailingDirection getDirection() {
        return SAILING.getDirection();
    }

    public static boolean isMoving() {
        return SAILING.isMoving();
    }

    public static boolean isNavigating() {
        return SAILING.isNavigating();
    }

    public static void setDirection(InteractMethod interactMethod, SailingDirection direction) {
        SAILING.setDirection(interactMethod, direction);
    }

    public static void setDirection(SailingDirection direction) {
        SAILING.setDirection(direction);
    }

    public static void setDirection(InteractMethod interactMethod, WorldPoint target) {
        SAILING.setDirection(interactMethod, target);
    }

    public static void setDirection(WorldPoint target) {
        SAILING.setDirection(target);
    }

    public static boolean navigate(InteractMethod interactMethod) {
        return SAILING.navigate(interactMethod);
    }

    public static boolean navigate() {
        return SAILING.navigate();
    }

    public static boolean stopNavigating(InteractMethod interactMethod) {
        return SAILING.stopNavigating(interactMethod);
    }

    public static boolean stopNavigating() {
        return SAILING.stopNavigating();
    }

    public static boolean setSails(InteractMethod interactMethod) {
        return SAILING.setSails(interactMethod);
    }

    public static boolean setSails() {
        return SAILING.setSails();
    }

    public static boolean unsetSails(InteractMethod interactMethod) {
        return SAILING.unsetSails(interactMethod);
    }

    public static boolean unsetSails() {
        return SAILING.unsetSails();
    }

    public static boolean increaseSpeed(InteractMethod interactMethod) {
        return SAILING.increaseSpeed(interactMethod);
    }

    public static boolean increaseSpeed() {
        return SAILING.increaseSpeed();
    }

    public static boolean decreaseSpeed(InteractMethod interactMethod) {
        return SAILING.decreaseSpeed(interactMethod);
    }

    public static boolean decreaseSpeed() {
        return SAILING.decreaseSpeed();
    }

    public static boolean reverse(InteractMethod interactMethod) {
        return SAILING.reverse(interactMethod);
    }

    public static boolean reverse() {
        return SAILING.reverse();
    }

    public static Ship getShip() {
        return SAILING.getShip();
    }

    public static boolean isOnBoat() {
        return SAILING.isOnBoat();
    }
}

