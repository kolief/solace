package net.solace.loader.modules;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import joptsimple.OptionParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.task.Scheduler;
import net.runelite.http.api.RuneLiteAPI;
import net.solace.api.commons.ITime;
import net.solace.api.domain.game.IClient;
import net.solace.api.entities.IPlayers;
import net.solace.api.game.IGame;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.impl.movement.WalkerManager;
import net.solace.api.movement.pathfinder.ITeleportLoader;
import net.solace.api.movement.pathfinder.ITransportLoader;
import net.solace.api.plugins.IPlugins;
import net.solace.loader.plugins.LoopedPluginManager;
import net.solace.api.plugins.PluginManager;
import net.solace.loader.plugins.PluginManagerImpl;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.config.ConfigManagerImpl;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.impl.reflection.ReflectionManager;
import net.solace.api.widgets.IWidgets;
import net.solace.loader.SolaceInitializer;
import net.solace.loader.SolaceManager;
import net.solace.loader.config.SolaceProperties;
import net.solace.loader.thirdparty.IncompatiblePluginChecker;
import net.solace.loader.util.NonScheduledExecutorServiceExceptionLogger;
import net.solace.ui.plugins.ProfileManager;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LoaderModule extends AbstractModule {
    private final String account;
    private final String scriptArgs;
    @Getter
    private final String script;
    private final Integer world;
    private final String ef;
    private final String solaceProfile;

    public LoaderModule() {
        var optionParser = new OptionParser(false);
        optionParser.allowsUnrecognizedOptions();
        var argsStr = System.getProperty("solace.args");
        var args = argsStr == null ? new String[0] : argsStr.split(";");
        log.debug("All received args: {}", Arrays.toString(args));
        var accountOpt = optionParser.accepts("account")
                .withRequiredArg()
                .ofType(String.class);
        var scriptArgsOpt = optionParser.accepts("scriptArgs")
                .withRequiredArg()
                .ofType(String.class);
        var scriptOpt = optionParser.accepts("script")
                .withRequiredArg()
                .ofType(String.class);
        var worldOpt = optionParser.accepts("world")
                .withRequiredArg()
                .ofType(Integer.class);
        var eternalFarmOpt = optionParser.accepts("ef")
                .withRequiredArg()
                .ofType(String.class);
        var solaceProfileOpt = optionParser.accepts("solace-profile")
                .withRequiredArg()
                .ofType(String.class);

        var options = optionParser.parse(args);

        this.account = options.valueOf(accountOpt);
        this.scriptArgs = options.valueOf(scriptArgsOpt);
        this.script = options.valueOf(scriptOpt);
        this.world = options.valueOf(worldOpt);
        this.ef = options.valueOf(eternalFarmOpt);
        this.solaceProfile = options.valueOf(solaceProfileOpt);
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("rsAccount")).toProvider(Providers.of(account));
        bind(String[].class).annotatedWith(Names.named("scriptArgs"))
                .toProvider(Providers.of(Objects.requireNonNullElse(scriptArgs, "").split(" ")));
        bind(String.class).annotatedWith(Names.named("script")).toProvider(Providers.of(script));
        bind(Integer.class).annotatedWith(Names.named("world")).toProvider(Providers.of(world));
        bind(String.class).annotatedWith(Names.named("ef")).toProvider(Providers.of(ef));
    }

    @Provides
    @Singleton
    @Named("shutDownExecutor")
    ExecutorService shutDownExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    ExecutorService provideExecutorService() {
        var poolSize = 2 * Runtime.getRuntime().availableProcessors();

        // Will start up to poolSize threads (because of allowCoreThreadTimeOut) as necessary, and times out
        // unused threads after 1 minute
        var executor = new ThreadPoolExecutor(poolSize, poolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("worker-%d").build());
        executor.allowCoreThreadTimeOut(true);

        return new NonScheduledExecutorServiceExceptionLogger(executor);
    }

    @Provides
    @Singleton
    ProfileManager provideProfileManager() {
        return new ProfileManager(RuneLiteAPI.GSON);
    }

    @Provides
    @Singleton
    ConfigManager provideConfigManager(
            ScheduledExecutorService executorService,
            EventBus eventBus,
            ProfileManager profileManager,
            @Nullable Client client,
            @Named("shutDownExecutor") ExecutorService shutDownExecutor,
            ILoadoutFactory loadoutFactory
    ) {
        return new ConfigManagerImpl(executorService, solaceProfile, eventBus, profileManager, client, RuneLiteAPI.GSON, shutDownExecutor, loadoutFactory);
    }

    @Provides
    @Singleton
    ReflectionManager provideReflectionManager() {
        return new ReflectionManager();
    }

    @Provides
    @Singleton
    WalkerManager provideWalkerManager(
            ConfigManager configManager,
            SolaceConfig solaceConfig,
            IWidgets widgets,
            IPlayers players,
            IGame game,
            ITransportLoader transportLoader,
            ITeleportLoader teleportLoader,
            IClient client
    ) {
        return new WalkerManager(configManager, solaceConfig, widgets, players, game, transportLoader,
                teleportLoader, client);
    }

    @Provides
    @Singleton
    PluginManager providePluginManager(
            EventBus eventBus,
            ConfigManager configManager,
            Scheduler scheduler,
            LoopedPluginManager loopedPluginManager
    ) {
        return new PluginManagerImpl(configManager, eventBus, scheduler, script, loopedPluginManager);
    }

    @Provides
    @Singleton
    LoopedPluginManager provideLoopedPluginManager(EventBus eventBus, IGame game, IPlugins plugins, ITime time, IClient client, ChatMessageManager chatMessageManager) {
        return new LoopedPluginManager(eventBus, game, plugins, time, client, chatMessageManager);
    }

    @Provides
    SolaceManager provideSolaceManager(Injector injector) {
        return injector.getInstance(SolaceInitializer.class);
    }

    @Provides
    @Singleton
    net.solace.api.util.SolaceProperties provideSolaceProperties() {
        return new SolaceProperties();
    }

    @Provides
    @Singleton
    IncompatiblePluginChecker provideIncompatiblePluginChecker(net.runelite.client.plugins.PluginManager pluginManager) {
        return new IncompatiblePluginChecker(pluginManager);
    }
}
