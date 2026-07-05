package net.solace.api.util;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Polygon;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.solace.api.Static;
import net.solace.api.commons.Rand;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.game.IClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanvasUtils {
    private static final Logger log = LoggerFactory.getLogger(CanvasUtils.class);

    public static java.awt.Point getPointOrFallbackToRandom(IClient client, java.awt.Point targetPoint) {
        Canvas canvas = client.getCanvas();
        if (targetPoint == null) {
            return CanvasUtils.getRandomPointInCanvas(canvas);
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (targetPoint.getX() >= 0.0 && targetPoint.getY() >= 0.0 && targetPoint.getX() < (double)width && targetPoint.getY() < (double)height) {
            return targetPoint;
        }
        int targetX = targetPoint.x;
        int targetY = targetPoint.y;
        java.awt.Point newPoint = new java.awt.Point(targetX, targetY);
        if (targetX < 0 || targetX > width) {
            newPoint = new java.awt.Point(Rand.nextInt(0, width), targetY);
        }
        if (targetY < 0 || targetY > height) {
            newPoint = new java.awt.Point(targetX, Rand.nextInt(0, height));
        }
        return newPoint;
    }

    public static java.awt.Point getRandomPointInCanvas(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        return new java.awt.Point(Rand.nextInt(0, width), Rand.nextInt(0, height));
    }

    public static java.awt.Point coordinateToPoint(Coordinate coordinate) {
        return new java.awt.Point(coordinate.getX(), coordinate.getY());
    }

    public static java.awt.Point translateCoordinateToCanvas(IClient client, int x, int y) {
        Client wrapped = (Client)client.getWrapped();
        if (wrapped.isStretchedEnabled()) {
            Dimension real = wrapped.getRealDimensions();
            Dimension stretched = wrapped.getStretchedDimensions();
            double xRatio = (double)stretched.width / (double)real.width;
            double yRatio = (double)stretched.height / (double)real.height;
            return new java.awt.Point((int)((double)x * xRatio), (int)((double)y * yRatio));
        }
        return new java.awt.Point(x, y);
    }

    public static boolean isTileOccluded(LocalPoint targetLocal, int plane, Point clickPoint) {
        Client wrappedClient = Static.getWrappedClient();
        WorldView wv = wrappedClient.getTopLevelWorldView();
        if (wv == null) {
            return false;
        }
        int[][][] tileHeights = wv.getTileHeights();
        int targetSceneX = targetLocal.getSceneX();
        int targetSceneY = targetLocal.getSceneY();
        if (targetSceneX < 0 || targetSceneX >= 104 || targetSceneY < 0 || targetSceneY >= 104) {
            return false;
        }
        int targetHeight = tileHeights[plane][targetSceneX][targetSceneY];
        int targetZ1 = CanvasUtils.getCameraDepth(wrappedClient, targetLocal, plane);
        if (targetZ1 < 50) {
            return true;
        }
        int checked = 0;
        for (int sx = 0; sx < 104; ++sx) {
            for (int sy = 0; sy < 104; ++sy) {
                int dy;
                int dx;
                Point otherCenter;
                LocalPoint otherLocal;
                int otherZ1;
                int otherHeight;
                if (sx == targetSceneX && sy == targetSceneY || (otherHeight = tileHeights[plane][sx][sy]) >= targetHeight || (otherZ1 = CanvasUtils.getCameraDepth(wrappedClient, otherLocal = LocalPoint.fromScene((int)sx, (int)sy, (WorldView)wv), plane)) <= 0 || otherZ1 >= targetZ1 || (otherCenter = Perspective.localToCanvas((Client)wrappedClient, (LocalPoint)otherLocal, (int)plane)) == null || (dx = otherCenter.getX() - clickPoint.getX()) * dx + (dy = otherCenter.getY() - clickPoint.getY()) * dy > 10000) continue;
                Polygon poly = Perspective.getCanvasTilePoly((Client)wrappedClient, (LocalPoint)otherLocal);
                ++checked;
                if (poly == null || !poly.contains(clickPoint.getX(), clickPoint.getY())) continue;
                return true;
            }
        }
        log.debug("Occlusion: checked {} elevated tile polygons, none occluding", (Object)checked);
        return false;
    }

    public static Point getMinimapClickPoint(LocalPoint localPoint) {
        Point toMinimap = Static.getGameThread().invokeAndWait(() -> Perspective.localToMinimap((Client)Static.getWrappedClient(), (LocalPoint)localPoint));
        if (toMinimap != null) {
            log.debug("Using minimap coordinates: x={}, y={}", (Object)toMinimap.getX(), (Object)toMinimap.getY());
        }
        return toMinimap;
    }

    public static Point getCanvasClickPoint(LocalPoint localPoint, int plane) {
        Point canv = Perspective.localToCanvas((Client)Static.getWrappedClient(), (LocalPoint)localPoint, (int)plane);
        if (canv == null) {
            return null;
        }
        int x = canv.getX();
        int y = canv.getY();
        int canvasHeight = Static.getWrappedClient().getCanvasHeight();
        int canvasWidth = Static.getWrappedClient().getCanvasWidth();
        if (x < 0 || x > canvasWidth || y < 0 || y > canvasHeight) {
            log.debug("Canvas point ({}, {}) out of bounds ({}x{})", new Object[]{x, y, canvasWidth, canvasHeight});
            return null;
        }
        return canv;
    }

    public static int getCameraDepth(Client client, LocalPoint point, int plane) {
        int tileHeight = Perspective.getTileHeight((Client)client, (LocalPoint)point, (int)plane);
        int x = point.getX() - client.getCameraX();
        int y = point.getY() - client.getCameraY();
        int z = tileHeight - client.getCameraZ();
        int yawSin = Perspective.SINE[client.getCameraYaw()];
        int yawCos = Perspective.COSINE[client.getCameraYaw()];
        int pitchSin = Perspective.SINE[client.getCameraPitch()];
        int pitchCos = Perspective.COSINE[client.getCameraPitch()];
        int y1 = y * yawCos - x * yawSin >> 16;
        return y1 * pitchCos + z * pitchSin >> 16;
    }
}

