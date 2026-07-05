package net.solace.loader.plugins.shops;

import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.solace.sdk.utils.DrawUtils;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

@Singleton
class ShopperOverlay extends Overlay {

    @Inject
    private SolaceShopsPlugin plugin;

    @Inject
    private Client client;

    @Inject
    protected ShopperOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        var shop = plugin.getSelectedShop();

        if (shop != null) {
            DrawUtils.outline(client, shop.getWorldPoint(), graphics2D, Color.CYAN, "Shop");

            var returnTile = shop.getReturnTile();
            if (returnTile != null) {
                DrawUtils.outline(client, returnTile, graphics2D, Color.GREEN, "Return");
            }
            return null;
        }

        return null;
    }
}