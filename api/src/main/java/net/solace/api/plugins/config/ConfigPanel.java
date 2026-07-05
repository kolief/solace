package net.solace.api.plugins.config;

import net.runelite.client.ui.PluginPanel;
import net.solace.api.plugins.config.PluginConfigurationDescriptor;

public abstract class ConfigPanel
extends PluginPanel {
    public ConfigPanel() {
        super(false);
    }

    public abstract void init(PluginConfigurationDescriptor var1, boolean var2);

    public void init(PluginConfigurationDescriptor pluginConfigurationDescriptor) {
        this.init(pluginConfigurationDescriptor, true);
    }
}

