package net.solace.sdk.input;

import java.awt.Point;
import java.util.List;
import net.solace.api.Static;
import net.solace.api.domain.Interactable;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.mouse.MouseMovementStrategy;

public final class MouseMovement {
    private MouseMovement() {
    }

    public static void setMouseMovementStrategy(MouseMovementStrategy mouseMovementStrategy) {
        Static.getMouseManager().setMouseMovementStrategy(mouseMovementStrategy);
    }

    public static MouseMovementStrategy getMouseMovementStrategy() {
        return Static.getMouseManager().getMouseMovementStrategy();
    }

    public static void move(int x, int y) {
        Static.getMouseManager().move(x, y);
    }

    public static void click(int x, int y) {
        Static.getMouseManager().click(x, y);
    }

    public static void release() {
        Static.getMouseManager().release();
    }

    public static List<Point> resamplePath(List<Point> points, Point targetPoint) {
        return Static.getMouseManager().resamplePath(points, targetPoint);
    }

    public static List<Point> resamplePath(List<Point> points, int targetPointCount) {
        return Static.getMouseManager().resamplePath(points, targetPointCount);
    }

    public static void moveAlongPath(List<Point> points) {
        Static.getMouseManager().moveAlongPath(points);
    }

    public static void moveAlongPath(List<Point> points, int baseDelay, int delayVariation, boolean easeMovement, double easeStrength, boolean fatigueEnabled, double fatigueMultiplier) {
        Static.getMouseManager().moveAlongPath(points, baseDelay, delayVariation, easeMovement, easeStrength, fatigueEnabled, fatigueMultiplier);
    }

    public static void moveTo(Point point) {
        Static.getMouseManager().moveTo(point);
    }

    public static void moveTo(AutomatedMenu automatedMenu) {
        Static.getMouseManager().moveTo(automatedMenu);
    }

    public static void moveTo(Interactable interactable) {
        Static.getMouseManager().moveTo(interactable);
    }

    public static boolean isMoving() {
        return Static.getMouseManager().isMoving();
    }
}

