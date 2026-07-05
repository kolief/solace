package net.solace.loader.plugins.fighter;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;
import net.solace.api.ui.TableAlignment;
import net.solace.api.ui.TableComponent;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

@Slf4j
class SolaceFighterOverlay extends OverlayPanel {
    private final SolaceFighterPlugin plugin;
    private final SolaceFighterConfig config;

    @Inject
    private SolaceFighterOverlay(final SolaceFighterPlugin plugin, final SolaceFighterConfig config) {
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
        tableComponent.addRow("Current task", plugin.getCurrentTaskName());
        tableComponent.addRow("Should stop", "" + plugin.isShouldStop());
        if (!tableComponent.isEmpty()) {
            panelComponent.setPreferredSize(new Dimension(200, 200));
            panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Solace Fighter")
                    .color(ColorUtil.fromHex("#bc62f0"))
                    .build());
            panelComponent.getChildren().add(tableComponent);
        }
        return super.render(graphics);
    }
}