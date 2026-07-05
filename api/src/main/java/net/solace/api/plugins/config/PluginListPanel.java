package net.solace.api.plugins.config;

import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.config.PluginConfigurationDescriptor;
import net.solace.api.plugins.exception.PluginInstantiationException;

public abstract class PluginListPanel
extends PluginPanel {
    public PluginListPanel() {
        super(false);
    }

    public abstract void openConfigurationPanel(PluginConfigurationDescriptor var1);

    public abstract void openConfigurationPanel(String var1);

    public abstract void openConfigurationPanel(Plugin var1);

    public abstract void savePinnedPlugins();

    public abstract void refresh();

    public abstract void rebuildPluginList();

    public abstract void startPlugin(Plugin var1) throws PluginInstantiationException;

    public abstract void stopPlugin(Plugin var1) throws PluginInstantiationException;

    public abstract MultiplexingPluginPanel getMuxer();
}

