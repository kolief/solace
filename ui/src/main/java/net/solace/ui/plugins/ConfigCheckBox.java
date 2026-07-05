package net.solace.ui.plugins;

import lombok.Getter;
import net.runelite.client.util.Text;

import javax.swing.JCheckBox;

@Getter
public class ConfigCheckBox extends JCheckBox {
    private final Object object;

    public ConfigCheckBox(Object object) {
        super(Text.titleCase((Enum<?>) object));
        this.object = object;
    }
}
