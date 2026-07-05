package net.solace.sdk.script.paint;

import lombok.Getter;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.RenderableEntity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class Paint extends Overlay {
    private final List<RenderableEntity> overlays = new ArrayList<>();
    @Getter
    private final DefaultPaint tracker = new DefaultPaint();
    private boolean enabled = false;
    @Inject
    private OverlayManager overlayManager;

    @Inject
    public Paint() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.LOW);
    }

    public void init() {
        overlayManager.add(this);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (!enabled) {
            return null;
        }

        for (RenderableEntity renderableEntity : overlays) {
            renderableEntity.render(g);
        }

        return null;
    }

    public void submit(RenderableEntity p) {
        overlays.add(p);
    }

    public void clear() {
        overlays.clear();
        tracker.clear();
        overlayManager.remove(this);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            submit(tracker.getTrackerRenderer());
        }
    }
}
