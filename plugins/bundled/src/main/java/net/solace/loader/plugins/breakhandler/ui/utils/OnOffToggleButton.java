package net.solace.loader.plugins.breakhandler.ui.utils;

import net.solace.api.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import net.solace.loader.plugins.breakhandler.SolaceBreakHandlerPlugin;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class OnOffToggleButton extends JToggleButton {
    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;

    static {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(SolaceBreakHandlerPlugin.class, "switcher_on.png");
        ON_SWITCHER = new ImageIcon(ImageUtils.recolorImage(onSwitcher, ColorScheme.BRAND_CRIMSON));
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage(
                ImageUtil.luminanceScale(
                        ImageUtil.grayscaleImage(onSwitcher),
                        0.61f
                ),
                true,
                false
        ));
    }

    public OnOffToggleButton() {
        super(OFF_SWITCHER);
        setSelectedIcon(ON_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
        setPreferredSize(new Dimension(25, 0));
        SwingUtil.addModalTooltip(this, "Disable", "Enable");
    }
}