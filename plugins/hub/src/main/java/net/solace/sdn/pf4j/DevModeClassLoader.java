package net.solace.sdn.pf4j;

import net.solace.api.plugins.DevelopmentClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

public class DevModeClassLoader extends SdnClassLoader implements DevelopmentClassLoader {
    public DevModeClassLoader(PluginManager pluginManager, PluginDescriptor pluginDescriptor, ClassLoader parent,
                              net.runelite.client.plugins.PluginManager runelitePluginManager) {
        super(pluginManager, pluginDescriptor, parent, runelitePluginManager);
    }

    @Override
    public boolean isDevelopment() {
        return true;
    }
}
