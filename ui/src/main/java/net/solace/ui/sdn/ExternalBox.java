package net.solace.ui.sdn;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import org.pf4j.update.PluginInfo;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class ExternalBox extends JPanel {
    private static final Font normalFont = FontManager.getRunescapeFont();
    private static final Font smallFont = FontManager.getRunescapeSmallFont();

    final PluginInfo pluginInfo;
    JLabel install = new JLabel();
    JMultilineLabel description = new JMultilineLabel();

    ExternalBox(PluginInfo pluginInfo) {
        this.pluginInfo = pluginInfo;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        titleWrapper.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
                BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)
        ));

        JLabel title = new JLabel();
        title.setText(pluginInfo.name);
        title.setFont(normalFont);
        title.setBorder(null);
        title.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        title.setPreferredSize(new Dimension(0, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 8, 0, 0));

        JPanel titleActions = new JPanel(new BorderLayout(3, 0));
        titleActions.setBorder(new EmptyBorder(0, 0, 0, 8));
        titleActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        titleActions.add(install, BorderLayout.EAST);

        titleWrapper.add(title, BorderLayout.CENTER);
        titleWrapper.add(titleActions, BorderLayout.EAST);

        description.setText("Version: " + pluginInfo.releases.get(pluginInfo.releases.size() - 1).version
                + (pluginInfo.description != null ? "\n" + pluginInfo.description : ""));
        description.setFont(smallFont);
        description.setDisabledTextColor(Color.WHITE);
        description.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        add(titleWrapper, BorderLayout.NORTH);
        add(description, BorderLayout.CENTER);
    }

    public static class JMultilineLabel extends JTextArea {
        private static final long serialVersionUID = 1L;

        public JMultilineLabel() {
            super();
            setEditable(false);
            setCursor(null);
            setOpaque(false);
            setFocusable(false);
            setWrapStyleWord(true);
            setLineWrap(true);
            setBorder(new EmptyBorder(0, 8, 0, 8));
            setAlignmentY(JLabel.CENTER_ALIGNMENT);

            DefaultCaret caret = (DefaultCaret) getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
    }
}
