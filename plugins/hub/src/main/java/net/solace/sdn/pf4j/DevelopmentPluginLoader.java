package net.solace.sdn.pf4j;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.BasePluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginClasspath;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

import java.nio.file.Path;

@Slf4j
class DevelopmentPluginLoader extends BasePluginLoader {
    private final net.runelite.client.plugins.PluginManager runeLitePluginManager;

    DevelopmentPluginLoader(PluginManager pluginManager, PluginClasspath pluginClasspath,
                            net.runelite.client.plugins.PluginManager runeLitePluginManager) {
        super(pluginManager, pluginClasspath);
        this.runeLitePluginManager = runeLitePluginManager;
    }

    @Override
    protected PluginClassLoader createPluginClassLoader(Path pluginPath, PluginDescriptor pluginDescriptor) {
        return new DevModeClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader(),
                runeLitePluginManager);
    }
}
