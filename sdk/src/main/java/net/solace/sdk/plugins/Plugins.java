package net.solace.sdk.plugins;

import net.solace.api.Static;
import net.solace.api.plugins.IPlugins;
import net.solace.api.plugins.Plugin;

public class Plugins {
    private static final IPlugins PLUGINS = Static.getPlugins();

    public static boolean isEnabled(Plugin plugin) {
        return PLUGINS.isEnabled(plugin);
    }

    public static boolean stopPlugin(Plugin plugin) {
        return PLUGINS.stopPlugin(plugin);
    }

    public static boolean startPlugin(Plugin plugin) {
        return PLUGINS.startPlugin(plugin);
    }
}

