package net.solace.api.interact.mouse;

import java.awt.Point;
import java.util.List;
import net.solace.api.domain.Interactable;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.mouse.MouseMovementStrategy;

public interface MouseManager {
    public void setMouseMovementStrategy(MouseMovementStrategy var1);

    public MouseMovementStrategy getMouseMovementStrategy();

    public void move(int var1, int var2);

    public void click(int var1, int var2);

    public void rightClick(int var1, int var2);

    public void release();

    public List<Point> resamplePath(List<Point> var1, Point var2);

    public List<Point> resamplePath(List<Point> var1, int var2);

    public void moveAlongPath(List<Point> var1);

    public void moveAlongPath(List<Point> var1, int var2, int var3, boolean var4, double var5, boolean var7, double var8);

    public void moveTo(Point var1);

    public void moveTo(AutomatedMenu var1);

    public void positionTo(AutomatedMenu var1);

    public Point resolveMenuPoint(AutomatedMenu var1);

    public void moveTo(Interactable var1);

    public boolean isMoving();
}

