package net.solace.api.plugins.config;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.ui.FontManager;

public class HotkeyButton
extends JButton {
    private Keybind value;

    public HotkeyButton(Keybind value, final boolean modifierless) {
        this.setFocusTraversalKeysEnabled(false);
        this.setFont(FontManager.getDefaultFont().deriveFont(12.0f));
        this.setValue(value);
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1) {
                    HotkeyButton.this.setValue(Keybind.NOT_SET);
                }
            }
        });
        this.addKeyListener(new KeyAdapter(){

            @Override
            public void keyPressed(KeyEvent e) {
                if (modifierless) {
                    HotkeyButton.this.setValue((Keybind)new ModifierlessKeybind(e));
                } else {
                    HotkeyButton.this.setValue(new Keybind(e));
                }
            }
        });
    }

    public void setValue(Keybind value) {
        if (value == null) {
            value = Keybind.NOT_SET;
        }
        this.value = value;
        this.setText(value.toString());
    }

    public Keybind getValue() {
        return this.value;
    }
}

