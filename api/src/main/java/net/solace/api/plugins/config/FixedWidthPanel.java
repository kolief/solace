package net.solace.api.plugins.config;

import java.awt.Dimension;
import javax.swing.JPanel;

public class FixedWidthPanel
extends JPanel {
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(225, super.getPreferredSize().height);
    }
}

