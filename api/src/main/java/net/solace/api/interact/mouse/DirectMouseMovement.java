package net.solace.api.interact.mouse;

import java.awt.Point;
import java.util.List;
import net.solace.api.interact.mouse.MouseMovementStrategy;

public class DirectMouseMovement
implements MouseMovementStrategy {
    @Override
    public MouseMovementStrategy.MousePath generatePath(Point current, Point target) {
        return new MouseMovementStrategy.MousePath(List.of(target));
    }
}

