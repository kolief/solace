package net.solace.sdk.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.worldmap.WorldMap;
import net.solace.api.Static;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.sdk.coords.Coordinate;
import net.solace.sdk.entities.Tiles;
import net.solace.sdk.utils.CoordUtils;

public class DrawUtils {
    private static final Color TRANSPORT_COLOR = new Color(0, 255, 0, 128);
    private static final Color TILE_BLOCKED_COLOR = new Color(0, 128, 255, 128);

    public static void drawOnMap(Graphics2D graphics, Locatable locatable, Color color) {
        DrawUtils.drawOnMap(graphics, locatable.getWorldLocation(), color);
    }

    public static void drawOnMap(Graphics2D graphics, ITile tile, Color color) {
        DrawUtils.drawOnMap(graphics, tile.getWorldLocation(), color);
    }

    public static void drawOnMap(Graphics2D graphics, WorldPoint point, Color color) {
        WorldMap ro = net.solace.sdk.game.Client.getWorldMap();
        float pixelsPerTile = ro.getWorldMapZoom();
        int tileCenterPixel = (int)Math.ceil(pixelsPerTile / 2.0f);
        Point tile = CoordUtils.worldPointToWorldMap(point);
        Point bottomRightTile = CoordUtils.worldPointToWorldMap(point.dx(1).dy(-1));
        if (tile == null || bottomRightTile == null) {
            return;
        }
        Point topLeft = Coordinate.offsetFrom(tile, -tileCenterPixel, -tileCenterPixel);
        Point bottomRight = Coordinate.offsetFrom(bottomRightTile, -tileCenterPixel, -tileCenterPixel);
        graphics.setColor(color);
        graphics.fillRect(topLeft.getX(), topLeft.getY(), bottomRight.getX() - topLeft.getX(), bottomRight.getY() - topLeft.getY());
    }

    public static void drawTransports(Graphics2D graphics2D) {
        Client client = net.solace.sdk.game.Client.getWrapped();
        List<Transport> transports = Static.getTransportLoader().buildTransports();
        for (Transport transport : transports) {
            Point linkCenter;
            DrawUtils.fillTile(graphics2D, transport.getSource(), TRANSPORT_COLOR);
            Point center = DrawUtils.tileCenter(client, transport.getSource());
            if (center == null || (linkCenter = DrawUtils.tileCenter(client, transport.getDestination())) == null) continue;
            graphics2D.drawLine(center.getX(), center.getY(), linkCenter.getX(), linkCenter.getY());
        }
    }

    public static void drawPath(Graphics2D graphics2D, List<WorldPoint> path) {
        int pathSize = path.size();
        for (int i = 0; i < pathSize; ++i) {
            WorldPoint t = path.get(i);
            DrawUtils.outline(net.solace.sdk.game.Client.getWrapped(), t, graphics2D, Color.RED, String.valueOf(i));
        }
    }

    public static void drawCollisions(Graphics2D graphics2D, CollisionMap collisionMap) {
        Client client = net.solace.sdk.game.Client.getWrapped();
        ITile[][] tiles = Tiles.getRaw();
        if (collisionMap == null) {
            return;
        }
        for (int x = 0; x < 104; ++x) {
            for (int y = 0; y < 104; ++y) {
                String s;
                Polygon poly;
                ITile tile = tiles[x][y];
                if (tile == null || (poly = Perspective.getCanvasTilePoly((Client)client, (LocalPoint)tile.getLocalLocation())) == null) continue;
                StringBuilder sb = new StringBuilder("");
                graphics2D.setColor(Color.WHITE);
                if (!collisionMap.n(tile.getWorldLocation())) {
                    sb.append("n");
                }
                if (!collisionMap.s(tile.getWorldLocation())) {
                    sb.append("s");
                }
                if (!collisionMap.w(tile.getWorldLocation())) {
                    sb.append("w");
                }
                if (!collisionMap.e(tile.getWorldLocation())) {
                    sb.append("e");
                }
                if ((s = sb.toString()).isEmpty()) continue;
                if (!s.equals("nswe")) {
                    graphics2D.setColor(Color.WHITE);
                    if (s.contains("n")) {
                        graphics2D.drawLine(poly.xpoints[3], poly.ypoints[3], poly.xpoints[2], poly.ypoints[2]);
                    }
                    if (s.contains("s")) {
                        graphics2D.drawLine(poly.xpoints[0], poly.ypoints[0], poly.xpoints[1], poly.ypoints[1]);
                    }
                    if (s.contains("w")) {
                        graphics2D.drawLine(poly.xpoints[0], poly.ypoints[0], poly.xpoints[3], poly.ypoints[3]);
                    }
                    if (!s.contains("e")) continue;
                    graphics2D.drawLine(poly.xpoints[1], poly.ypoints[1], poly.xpoints[2], poly.ypoints[2]);
                    continue;
                }
                graphics2D.setColor(TILE_BLOCKED_COLOR);
                graphics2D.fill(poly);
            }
        }
    }

    public static void drawCollisions(Graphics2D graphics2D) {
        DrawUtils.drawCollisions(graphics2D, (CollisionMap)Static.getGlobalCollisionMap());
    }

    public static void fillTile(Graphics2D graphics, WorldPoint point, Color color) {
        Client client = net.solace.sdk.game.Client.getWrapped();
        LocalPoint lp = LocalPoint.fromWorld((Client)client, (WorldPoint)point);
        if (lp == null) {
            return;
        }
        Polygon poly = Perspective.getCanvasTilePoly((Client)client, (LocalPoint)lp);
        if (poly == null) {
            return;
        }
        graphics.setColor(color);
        graphics.fill(poly);
    }

    public static Point tileCenter(Client client, WorldPoint b) {
        LocalPoint lp = LocalPoint.fromWorld((Client)client, (WorldPoint)b);
        if (lp == null) {
            return null;
        }
        Polygon poly = Perspective.getCanvasTilePoly((Client)client, (LocalPoint)lp);
        if (poly == null) {
            return null;
        }
        int cx = poly.getBounds().x + poly.getBounds().width / 2;
        int cy = poly.getBounds().y + poly.getBounds().height / 2;
        return new Point(cx, cy);
    }

    public static void outline(Client client, WorldPoint point, Graphics2D graphics2D, Color color) {
        DrawUtils.outline(client, point, graphics2D, color, null);
    }

    public static void outline(Client client, WorldPoint point, Graphics2D graphics, Color color, String text) {
        LocalPoint localPoint = LocalPoint.fromWorld((Client)client, (WorldPoint)point);
        if (localPoint == null) {
            return;
        }
        Polygon poly = Perspective.getCanvasTilePoly((Client)client, (LocalPoint)localPoint);
        if (poly == null) {
            return;
        }
        if (text != null) {
            int stringX = (int)(poly.getBounds().getCenterX() - graphics.getFont().getStringBounds(text, graphics.getFontRenderContext()).getWidth() / 2.0);
            int stringY = (int)poly.getBounds().getCenterY();
            graphics.setColor(color);
            graphics.drawString(text, stringX, stringY);
        }
        graphics.setColor(color);
        Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(2.0f));
        graphics.draw(poly);
        graphics.setColor(new Color(0, 0, 0, 50));
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }
}

