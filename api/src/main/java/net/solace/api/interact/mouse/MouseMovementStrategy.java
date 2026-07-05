package net.solace.api.interact.mouse;

import java.awt.Point;
import java.util.List;

public interface MouseMovementStrategy {
    public MousePath generatePath(Point var1, Point var2);

    public static class MousePath {
        private final List<Point> points;

        public MousePath(List<Point> points) {
            this.points = points;
        }

        public List<Point> getPoints() {
            return this.points;
        }
    }
}

