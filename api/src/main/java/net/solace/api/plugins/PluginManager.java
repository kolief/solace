package net.solace.api.plugins;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.exception.PluginInstantiationException;

public interface PluginManager {
    public Config getPluginConfigProxy(Plugin var1);

    public List<Config> getPluginConfigProxies(Collection<Plugin> var1);

    public void loadDefaultPluginConfiguration(Collection<Plugin> var1);

    public void startCorePlugins();

    public void startPlugins();

    public void startPlugins(Collection<Plugin> var1);

    public void stopPlugins();

    public List<Plugin> loadPlugins(List<Class<?>> var1, BiConsumer<Integer, Integer> var2) throws PluginInstantiationException;

    public boolean startPlugin(Plugin var1) throws PluginInstantiationException;

    public boolean stopPlugin(Plugin var1) throws PluginInstantiationException;

    public void setPluginEnabled(Plugin var1, boolean var2);

    public boolean isPluginEnabled(Plugin var1);

    public void add(Plugin var1);

    public void remove(Plugin var1);

    public Collection<Plugin> getPlugins();

    public void loadCorePlugins() throws IOException, PluginInstantiationException;

    public List<Plugin> conflictsForPlugin(Plugin var1);
}

