package net.solace.sdk.script.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.RenderableEntity;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.solace.api.ui.TableAlignment;
import net.solace.api.ui.TableComponent;
import net.solace.sdk.commons.StopWatch;
import net.solace.sdk.game.Skills;
import net.solace.sdk.script.paint.ExperienceTracker;
import net.solace.sdk.script.paint.Statistic;

public class DefaultPaint
extends Overlay {
    private final List<Statistic> paintStatistics = new ArrayList<Statistic>();
    private final PanelComponent panel = new PanelComponent();
    private final List<RenderableEntity> overlays = new ArrayList<RenderableEntity>();
    private boolean enabled = true;
    private final RenderableEntity trackerRenderer = graphics -> {
        if (!this.enabled) {
            this.panel.getChildren().clear();
            return null;
        }
        TableComponent table = new TableComponent();
        table.setColumnAlignments(new TableAlignment[]{TableAlignment.LEFT, TableAlignment.LEFT});
        this.panel.getChildren().clear();
        this.panel.setPreferredLocation(new Point(6, 6));
        this.panel.setPreferredSize(new Dimension(200, 0));
        for (Statistic statistic : this.paintStatistics) {
            String key = statistic.getKey();
            if (statistic.isHeader()) {
                this.panel.getChildren().add(TitleComponent.builder().text(key).color(Color.WHITE).build());
                continue;
            }
            if (statistic.isEmpty()) {
                table.addRow(new String[]{""});
                continue;
            }
            String text = key + ":";
            table.addRow(new String[]{text, statistic.toString()});
        }
        this.panel.getChildren().add(table);
        return this.panel.render(graphics);
    };

    public DefaultPaint() {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.setPriority(1.0f);
        this.overlays.add(this.trackerRenderer);
    }

    public Dimension render(Graphics2D graphics) {
        this.overlays.forEach(x -> x.render(graphics));
        return null;
    }

    public void submit(String key, Supplier<String> supplier) {
        this.paintStatistics.add(new Statistic(key, supplier));
    }

    public void submit(RenderableEntity renderableEntity) {
        this.overlays.add(renderableEntity);
    }

    public void addSeparator() {
        this.paintStatistics.add(Statistic.empty());
    }

    public void clear() {
        this.paintStatistics.clear();
        this.overlays.clear();
    }

    public void remove(RenderableEntity renderableEntity) {
        this.overlays.remove(renderableEntity);
    }

    public void trackSkill(Skill skill, boolean trackLevels) {
        ExperienceTracker tracker = new ExperienceTracker(skill, Skills.getExperience(skill), Skills.getLevel(skill));
        StopWatch timer = StopWatch.start();
        String xpKey = skill.getName() + " XP";
        if (!this.isTracked(xpKey)) {
            this.paintStatistics.add(new Statistic(xpKey, timer, tracker::getExperienceGained));
        }
        String lvlKey = skill.getName() + " LVLs";
        if (trackLevels && !this.isTracked(lvlKey)) {
            this.paintStatistics.add(new Statistic(lvlKey, timer, tracker::getLevelsGained));
        }
    }

    private boolean isTracked(String key) {
        for (Statistic statistic : this.paintStatistics) {
            if (!Objects.equals(statistic.getKey(), key)) continue;
            return true;
        }
        return false;
    }

    public void setHeader(String text) {
        this.paintStatistics.add(new Statistic(text, true, null));
    }

    public PanelComponent getPanel() {
        return this.panel;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RenderableEntity getTrackerRenderer() {
        return this.trackerRenderer;
    }
}

