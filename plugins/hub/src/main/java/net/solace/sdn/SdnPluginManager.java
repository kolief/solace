package net.solace.sdn;

import net.solace.sdn.update.UpdateManager;
import org.pf4j.PluginManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SdnPluginManager {
    void startExternalPluginManager();

    void update();

    void startPlugins();

    boolean isDevMode();

    Map<String, Map<String, String>> getPluginsInfoMap();

    boolean uninstall(String pluginId);

    boolean reloadStart(String pluginId);

    PluginManager getPluginManager();

    UpdateManager getUpdateManager();

    List<String> getDisabledPluginIds();

    Set<String> getDependencies();

    boolean install(String id) throws IOException;
}
