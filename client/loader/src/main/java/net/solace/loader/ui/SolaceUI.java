package net.solace.loader.ui;

import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.solace.api.plugins.config.PluginListPanel;
import net.solace.ui.plugins.TopLevelConfigPanel;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;

@Singleton
public class SolaceUI {
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private PluginListPanel pluginListPanel;

    @Inject
    private TopLevelConfigPanel topLevelConfigPanel;

    NavigationButton navButton;

    public void init() {
        var icon = ImageUtil.loadImageResource(getClass(), "solace.png");
        navButton = NavigationButton.builder()
                .tooltip("Solace")
                .icon(icon)
                .priority(-1)
                .panel(topLevelConfigPanel)
                .build();

        pluginListPanel.rebuildPluginList();
        clientToolbar.addNavigation(navButton);
        SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));
    }

    public void clear() {
        clientToolbar.removeNavigation(navButton);
    }
}
