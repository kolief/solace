package net.solace.loader.plugins.solacedevtools;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.solace.api.events.AutomatedClick;
import net.solace.api.movement.pathfinder.LocalCollisionMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

@Singleton
public class SolaceDevToolsOverlay extends Overlay {
    private final SolaceDevToolsConfig config;
    private final EntityRenderer entityRenderer;
    private final DrawService drawService;
    private final Client client;

    private int lastX;
    private int lastY;

    @Inject
    private SolaceDevToolsOverlay(
            SolaceDevToolsConfig config,
            EntityRenderer entityRenderer,
            DrawService drawService, Client client
    ) {
        this.config = config;
        this.entityRenderer = entityRenderer;
        this.drawService = drawService;
        this.client = client;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(1.0f);
    }

    @Override
    public Dimension render(Graphics2D g) {
        entityRenderer.setActions(config.actions());
        entityRenderer.setNames(config.names());
        entityRenderer.setAnimations(config.animations());
        entityRenderer.setGraphics(config.graphics());
        entityRenderer.setActions(config.actions());
        entityRenderer.setIds(config.ids());
        entityRenderer.setIndexes(config.indexes());
        entityRenderer.setQuantities(config.quantities());
        entityRenderer.setWorldLocations(config.worldLocations());
        entityRenderer.setTrueWorldLocations(config.trueWorldLocations());

        entityRenderer.setTileLocation(config.tileLocation());
        entityRenderer.setGameObjects(config.gameObjects());
        entityRenderer.setDecorativeObjects(config.decorObjects());
        entityRenderer.setGroundObjects(config.groundObjects());
        entityRenderer.setInventory(config.inventory());
        entityRenderer.setPlayers(config.players());
        entityRenderer.setNpcs(config.npcs());
        entityRenderer.setGroundObjects(config.groundObjects());
        entityRenderer.setTileItems(config.tileItems());
        entityRenderer.setWallObjects(config.wallObjects());
        entityRenderer.setPath(config.path());
        entityRenderer.setRadius(config.radius());

        if (config.collisionOverlay()) {
            drawService.drawCollisions(g);
        }

        if (config.collisionLocalOverlay()) {
            drawService.drawCollisions(g, new LocalCollisionMap(true));
        }

        if (config.pathOverlay()) {
            drawService.drawLastPath(g);
        }

        if (config.transportsOverlay()) {
            drawService.drawTransports(g);
        }

        entityRenderer.render(g);

        if (config.drawMouse()) {
            g.setFont(new Font("Tahoma", Font.BOLD, 18));

            var mousePos = client.getMouseCanvasPosition();
            if (mousePos != null) {
                var point = new Point(mousePos.getX() - (g.getFont().getSize() / 3),
                        mousePos.getY() + (g.getFont().getSize() / 3));
                OverlayUtil.renderTextLocation(g, point, "X", Color.WHITE);
            }

            OverlayUtil.renderTextLocation(g,
                    new Point(lastX - (g.getFont().getSize() / 3),
                            lastY + (g.getFont().getSize() / 3)), "X", Color.GREEN);
        }

        return null;
    }

    @Subscribe
    private void onAutomatedClick(AutomatedClick e) {
        lastX = e.getX();
        lastY = e.getY();
    }
}
