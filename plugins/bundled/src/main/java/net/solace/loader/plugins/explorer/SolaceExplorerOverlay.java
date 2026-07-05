package net.solace.loader.plugins.explorer;

import com.google.inject.Singleton;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.solace.api.Static;
import net.solace.loader.plugins.explorer.exclude.SolaceExplorerPlugin;
import net.solace.sdk.movement.Walker;
import net.solace.sdk.utils.DrawUtils;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;

@Singleton
public class SolaceExplorerOverlay extends OverlayPanel {

    @Inject
    private SolaceExplorerPlugin plugin;

    @Inject
    private SolaceExplorerConfig config;

    @Inject
    protected SolaceExplorerOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (!config.showOverlay() || plugin.getDestination() == null) {
            return null;
        }

        var path = Walker.getCurrentPath();

        if (path == null) {
            return null;
        }

        for (var tile : path) {
            DrawUtils.drawOnMap(graphics2D, tile, Color.CYAN);

            var lp = LocalPoint.fromWorld(Static.getWrappedClient(), tile);

            if (lp == null) {
                continue;
            }

            Polygon poly = Perspective.getCanvasTilePoly(Static.getWrappedClient(), lp);

            if (poly != null) {
                Stroke stroke = new BasicStroke((float) 1);
                OverlayUtil.renderPolygon(graphics2D, poly, Color.CYAN, new Color(0, 255, 255, 90), stroke);
            }
        }

        return null;
    }
}