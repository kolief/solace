package net.solace.loader.interact;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.game.IClient;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.mouse.DirectMouseMovement;
import net.solace.api.interact.mouse.MouseManager;
import net.solace.api.interact.mouse.MouseMovementStrategy;
import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LoaderMouseManager implements MouseManager {
    private final IClient client;

    @Getter
    @Setter
    private MouseMovementStrategy mouseMovementStrategy = new DirectMouseMovement();

    private Point currentPosition;
    private boolean moving;

    public LoaderMouseManager(IClient client) {
        this.client = client;
    }

    @Override
    public void setMouseMovementStrategy(MouseMovementStrategy mouseMovementStrategy) {
        Preconditions.checkNotNull(mouseMovementStrategy, "Mouse movement strategy cannot be null");
        this.mouseMovementStrategy = mouseMovementStrategy;
    }

    @Override
    public void move(int x, int y) {
        dispatchMouseEvent(MouseEvent.MOUSE_MOVED, x, y, 0, false);
        currentPosition = new Point(x, y);
    }

    @Override
    public void click(int x, int y) {
        dispatchMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, MouseEvent.BUTTON1_DOWN_MASK, false);
        currentPosition = new Point(x, y);
    }

    @Override
    public void rightClick(int x, int y) {
        dispatchMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, MouseEvent.BUTTON3_DOWN_MASK, false);
        currentPosition = new Point(x, y);
    }

    @Override
    public void release() {
        dispatchMouseEvent(MouseEvent.MOUSE_RELEASED, 0, 0, 0, false);
    }

    @Override
    public List<Point> resamplePath(List<Point> points, Point targetPoint) {
        if (points == null || points.isEmpty()) {
            return List.of(targetPoint);
        }
        return resamplePath(points, points.size());
    }

    @Override
    public List<Point> resamplePath(List<Point> points, int targetPointCount) {
        if (points == null || points.isEmpty()) {
            return List.of();
        }
        if (targetPointCount <= 0 || points.size() <= targetPointCount) {
            return new ArrayList<>(points);
        }

        var resampled = new ArrayList<Point>(targetPointCount);
        for (int i = 0; i < targetPointCount; i++) {
            int index = (int) Math.round((double) i / (targetPointCount - 1) * (points.size() - 1));
            resampled.add(points.get(index));
        }
        return resampled;
    }

    @Override
    public void moveAlongPath(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return;
        }
        moving = true;
        try {
            for (Point point : points) {
                move(point.x, point.y);
            }
        } finally {
            moving = false;
        }
    }

    @Override
    public void moveAlongPath(
            List<Point> points,
            int baseDelay,
            int delayVariation,
            boolean easeMovement,
            double easeStrength,
            boolean fatigueEnabled,
            double fatigueMultiplier
    ) {
        moveAlongPath(points);
    }

    @Override
    public void moveTo(Point point) {
        if (point == null) {
            return;
        }
        var path = mouseMovementStrategy.generatePath(currentPosition, point);
        moveAlongPath(path.getPoints());
    }

    @Override
    public void moveTo(AutomatedMenu automatedMenu) {
        var point = resolveMenuPoint(automatedMenu);
        if (point != null) {
            moveTo(point);
        }
    }

    @Override
    public void positionTo(AutomatedMenu automatedMenu) {
        moveTo(automatedMenu);
    }

    @Override
    public Point resolveMenuPoint(AutomatedMenu automatedMenu) {
        if (automatedMenu == null) {
            return null;
        }
        var clickPoint = automatedMenu.getClickPoint();
        return clickPoint != null ? new Point(clickPoint.getX(), clickPoint.getY()) : null;
    }

    @Override
    public void moveTo(Interactable interactable) {
        if (interactable == null) {
            return;
        }
        var clickPoint = interactable.getClickPoint();
        if (clickPoint != null) {
            moveTo(new Point(clickPoint.getX(), clickPoint.getY()));
        }
    }

    @Override
    public boolean isMoving() {
        return moving;
    }

    Point getCurrentPosition() {
        return currentPosition;
    }

    void setCurrentPosition(Point currentPosition) {
        this.currentPosition = currentPosition;
    }

    void setMoving(boolean moving) {
        this.moving = moving;
    }

    private void dispatchMouseEvent(int id, int x, int y, int modifiers, boolean popupTrigger) {
        var canvas = client.getCanvas();
        var translated = translateToCanvas(x, y);
        canvas.dispatchEvent(new MouseEvent(
                canvas.getParent(),
                id,
                System.currentTimeMillis(),
                modifiers,
                translated.x,
                translated.y,
                0,
                popupTrigger
        ));
    }

    private Point translateToCanvas(int x, int y) {
        var wrapped = client.getWrapped();
        if (wrapped.isStretchedEnabled()) {
            var real = wrapped.getRealDimensions();
            var stretched = wrapped.getStretchedDimensions();

            var xRatio = (double) stretched.width / real.width;
            var yRatio = (double) stretched.height / real.height;

            return new Point(
                    (int) (x * xRatio),
                    (int) (y * yRatio)
            );
        }

        return new Point(x, y);
    }
}
