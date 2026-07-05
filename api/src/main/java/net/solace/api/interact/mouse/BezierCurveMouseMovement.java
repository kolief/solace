package net.solace.api.interact.mouse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.solace.api.interact.mouse.MouseMovementStrategy;

public class BezierCurveMouseMovement
implements MouseMovementStrategy {
    private final int steps;
    private final double controlPointDeviation;

    public BezierCurveMouseMovement(int steps, double controlPointDeviation) {
        this.steps = Math.max(1, steps);
        this.controlPointDeviation = Math.max(0.0, Math.min(1.0, controlPointDeviation));
    }

    public BezierCurveMouseMovement() {
        this(15, 0.3);
    }

    @Override
    public MouseMovementStrategy.MousePath generatePath(Point current, Point target) {
        if (current == null || current.equals(target)) {
            return new MouseMovementStrategy.MousePath(List.of(target));
        }
        Point controlPoint = this.generateControlPoint(current, target);
        ArrayList<Point> path = new ArrayList<Point>(this.steps);
        for (int i = 1; i <= this.steps; ++i) {
            double t = (double)i / (double)this.steps;
            double oneMinusT = 1.0 - t;
            int x = (int)(oneMinusT * oneMinusT * (double)current.x + 2.0 * oneMinusT * t * (double)controlPoint.x + t * t * (double)target.x);
            int y = (int)(oneMinusT * oneMinusT * (double)current.y + 2.0 * oneMinusT * t * (double)controlPoint.y + t * t * (double)target.y);
            path.add(new Point(x, y));
        }
        return new MouseMovementStrategy.MousePath(path);
    }

    private Point generateControlPoint(Point current, Point target) {
        int midX = (current.x + target.x) / 2;
        int midY = (current.y + target.y) / 2;
        double distance = Math.sqrt(Math.pow(target.x - current.x, 2.0) + Math.pow(target.y - current.y, 2.0));
        double maxOffset = distance * this.controlPointDeviation;
        double offset = maxOffset > 0.0 ? ThreadLocalRandom.current().nextDouble(-maxOffset, maxOffset) : 0.0;
        double dx = target.x - current.x;
        double dy = target.y - current.y;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length == 0.0) {
            return new Point(midX, midY);
        }
        double perpX = -dy / length;
        double perpY = dx / length;
        int controlX = (int)((double)midX + perpX * offset);
        int controlY = (int)((double)midY + perpY * offset);
        return new Point(controlX, controlY);
    }
}

