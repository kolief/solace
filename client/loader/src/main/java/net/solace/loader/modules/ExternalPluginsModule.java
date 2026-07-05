package net.solace.loader.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.RequiredArgsConstructor;
import net.solace.loader.local.LocalBootstrap;
import net.runelite.client.eventbus.EventBus;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.sdn.SdnPluginManager;
import net.solace.sdn.SdnPluginManagerImpl;
import net.solace.sdn.pf4j.SdnPf4jPluginManager;
import net.solace.sdn.update.SdnRepository;
import net.solace.sdn.update.SdnUpdateManager;

import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor
public class ExternalPluginsModule extends AbstractModule {
    private final String script;

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    SdnRepository provideSdnRepository() {
        return new SdnRepository("Solace Plugins");
    }

    @Provides
    @Singleton
    SdnPf4jPluginManager provideSdnPf4jPluginManager(
            net.runelite.client.plugins.PluginManager runeLitePluginManager
    ) {
        return new SdnPf4jPluginManager(runeLitePluginManager);
    }

    @Provides
    @Singleton
    SdnUpdateManager provideSdnUpdateManager(SdnPf4jPluginManager sdnPf4jPluginManager, SdnRepository sdnRepository) {
        return new SdnUpdateManager(sdnPf4jPluginManager, sdnRepository);
    }

    @Provides
    @Singleton
    SdnPluginManager provideSdnPluginManager(
            SdnRepository sdnRepository,
            ConfigManager configManager,
            ScheduledExecutorService executorService,
            EventBus eventBus,
            PluginManager solacePluginManager,
            SdnPf4jPluginManager sdnPf4jPluginManager,
            SdnUpdateManager sdnUpdateManager
    ) {
        return new SdnPluginManagerImpl(
                sdnRepository,
                configManager,
                executorService,
                eventBus,
                solacePluginManager,
                sdnPf4jPluginManager,
                sdnUpdateManager
        );
    }

    @Provides
    @Singleton
    LocalBootstrap provideLocalBootstrap(
            SdnPluginManager sdnPluginManager,
            PluginManager pluginManager,
            EventBus eventBus
    ) {
        return new LocalBootstrap(sdnPluginManager, pluginManager, eventBus, script);
    }
}
