package net.solace.loader.plugins.solacedevtools;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.solacedevtools.logger.LoggerWindow;

@PluginDescriptor(
        name = "Solace Dev Tools",
        description = "A collection of tools for developers",
        tags = {"dev", "tools"}
)
@Slf4j
public class SolaceDevToolsPlugin extends Plugin {
    @Inject
    private SolaceDevToolsConfig config;

    @Inject
    private SolaceDevToolsOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private EventBus eventBus;

    private LoggerWindow logger;

    @Override
    public void startUp() {
        overlayManager.add(overlay);
        eventBus.register(overlay);

        if (logger == null) {
            logger = new LoggerWindow();
        }

        logger.setVisible(true);
    }

    @Override
    public void shutDown() {
        overlayManager.remove(overlay);
        eventBus.unregister(overlay);

        logger.dispose();
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if (config.debugMenuActions()) {
            var menuEntry = e.getMenuEntry();
            String action = "O=" + menuEntry.getOption()
                            + " | T=" + menuEntry.getTarget()
                            + " | ID=" + menuEntry.getIdentifier()
                            + " | OP=" + menuEntry.getType()
                            + " | P0=" + menuEntry.getParam0()
                            + " | P1=" + menuEntry.getParam1()
                            + " | ITEM=" + menuEntry.getItemId()
                            + " | WV=" + menuEntry.getWorldViewId();
            log.info("[Menu Action] {}", action);
        }
    }

    @Provides
    public SolaceDevToolsConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceDevToolsConfig.class);
    }
}
