package net.solace.loader.local;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.solace.api.events.ExternalPluginsChanged;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginManager;
import net.solace.loader.events.SdnLoaded;
import net.solace.sdn.SdnPluginManager;

import javax.swing.SwingUtilities;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class LocalBootstrap {
    private final SdnPluginManager sdnPluginManager;
    private final PluginManager pluginManager;
    private final EventBus eventBus;
    private final String script;

    public void loadPlugins() {
        sdnPluginManager.startExternalPluginManager();
        sdnPluginManager.startPlugins();

        pluginManager.loadDefaultPluginConfiguration(null);
        pluginManager.startPlugins();

        if (script != null) {
            pluginManager.getPlugins().stream()
                    .filter(p -> Objects.equals(p.getName(), script))
                    .findFirst()
                    .ifPresentOrElse(this::startPlugin, () -> log.warn("Plugin '{}' not found", script));
        }

        eventBus.post(new SdnLoaded());
        eventBus.post(new ExternalPluginsChanged());
    }

    private void startPlugin(Plugin plugin) {
        SwingUtilities.invokeLater(() -> {
            try {
                pluginManager.setPluginEnabled(plugin, true);
                pluginManager.startPlugin(plugin);
            } catch (Exception e) {
                log.error("Error starting plugin {}", plugin.getName(), e);
            }
        });
    }
}
