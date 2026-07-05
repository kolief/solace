package net.solace.api.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.Text;

public final class ComboBoxListRenderer<T>
extends JLabel
implements ListCellRenderer<T> {
    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T o, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            this.setBackground(ColorScheme.DARK_GRAY_COLOR);
            this.setForeground(Color.WHITE);
        } else {
            this.setBackground(list.getBackground());
            this.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        }
        this.setBorder(new EmptyBorder(5, 5, 5, 0));
        String text = o instanceof Enum ? Text.titleCase((Enum)((Enum)o)) : o.toString();
        this.setText(text);
        return this;
    }
}

