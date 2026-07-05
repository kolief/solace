package net.solace.impl.plugins;

import lombok.RequiredArgsConstructor;
import net.solace.api.plugins.IPlugins;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.exception.PluginInstantiationException;

import javax.swing.SwingUtilities;

@RequiredArgsConstructor
public class PluginsImpl implements IPlugins {
    private final PluginManager pluginManager;

    @Override
    public boolean isEnabled(Plugin plugin) {
        return pluginManager.isPluginEnabled(plugin);
    }

    @Override
    public boolean stopPlugin(Plugin plugin) {
        assert SwingUtilities.isEventDispatchThread();

        try {
            pluginManager.setPluginEnabled(plugin, false);
            return pluginManager.stopPlugin(plugin);
        } catch (PluginInstantiationException e) {
            return false;
        }
    }

    @Override
    public boolean startPlugin(Plugin plugin) {
        assert SwingUtilities.isEventDispatchThread();

        try {
            pluginManager.setPluginEnabled(plugin, true);
            return pluginManager.startPlugin(plugin);
        } catch (PluginInstantiationException e) {
            return false;
        }
    }
}
