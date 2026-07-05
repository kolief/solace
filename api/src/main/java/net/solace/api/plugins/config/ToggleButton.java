package net.solace.api.plugins.config;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.Text;
import net.solace.api.ui.ColorScheme;

public class ToggleButton
extends JCheckBox {
    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;
    private static final ImageIcon DISABLED_SWITCHER;
    private final Object object;

    public ToggleButton() {
        super(OFF_SWITCHER);
        this.object = null;
        this.setSelectedIcon(ON_SWITCHER);
        this.setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations((AbstractButton)this);
    }

    public ToggleButton(String text) {
        super(text, OFF_SWITCHER, false);
        this.object = null;
        this.setSelectedIcon(ON_SWITCHER);
        this.setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations((AbstractButton)this);
    }

    public ToggleButton(Object object) {
        super(Text.titleCase((Enum)((Enum)object)), OFF_SWITCHER, false);
        this.object = object;
        this.setSelectedIcon(ON_SWITCHER);
        this.setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations((AbstractButton)this);
    }

    public Object getObject() {
        return this.object;
    }

    static {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(ToggleButton.class, (String)"switcher_on.png");
        ON_SWITCHER = new ImageIcon(ImageUtil.recolorImage((Image)onSwitcher, (Color)ColorScheme.BRAND_CRIMSON));
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage((BufferedImage)ImageUtil.luminanceScale((Image)ImageUtil.grayscaleImage((BufferedImage)onSwitcher), (float)0.61f), (boolean)true, (boolean)false));
        DISABLED_SWITCHER = new ImageIcon(ImageUtil.flipImage((BufferedImage)ImageUtil.luminanceScale((Image)ImageUtil.grayscaleImage((BufferedImage)onSwitcher), (float)0.4f), (boolean)true, (boolean)false));
    }
}

