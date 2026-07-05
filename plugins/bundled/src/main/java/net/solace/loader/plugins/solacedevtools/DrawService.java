package net.solace.loader.plugins.solacedevtools;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.entities.ITiles;
import net.solace.api.movement.IWalker;
import net.solace.api.movement.pathfinder.CollisionMap;
import net.solace.api.movement.pathfinder.GlobalCollisionMap;
import net.solace.api.movement.pathfinder.model.Transport;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class DrawService {
    private static final Color TRANSPORT_COLOR = new Color(0, 255, 0, 128);
    private static final Color TILE_BLOCKED_COLOR = new Color(0, 128, 255, 128);

    private final Client client;
    private final ITiles tiles;
    private final GlobalCollisionMap globalCollisionMap;
    private final IWalker walker;

    public void drawPath(Graphics2D graphics2D, List<WorldPoint> path) {
        for (int i = 0, pathSize = path.size(); i < pathSize; i++) {
            var t = path.get(i);
            outline(t, graphics2D, Color.RED, String.valueOf(i));
        }
    }

    public void outline(WorldPoint point, Graphics2D graphics, Color color, String text) {
        var localPoint = LocalPoint.fromWorld(client, point);
        if (localPoint == null) {
            return;
        }

        var poly = Perspective.getCanvasTilePoly(client, localPoint);
        if (poly == null) {
            return;
        }

        if (text != null) {
            var stringX = (int) (poly.getBounds().getCenterX() -
                    graphics.getFont().getStringBounds(text, graphics.getFontRenderContext()).getWidth() / 2);
            var stringY = (int) poly.getBounds().getCenterY();
            graphics.setColor(color);
            graphics.drawString(text, stringX, stringY);
        }

        graphics.setColor(color);
        final var originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(2));
        graphics.draw(poly);
        graphics.setColor(new Color(0, 0, 0, 50));
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }

    public void drawCollisions(Graphics2D graphics2D) {
        drawCollisions(graphics2D, globalCollisionMap);
    }

    public void drawCollisions(Graphics2D graphics2D, CollisionMap collisionMap) {
        var tiles = this.tiles.getRaw();

        if (collisionMap == null) {
            return;
        }

        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                var tile = tiles[x][y];
                if (tile == null) {
                    continue;
                }

                Polygon poly = Perspective.getCanvasTilePoly(client, tile.getLocalLocation());
                if (poly == null) {
                    continue;
                }

                StringBuilder sb = new StringBuilder();
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

                String s = sb.toString();
                if (s.isEmpty()) {
                    continue;
                }

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

                    if (s.contains("e")) {
                        graphics2D.drawLine(poly.xpoints[1], poly.ypoints[1], poly.xpoints[2], poly.ypoints[2]);
                    }

                    continue;
                }

                graphics2D.setColor(TILE_BLOCKED_COLOR);
                graphics2D.fill(poly);
            }
        }
    }

    public void drawLastPath(Graphics2D graphics2D) {
        var lastPath = walker.getLastPath();
        if (lastPath != null) {
            drawPath(graphics2D, lastPath);
        }
    }

    public void drawTransports(Graphics2D graphics2D) {
        var transports = Static.getTransportLoader().buildTransports();

        for (Transport transport : transports) {
            fillTile(graphics2D, transport.getSource(), TRANSPORT_COLOR);
            Point center = tileCenter(client, transport.getSource());
            if (center == null) {
                continue;
            }

            Point linkCenter = tileCenter(client, transport.getDestination());
            if (linkCenter == null) {
                continue;
            }

            graphics2D.drawLine(center.getX(), center.getY(), linkCenter.getX(), linkCenter.getY());
        }
    }

    public void fillTile(Graphics2D graphics, WorldPoint point, Color color) {
        if (point.getPlane() != client.getPlane()) {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null) {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) {
            return;
        }

        graphics.setColor(color);
        graphics.fill(poly);
    }

    public Point tileCenter(net.runelite.api.Client client, WorldPoint b) {
        if (b.getPlane() != client.getPlane()) {
            return null;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, b);
        if (lp == null) {
            return null;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) {
            return null;
        }

        int cx = poly.getBounds().x + poly.getBounds().width / 2;
        int cy = poly.getBounds().y + poly.getBounds().height / 2;
        return new Point(cx, cy);
    }
}
