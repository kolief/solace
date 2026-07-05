package net.solace.api.plugins.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import net.solace.api.plugins.config.ToggleButton;
import net.solace.api.ui.ColorScheme;

public class PluginToggleButton
extends JToggleButton {
    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;
    private String conflictString = "";

    public PluginToggleButton() {
        super(OFF_SWITCHER);
        this.setSelectedIcon(ON_SWITCHER);
        SwingUtil.removeButtonDecorations((AbstractButton)this);
        this.setPreferredSize(new Dimension(25, 0));
        this.addItemListener(l -> this.updateTooltip());
        this.updateTooltip();
    }

    private void updateTooltip() {
        this.setToolTipText((String)(this.isSelected() ? "Disable plugin" : "<html>Enable plugin" + this.conflictString));
    }

    public void setConflicts(List<String> conflicts) {
        if (conflicts != null && !conflicts.isEmpty()) {
            StringBuilder sb = new StringBuilder("<br>Plugin conflicts: ");
            for (int i = 0; i < conflicts.size() - 2; ++i) {
                sb.append(conflicts.get(i));
                sb.append(", ");
            }
            if (conflicts.size() >= 2) {
                sb.append(conflicts.get(conflicts.size() - 2));
                sb.append(" and ");
            }
            sb.append(conflicts.get(conflicts.size() - 1));
            this.conflictString = sb.toString();
        } else {
            this.conflictString = "";
        }
        this.updateTooltip();
    }

    static {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(ToggleButton.class, (String)"switcher_on.png");
        ON_SWITCHER = new ImageIcon(ImageUtil.recolorImage((Image)onSwitcher, (Color)ColorScheme.BRAND_CRIMSON));
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage((BufferedImage)ImageUtil.luminanceScale((Image)ImageUtil.grayscaleImage((BufferedImage)onSwitcher), (float)0.61f), (boolean)true, (boolean)false));
    }
}

