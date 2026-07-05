package net.solace.ui.sdn;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.solace.sdn.SdnPluginManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

@Slf4j
public class SdnPluginManagerPanel extends PluginPanel {

    @Inject
    public SdnPluginManagerPanel(SdnPluginManager sdnPluginManager, EventBus eventBus) {
        super(false);
        removeAll();

        setLayout(new BorderLayout(0, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        var pluginsPanel = new PluginsPanel(sdnPluginManager, eventBus);

        add(pluginsPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    static JScrollPane wrapContainer(final JPanel container) {
        final JPanel wrapped = new JPanel(new BorderLayout());
        wrapped.add(container, BorderLayout.NORTH);

        final JScrollPane scroller = new JScrollPane(wrapped);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        return scroller;
    }
}
