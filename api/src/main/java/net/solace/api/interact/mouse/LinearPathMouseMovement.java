package net.solace.api.interact.mouse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import net.solace.api.interact.mouse.MouseMovementStrategy;

public class LinearPathMouseMovement
implements MouseMovementStrategy {
    private final int steps;

    public LinearPathMouseMovement(int steps) {
        this.steps = Math.max(1, steps);
    }

    public LinearPathMouseMovement() {
        this(10);
    }

    @Override
    public MouseMovementStrategy.MousePath generatePath(Point current, Point target) {
        if (current == null || current.equals(target)) {
            return new MouseMovementStrategy.MousePath(List.of(target));
        }
        ArrayList<Point> path = new ArrayList<Point>(this.steps);
        double dx = (double)(target.x - current.x) / (double)this.steps;
        double dy = (double)(target.y - current.y) / (double)this.steps;
        for (int i = 1; i <= this.steps; ++i) {
            int x = (int)((double)current.x + dx * (double)i);
            int y = (int)((double)current.y + dy * (double)i);
            path.add(new Point(x, y));
        }
        return new MouseMovementStrategy.MousePath(path);
    }
}

