package net.solace.loader.plugins.loadoutmanager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.ImageUtil;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.events.ConfigChanged;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.PluginManager;
import net.solace.sdn.SdnPluginManager;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.PluginListPanel;
import net.solace.loader.plugins.loadoutmanager.panel.LoadoutConfigPanel;
import net.solace.ui.plugins.items.ItemSelector;

import static net.solace.loader.plugins.loadoutmanager.SolaceLoadoutManagerConfig.CONFIG_GROUP;

@PluginDescriptor(
        name = "Solace Loadout Manager",
        description = "A plugin that allows you to manage loadouts.",
        enabledByDefault = true
)
public class SolaceLoadoutManagerPlugin extends LoopedPlugin {
    @Inject
    private LoadoutConfigPanel loadoutConfigPanel;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ConfigManager configManager;

    private NavigationButton navButton;

    @Override
    public void startUp() {
        setToggleHidden(true);
        setCurrentFetchingLoadout(null);
        var icon = ImageUtil.loadImageResource(SolaceLoadoutManagerPlugin.class, "slaughter.png");
        navButton = NavigationButton.builder()
                .tooltip("Solace Loadout Manager")
                .icon(icon)
                .priority(1)
                .panel(loadoutConfigPanel)
                .build();

        clientToolbar.addNavigation(navButton);

        loadoutConfigPanel.rebuild();
    }

    @Override
    public void shutDown() {
        clientToolbar.removeNavigation(navButton);
    }

    public Loadout getCurrentFetchingLoadout() {
        return configManager.getConfiguration(CONFIG_GROUP, "loadoutMgr_current", Loadout.class);
    }

    public void setCurrentFetchingLoadout(Loadout loadout) {
        if (loadout == null) {
            configManager.unsetConfiguration(CONFIG_GROUP, "loadoutMgr_current");
        } else {
            configManager.setConfiguration(CONFIG_GROUP, "loadoutMgr_current", loadout);
        }
    }

    @Override
    public int loop() {
        var currentFetchingLoadout = getCurrentFetchingLoadout();
        if (currentFetchingLoadout == null) {
            return -1;
        }

        if (currentFetchingLoadout.isLoadoutCompleted()) {
            setCurrentFetchingLoadout(null);
            return -1;
        }

        currentFetchingLoadout.fetchFromBank();
        return -2;
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged e) {
        if (e.getGroup().equals("solaceloadoutmanager")) {
            loadoutConfigPanel.rebuild();
        }
    }

    @Provides
    SolaceLoadoutManagerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceLoadoutManagerConfig.class);
    }

    @Provides
    LoadoutConfigPanel provideLoadoutConfigPanel(PluginListPanel pluginList, ConfigManager configManager,
                                                 PluginManager pluginManager, ColorPickerManager colorPickerManager,
                                                 SdnPluginManager sdnPluginManager, EventBus eventBus,
                                                 ItemManager itemManager, Provider<ItemSelector> itemSelectorProvider,
                                                 IClientThread clientThread, SolaceLoadoutManagerConfig config,
                                                 ILoadoutFactory loadoutFactory) {
        return new LoadoutConfigPanel(pluginList, configManager, pluginManager, colorPickerManager, sdnPluginManager,
                eventBus, itemManager, itemSelectorProvider, clientThread, this, config, loadoutFactory
        );
    }
}

