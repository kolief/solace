package net.solace.loader.thirdparty;

import lombok.RequiredArgsConstructor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class IncompatiblePluginChecker {
    private static final List<String> INCOMPATIBLE_PLUGINS = List.of("Better Teleport Menu");

    private final PluginManager pluginManager;

    public void checkAndDisable() {
        SwingUtilities.invokeLater(() -> {
            var disabledPlugins = new ArrayList<String>();
            for (var pluginName : INCOMPATIBLE_PLUGINS) {
                var plugin = pluginManager.getPlugins().stream()
                        .filter(p -> p.getName().equals(pluginName))
                        .findFirst()
                        .orElse(null);
                if (plugin == null) {
                    continue;
                }

                if (pluginManager.isPluginActive(plugin)) {
                    disabledPlugins.add(pluginName);

                    try {
                        pluginManager.setPluginEnabled(plugin, false);
                        pluginManager.stopPlugin(plugin);
                    } catch (PluginInstantiationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            if (!disabledPlugins.isEmpty()) {
                var message = "The following incompatible plugins were automatically disabled: " + String.join(", ", disabledPlugins);
                JOptionPane.showMessageDialog(null, message, "Incompatible Plugins", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
