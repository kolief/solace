package net.solace.api.plugins;

import org.pf4j.update.PluginInfo;

import java.util.List;

public interface UpdateManager {
    List<PluginInfo> getAvailablePlugins();

    List<PluginInfo> getPlugins();
}
