package net.solace.api.plugins.config;

import javax.swing.JPanel;
import net.solace.api.plugins.config.PluginConfigurationDescriptor;
import net.solace.api.plugins.config.SearchablePlugin;

public abstract class PluginListItem
extends JPanel
implements SearchablePlugin {
    public abstract PluginConfigurationDescriptor getPluginConfig();

    public abstract void setPluginEnabled(boolean var1);
}

