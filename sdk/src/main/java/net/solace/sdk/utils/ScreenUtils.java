package net.solace.sdk.utils;

import java.awt.Rectangle;
import net.runelite.api.Point;
import net.solace.api.domain.widgets.IWidget;
import net.solace.sdk.game.Client;
import net.solace.sdk.widgets.Widgets;

public class ScreenUtils {
    private static final int MINIMAP_WIDTH = 250;
    private static final int MINIMAP_HEIGHT = 180;

    public static boolean isClickOffScreen(Point point) {
        return ScreenUtils.isClickOffScreen(point.getX(), point.getY());
    }

    public static boolean isClickOffScreen(java.awt.Point point) {
        return ScreenUtils.isClickOffScreen(point.x, point.y);
    }

    public static boolean isClickOffScreen(int x, int y) {
        return x < 0 || y < 0 || x > Client.getViewportWidth() || y > Client.getViewportHeight();
    }

    public static boolean isClickInsideMinimap(int x, int y) {
        return ScreenUtils.getMinimap().contains(x, y);
    }

    public static boolean isClickInsideMinimap(java.awt.Point point) {
        return ScreenUtils.getMinimap().contains(point);
    }

    public static boolean isClickInsideMinimap(Point point) {
        return ScreenUtils.getMinimap().contains(point.getX(), point.getY());
    }

    public static Rectangle getMinimap() {
        IWidget minimap = Widgets.get(35913750);
        if (Widgets.isVisible(minimap)) {
            return minimap.getBounds();
        }
        IWidget minimap1 = Widgets.get(10747934);
        if (Widgets.isVisible(minimap1)) {
            return minimap1.getBounds();
        }
        IWidget minimap2 = Widgets.get(10551326);
        if (Widgets.isVisible(minimap2)) {
            return minimap2.getBounds();
        }
        Rectangle bounds = Client.getCanvas().getBounds();
        return new Rectangle(bounds.width - 250, 0, 250, 180);
    }
}

