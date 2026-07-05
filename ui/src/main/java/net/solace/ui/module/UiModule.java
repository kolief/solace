package net.solace.ui.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.http.api.RuneLiteAPI;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.ConfigPanel;
import net.solace.api.plugins.config.PluginListPanel;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.sdn.SdnPluginManager;
import net.solace.ui.plugins.ConfigPanelImpl;
import net.solace.ui.plugins.PluginListPanelImpl;
import net.solace.ui.plugins.ProfileManager;
import net.solace.ui.plugins.ProfilePanel;
import net.solace.ui.plugins.TopLevelConfigPanel;
import net.solace.ui.plugins.items.Definitions;
import net.solace.ui.plugins.items.ItemSelector;
import net.solace.ui.sdn.SdnPluginManagerPanel;

import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

public class UiModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    PluginListPanel providePluginListPanel(
            PluginManager pluginManager,
            EventBus eventBus,
            ConfigManager configManager,
            SdnPluginManager sdnPluginManager,
            Provider<ConfigPanel> configPanelProvider
    ) {
        return new PluginListPanelImpl(pluginManager, eventBus, configManager, sdnPluginManager, configPanelProvider);
    }

    @Provides
    @Singleton
    SdnPluginManagerPanel provideSdnPanel(
            EventBus eventBus,
            SdnPluginManager sdnPluginManager
    ) {
        return new SdnPluginManagerPanel(sdnPluginManager, eventBus);
    }


    @Provides
    @Singleton
    TopLevelConfigPanel provideTopLevelConfigPanel(
            EventBus eventBus,
            PluginListPanel pluginListPanel,
            SdnPluginManagerPanel sdnPanel,
            Provider<ConfigPanel> configPanelProvider,
            SolaceConfig solaceConfig,
            ConfigManager configManager,
            ProfilePanel profilePanel
    ) {
        return new TopLevelConfigPanel(eventBus, pluginListPanel, sdnPanel, configPanelProvider, solaceConfig,
                configManager, profilePanel);
    }

    @Provides
    ConfigPanel provideConfigPanel(
            PluginListPanel pluginList,
            ConfigManager configManager,
            PluginManager pluginManager,
            ColorPickerManager colorPickerManager,
            SdnPluginManager sdnPluginManager,
            EventBus eventBus,
            ItemManager itemManager,
            Provider<ItemSelector> itemSelectorProvider,
            IClientThread clientThread,
            ILoadoutFactory loadoutFactory
    ) {
        return new ConfigPanelImpl(
                pluginList,
                configManager,
                pluginManager,
                colorPickerManager,
                sdnPluginManager,
                eventBus,
                itemManager,
                itemSelectorProvider,
                clientThread,
                loadoutFactory
        );
    }

    @Provides
    @Singleton
    Definitions provideDefinitions() {
        return new Definitions(RuneLiteAPI.GSON);
    }

    @Provides
    @Singleton
    ItemSelector provideItemSelector(ItemManager itemManager, Client client, Definitions definitions, ConfigManager configManager, IClientThread clientThread) {
        return new ItemSelector(itemManager, client, definitions, configManager, clientThread);
    }

    @Provides
    @Singleton
    ProfilePanel provideProfilePanel(
            ConfigManager configManager,
            ProfileManager profileManager,
            ScheduledExecutorService scheduledExecutorService
    ) {
        return new ProfilePanel(configManager, profileManager, scheduledExecutorService);
    }
}
