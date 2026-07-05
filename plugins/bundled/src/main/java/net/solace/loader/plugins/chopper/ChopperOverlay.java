package net.solace.loader.plugins.chopper;

import com.google.inject.Singleton;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.solace.api.domain.tiles.ITile;
import net.solace.sdk.utils.DrawUtils;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

@Singleton
class ChopperOverlay extends Overlay {
    @Setter
    private List<ITile> fireArea;

    @Setter
    private WorldPoint startingLocation;

    @Inject
    private Client client;

    @Inject
    protected ChopperOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (fireArea != null) {
            for (var tile : fireArea) {
                if (tile.isEmpty()) {
                    DrawUtils.outline(client, tile.getWorldLocation(), graphics2D, Color.GREEN, "Empty tile");
                }
            }
        }

        if (startingLocation != null) {
            DrawUtils.outline(client, startingLocation, graphics2D, Color.ORANGE, "Anchor");
        }

        return null;
    }
}