package net.solace.ui.plugins.items;

import lombok.RequiredArgsConstructor;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.solace.api.plugins.config.ItemConfig;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Component;

@RequiredArgsConstructor
public class ItemCellRenderer extends DefaultListCellRenderer {
    private final ItemManager itemManager;

    private final JLabel label = new JLabel();

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        var comp = (ItemConfig) value;
        label.setText(comp.getName() + " (" + comp.getId() + ")");
        AsyncBufferedImage image = itemManager.getImage(comp.getId());
        image.addTo(label);

        list.repaint();

        return label;
    }
}
