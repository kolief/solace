package net.solace.sdk.utils;

import java.awt.Rectangle;
import javax.annotation.Nullable;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.worldmap.WorldMap;
import net.solace.api.domain.widgets.IWidget;
import net.solace.sdk.entities.Players;
import net.solace.sdk.game.Client;
import net.solace.sdk.widgets.Widgets;

public class CoordUtils {
    public static Point worldPointToWorldMap(WorldPoint worldPoint) {
        WorldMap worldMap = Client.getWorldMap();
        if (!worldMap.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY())) {
            return null;
        }
        float pixelsPerTile = worldMap.getWorldMapZoom();
        IWidget map = Widgets.get(38993927);
        if (map != null) {
            Rectangle worldMapRect = map.getBounds();
            int widthInTiles = (int)Math.ceil(worldMapRect.getWidth() / (double)pixelsPerTile);
            int heightInTiles = (int)Math.ceil(worldMapRect.getHeight() / (double)pixelsPerTile);
            Point worldMapPosition = worldMap.getWorldMapPosition();
            int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
            int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
            int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();
            int xGraphDiff = (int)((float)xTileOffset * pixelsPerTile);
            int yGraphDiff = (int)((float)yTileOffset * pixelsPerTile);
            yGraphDiff = (int)((double)yGraphDiff - ((double)pixelsPerTile - Math.ceil(pixelsPerTile / 2.0f)));
            xGraphDiff = (int)((double)xGraphDiff + ((double)pixelsPerTile - Math.ceil(pixelsPerTile / 2.0f)));
            yGraphDiff = worldMapRect.height - yGraphDiff;
            return new Point(xGraphDiff += (int)worldMapRect.getX(), yGraphDiff += (int)worldMapRect.getY());
        }
        return null;
    }

    public static WorldPoint worldMapToWorldPoint(Point point) {
        float zoom = Client.getWorldMap().getWorldMapZoom();
        WorldMap renderOverview = Client.getWorldMap();
        WorldPoint mapPoint = new WorldPoint(renderOverview.getWorldMapPosition().getX(), renderOverview.getWorldMapPosition().getY(), 0);
        Point middle = CoordUtils.worldPointToWorldMap(mapPoint);
        if (middle == null) {
            return null;
        }
        int dx = (int)((float)(point.getX() - middle.getX()) / zoom);
        int dy = (int)((float)(-(point.getY() - middle.getY())) / zoom);
        return mapPoint.dx(dx).dy(dy);
    }

    @Nullable
    public static Point localToMinimap(LocalPoint point, int distance) {
        int y;
        LocalPoint localLocation = Players.getLocal().getLocalLocation();
        int x = point.getX() / 32 - localLocation.getX() / 32;
        int dist = x * x + (y = point.getY() / 32 - localLocation.getY() / 32) * y;
        if (dist < distance) {
            IWidget minimap1;
            IWidget minimapDrawWidget = Client.isResized() ? ((minimap1 = Widgets.get(10747934)) != null ? minimap1 : Widgets.get(10551326)) : Widgets.get(35913750);
            if (minimapDrawWidget == null || minimapDrawWidget.isHidden()) {
                return null;
            }
            int angle = Client.getMapAngle() & 0x7FF;
            int sin = Perspective.SINE[angle];
            int cos = Perspective.COSINE[angle];
            int xx = y * sin + cos * x >> 16;
            int yy = sin * x - y * cos >> 16;
            Point loc = minimapDrawWidget.getCanvasLocation();
            int miniMapX = loc.getX() + xx + minimapDrawWidget.getWidth() / 2;
            int miniMapY = minimapDrawWidget.getHeight() / 2 + loc.getY() + yy;
            return new Point(miniMapX, miniMapY);
        }
        return null;
    }
}

