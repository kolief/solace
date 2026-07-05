package net.solace.loader.plugins.fighter;

import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.solace.sdk.utils.DrawUtils;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

@Singleton
class FighterOverlay extends Overlay {
    @Inject
    private Client client;

    @Inject
    private SolaceFighterConfig config;

    @Inject
    private SolaceFighterPlugin plugin;

    @Inject
    protected FighterOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        WorldPoint center = plugin.getCenter();
        if (center == null) {
            return null;
        }

        var cannon = plugin.getCannon();
        if (config.refillCannon() && cannon != null) {
            DrawUtils.outline(client, cannon.getWorldLocation(), graphics2D, Color.YELLOW, "Cannon");
        }

        if (config.drawCenter()) {
            DrawUtils.outline(client, center, graphics2D, Color.ORANGE, String.format("Center: %s", config.centerTile()));
        }

        if (config.drawRadius()) {
            LocalPoint localPoint = LocalPoint.fromWorld(client, center);
            if (localPoint != null) {
                Polygon poly = Perspective.getCanvasTileAreaPoly(client, localPoint, config.attackRange() * 2);
                OverlayUtil.renderPolygon(graphics2D, poly, Color.WHITE, new Color(0, 0, 0, 0), new BasicStroke(2));
            }
        }

        if (config.drawSafespot()) {
            WorldPoint safespot = plugin.getSafespot();
            if (safespot != null) {
                DrawUtils.outline(client, safespot, graphics2D, Color.GREEN, "Safespot");
            }
        }

        return null;
    }
}