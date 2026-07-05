package net.solace.sdn;

import com.google.common.collect.Lists;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.solace.api.Static;
import net.solace.api.events.ExternalPluginsChanged;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDependency;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.PluginMetaData;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginInstantiationException;
import net.solace.api.util.SwingUtil;
import net.solace.loader.events.SdnPluginChanged;
import net.solace.sdn.pf4j.MissingDependenciesException;
import net.solace.sdn.pf4j.SdnPf4jPluginManager;
import net.solace.sdn.pf4j.SdnPluginDescriptor;
import net.solace.sdn.update.SdnPluginInfo;
import net.solace.sdn.update.SdnRepository;
import net.solace.sdn.update.SdnUpdateManager;
import org.pf4j.DependencyResolver;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginWrapper;
import org.pf4j.update.VerifyException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.solace.loader.commons.Directories.EXTERNALPLUGIN_DIR;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class SdnPluginManagerImpl implements SdnPluginManager {
    public static ArrayList<ClassLoader> pluginClassLoaders = new ArrayList<>();

    static {
        try {
            Files.createDirectories(EXTERNALPLUGIN_DIR);
        } catch (IOException e) {
            log.error("Error creating external plugin directory", e);
        }
    }

    @Getter(AccessLevel.PUBLIC)
    private final List<SdnRepository> repositories = new ArrayList<>();
    private final Map<String, String> pluginsMap = new HashMap<>();
    @Getter(AccessLevel.PUBLIC)
    private final Map<String, Map<String, String>> pluginsInfoMap = new HashMap<>();
    private final ConfigManager configManager;
    private final ScheduledExecutorService executorService;
    private final EventBus eventBus;
    private final PluginManager solacePluginManager;
    @Getter
    private final SdnPf4jPluginManager pluginManager;
    @Getter
    private final SdnUpdateManager updateManager;
    @Getter
    private final SdnRepository sdnRepository;

    public SdnPluginManagerImpl(
            SdnRepository sdnRepository,
            ConfigManager configManager,
            ScheduledExecutorService executorService,
            EventBus eventBus,
            PluginManager solacePluginManager,
            SdnPf4jPluginManager sdnPf4jPluginManager,
            SdnUpdateManager updateManager
    ) {
        this.sdnRepository = sdnRepository;
        this.configManager = configManager;
        this.executorService = executorService;
        this.eventBus = eventBus;
        this.solacePluginManager = solacePluginManager;
        this.pluginManager = sdnPf4jPluginManager;
        this.updateManager = updateManager;
    }

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    public static <T> List<List<T>> topologicalGroupSort(Graph<T> graph) {
        final Set<T> root = graph.nodes().stream()
                .filter(node -> graph.inDegree(node) == 0)
                .collect(Collectors.toSet());
        final Map<T, Integer> dependencyCount = new HashMap<>();

        root.forEach(n -> dependencyCount.put(n, 0));
        root.forEach(n -> graph.successors(n)
                .forEach(m -> incrementChildren(graph, dependencyCount, m, dependencyCount.get(n) + 1)));

        // create list<list> dependency grouping
        final List<List<T>> dependencyGroups = new ArrayList<>();
        final int[] curGroup = {-1};

        dependencyCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry ->
                {
                    if (entry.getValue() != curGroup[0]) {
                        curGroup[0] = entry.getValue();
                        dependencyGroups.add(new ArrayList<>());
                    }
                    dependencyGroups.get(dependencyGroups.size() - 1).add(entry.getKey());
                });

        return dependencyGroups;
    }

    private static <T> void incrementChildren(Graph<T> graph, Map<T, Integer> dependencyCount, T n, int val) {
        if (!dependencyCount.containsKey(n) || dependencyCount.get(n) < val) {
            dependencyCount.put(n, val);
            graph.successors(n).forEach(m ->
                    incrementChildren(graph, dependencyCount, m, val + 1));
        }
    }


    @Override
    public void startExternalPluginManager() {
        try {
            pluginManager.loadPlugins();
        } catch (Exception ex) {
            if (ex instanceof MissingDependenciesException) {
                var deps = ((MissingDependenciesException) ex).getDependencies();
                var reverseDepMap = ((MissingDependenciesException) ex).getReverseDependencyMap();

                for (var dependency : deps) {
                    var dependentPlugins = reverseDepMap.get(dependency);

                    log.error("Dependency {} is missing and is required by {}.", dependency, dependentPlugins);
                    dependentPlugins.forEach(pluginManager::disableLoading);
                }

                startExternalPluginManager();
            } else {
                log.error("Could not load plugins", ex);
            }
        }
    }

    public void scanAndInstantiate(List<Plugin> plugins, boolean init, boolean initConfig) {
        MutableGraph<Class<? extends Plugin>> graph = GraphBuilder
                .directed()
                .build();

        for (var plugin : plugins) {
            var clazz = plugin.getClass();
            var pluginDescriptor = clazz.getAnnotation(PluginDescriptor.class);

            try {
                if (pluginDescriptor == null) {
                    if (Plugin.class.isAssignableFrom(clazz)) {
                        log.warn("Class {} is a plugin, but has no plugin descriptor", clazz);
                    }
                    continue;
                } else if (!Plugin.class.isAssignableFrom(clazz)) {
                    log.warn("Class {} has plugin descriptor, but is not a plugin", clazz);
                    continue;
                }
            } catch (EnumConstantNotPresentException e) {
                log.warn("{} has an invalid plugin type of {}", clazz, e.getMessage());
                continue;
            }

            @SuppressWarnings("unchecked") var pluginClass = (Class<Plugin>) clazz;
            graph.addNode(pluginClass);
        }

        List<Class<? extends Plugin>> toRemove = new ArrayList<>();
        // Build plugin graph
        for (var pluginClazz : graph.nodes()) {
            var pluginDependencies = pluginClazz.getAnnotationsByType(PluginDependency.class);

            for (var pluginDependency : pluginDependencies) {
                try {
                    if (graph.nodes().contains(pluginDependency.value())) {
                        graph.putEdge(pluginClazz, pluginDependency.value());
                    }
                } catch (TypeNotPresentException e) {
                    log.warn("Unable to load plugin dependency {} for {}", e.typeName(), pluginClazz);
                    toRemove.add(pluginClazz);
                }
            }
        }

        toRemove.forEach(graph::removeNode);

        if (Graphs.hasCycle(graph)) {
            throw new RuntimeException("Plugin dependency graph contains a cycle!");
        }

        var sortedPlugins = topologicalGroupSort(graph);
        sortedPlugins = Lists.reverse(sortedPlugins);
        var loaded = new AtomicInteger();

        final var start = System.currentTimeMillis();

        List<Plugin> scannedPlugins = new CopyOnWriteArrayList<>();
        sortedPlugins.forEach(group -> {
            List<Future<?>> curGroup = new ArrayList<>();
            group.forEach(pluginClazz ->
                    curGroup.add(executorService.submit(() ->
                    {
                        Plugin plugininst;
                        try {
                            //noinspection unchecked
                            plugininst = instantiate(scannedPlugins, (Class<Plugin>) pluginClazz, init, initConfig);
                            if (plugininst == null) {
                                return;
                            }

                            scannedPlugins.add(plugininst);
                        } catch (PluginInstantiationException e) {
                            log.warn("Error instantiating plugin!", e);
                            return;
                        }

                        loaded.getAndIncrement();
                    })));
            curGroup.forEach(future ->
            {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.warn("Could not instantiate external plugin", e);
                }
            });
        });

        log.info("External plugin instantiation took {}ms", System.currentTimeMillis() - start);
    }

    @SuppressWarnings("unchecked")
    private Plugin instantiate(List<Plugin> scannedPlugins, Class<Plugin> clazz, boolean init, boolean initConfig) throws PluginInstantiationException {
        var pluginDependencies =
                clazz.getAnnotationsByType(PluginDependency.class);
        List<Plugin> deps = new ArrayList<>();
        for (var pluginDependency : pluginDependencies) {
            var dependency =
                    Stream.concat(solacePluginManager.getPlugins().stream(), scannedPlugins.stream())
                            .filter(p -> p.getClass() == pluginDependency.value()).findFirst();
            if (dependency.isEmpty()) {
                throw new PluginInstantiationException(
                        "Unmet dependency for " + clazz.getSimpleName() + ": " + pluginDependency.value().getSimpleName());
            }
            deps.add(dependency.get());
        }

        log.info("Loading plugin {}", clazz.getSimpleName());
        Plugin plugin;
        try {
            plugin = clazz.getDeclaredConstructor().newInstance();

            var info = pluginsInfoMap.get(plugin.getName());
            var subscriptionId = info.get("subscriptionId");
            var pluginMetaData = new PluginMetaData(
                    info.get("id"),
                    plugin.getName(),
                    info.get("version"),
                    subscriptionId != null ? Long.parseLong(subscriptionId) : null,
                    info.get("commitHash")
            );

            plugin.setPluginMetaData(pluginMetaData);
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable ex) {
            throw new PluginInstantiationException(ex);
        }

        log.info("Instantiated plugin {}", plugin.getName());

        try {
            var parent = Static.injector;

            if (deps.size() > 1) {
                List<Module> modules = new ArrayList<>(deps.size());
                for (var p : deps) {
                    // Create a module for each dependency
                    Module module = (Binder binder) -> {
                        binder.bind((Class<Plugin>) p.getClass()).toInstance(p);
                        binder.install(p);
                    };
                    modules.add(module);
                }

                // Create a parent injector containing all of the dependencies
                parent = parent.createChildInjector(modules);
            } else if (!deps.isEmpty()) {
                // With only one dependency we can simply use its injector
                parent = deps.get(0).getInjector();
            }

            // Create injector for the module
            Module pluginModule = (Binder binder) -> {
                // Since the plugin itself is a module, it won't bind itself, so we'll bind it here
                binder.bind(clazz).toInstance(plugin);
                binder.install(plugin);
            };

            Injector pluginInjector;

            try {
                pluginInjector = parent.createChildInjector(pluginModule);
            } catch (NoClassDefFoundError ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        null,
                        "'" + plugin.getName() + "' could not be installed because" +
                        " it depends on '" + ex.getMessage() + "'. Make sure it is installed.",
                        "Installation error",
                        JOptionPane.ERROR_MESSAGE
                ));
                throw new PluginInstantiationException(ex);
            }

            pluginInjector.injectMembers(plugin);
            plugin.setInjector(pluginInjector);

            if (initConfig) {
                for (var key : pluginInjector.getBindings().keySet()) {
                    var type = key.getTypeLiteral().getRawType();
                    if (Config.class.isAssignableFrom(type)) {
                        var config = (Config) pluginInjector.getInstance(key);
                        configManager.setDefaultConfiguration(config, false);
                    }
                }
            }

            if (init) {
                try {
                    SwingUtil.syncExec(() ->
                    {
                        try {
                            solacePluginManager.add(plugin);
                            solacePluginManager.startPlugin(plugin);
                            eventBus.post(new SdnPluginChanged(pluginsMap.get(plugin.getName()),
                                    plugin, true));
                        } catch (PluginInstantiationException e) {
                            log.error("Error starting plugin!", e);
                        }
                    });
                } catch (Exception ex) {
                    log.warn("unable to start plugin", ex);
                }
            } else {
                solacePluginManager.add(plugin);
            }
        } catch (CreationException ex) {
            log.error("Error creating injector for plugin: '{}'", plugin.getName());
            throw new PluginInstantiationException(ex);
        } catch (NoClassDefFoundError | NoSuchFieldError | NoSuchMethodError ex) {
            log.error("Plugin {} is outdated", plugin.getName(), ex);
            return null;
        }

        return plugin;
    }

    public void checkDepsAndStart(List<PluginWrapper> startedPlugins, List<Plugin> scannedPlugins, PluginWrapper pluginWrapper) {
        var depsLoaded = true;
        for (var dependency : pluginWrapper.getDescriptor().getDependencies()) {
            if (startedPlugins.stream().noneMatch(pl -> pl.getPluginId().equals(dependency.getPluginId()))) {
                depsLoaded = false;
            }
        }

        if (!depsLoaded) {
            // This should never happen but can crash the client
            return;
        }

        var plugins = loadPlugin(pluginWrapper);

        scannedPlugins.addAll(plugins);
    }

    @Override
    public void startPlugins() {
        pluginManager.startPlugins();

        List<Plugin> scannedPlugins = new ArrayList<>();
        var startedPlugins = getStartedPlugins();
        for (var plugin : startedPlugins) {
            checkDepsAndStart(startedPlugins, scannedPlugins, plugin);
        }

        scanAndInstantiate(scannedPlugins, false, false);

        for (var pluginId : getDisabledPluginIds()) {
            pluginManager.enablePlugin(pluginId);
            pluginManager.deletePlugin(pluginId);
        }
    }

    public List<Plugin> loadPlugin(PluginWrapper pluginWrapper) {
        var pluginId = pluginWrapper.getPluginId();
        List<Plugin> scannedPlugins = new ArrayList<>();
        try {
            var extensions = pluginManager.getExtensions(Plugin.class, pluginId);
            for (var plugin : extensions) {
                var classLoader = plugin.getClass().getClassLoader();
                pluginClassLoaders.add(classLoader);

                pluginsMap.remove(plugin.getName());
                pluginsMap.put(plugin.getName(), pluginId);

                pluginsInfoMap.remove(plugin.getName());

                var info = sdnRepository.getPlugin(pluginId);
                var descriptor = ((SdnPluginDescriptor) pluginWrapper.getDescriptor());

                pluginsInfoMap.put(
                        plugin.getName(),
                        new HashMap<>() {{
                            put("version", descriptor.getVersion());
                            put("id", descriptor.getPluginId());
                            put("provider", descriptor.getProvider());

                            var commitHash = descriptor.getCommitHash();
                            if (commitHash != null) {
                                put("commitHash", commitHash);
                            }

                            if (info instanceof SdnPluginInfo) {
                                var subscriptionId = ((SdnPluginInfo) info).subscriptionId;
                                put("subscriptionId", subscriptionId != null ? String.valueOf(subscriptionId) : null);
                            }
                        }}
                );

                scannedPlugins.add(plugin);
            }
        } catch (Throwable ex) {
            log.error("Plugin {} could not be loaded.", pluginId, ex);
        }

        return scannedPlugins;
    }

    private Path stopPlugin(String pluginId) {
        var startedPlugins = List.copyOf(getStartedPlugins());

        for (var pluginWrapper : startedPlugins) {
            if (!pluginId.equals(pluginWrapper.getDescriptor().getPluginId())) {
                continue;
            }

            var extensions = pluginManager.getExtensions(Plugin.class, pluginId);

            for (var plugin : solacePluginManager.getPlugins()) {
                var found = false;
                for (var extension : extensions) {
                    if (extension.getName().equals(plugin.getName())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    continue;
                }

                try {
                    SwingUtil.syncExec(() ->
                    {
                        try {
                            solacePluginManager.stopPlugin(plugin);
                        } catch (Exception e2) {
                            throw new RuntimeException(e2);
                        }
                    });
                    solacePluginManager.remove(plugin);
                    pluginClassLoaders.remove(plugin.getClass().getClassLoader());

                    eventBus.post(new SdnPluginChanged(pluginId, plugin, false));
                    eventBus.post(new ExternalPluginsChanged());

                    return pluginWrapper.getPluginPath();
                } catch (Exception ex) {
                    log.warn("unable to stop plugin", ex);
                    return null;
                }
            }
        }

        return null;
    }

    public boolean install(String pluginId) throws IOException {

        if (getDisabledPluginIds().contains(pluginId)) {

            pluginManager.enablePlugin(pluginId);
            pluginManager.startPlugin(pluginId);

            var wrapper = pluginManager.getPlugin(pluginId);
            scanAndInstantiate(loadPlugin(wrapper), true, false);
            var event = new ExternalPluginsChanged();
            eventBus.post(event);

            return true;
        }

        if (getStartedPlugins().stream().anyMatch(ev -> ev.getPluginId().equals(pluginId))) {
            log.warn("Plugin {} is already installed", pluginId);
            return true;
        }

        try {
            if (!isDevMode()) {
                pluginManager.loadPlugins();
                pluginManager.startPlugin(pluginId);
                var wrapper = pluginManager.getPlugin(pluginId);
                if (wrapper == null) {
                    log.error("Plugin {} is not present in {}", pluginId, EXTERNALPLUGIN_DIR);
                    return false;
                }
                var extensions = loadPlugin(wrapper);
                if (extensions.isEmpty()) {
                    log.error("Plugin {} could not be loaded because it has no extensions.", pluginId);
                    return false;
                }

                scanAndInstantiate(extensions, true, true);
            } else {
                // In development mode our plugin will already be present in a repository, so we can just load it
                pluginManager.loadPlugins();
                pluginManager.startPlugin(pluginId);
            }

            var event = new ExternalPluginsChanged();
            eventBus.post(event);
        } catch (DependencyResolver.DependenciesNotFoundException ex) {
            uninstall(pluginId);

            for (var dep : ex.getDependencies()) {
                install(dep);
            }

            install(pluginId);
        }

        return true;
    }

    public boolean uninstall(String pluginId) {
        return uninstall(pluginId, false);
    }

    @Override
    public boolean reloadStart(String pluginId) {
        pluginManager.loadPlugins();
        pluginManager.startPlugin(pluginId);

        var startedPlugins = List.copyOf(getStartedPlugins());
        var disabledPlugins = List.copyOf(getDisabledPlugins());
        var combinedList = Stream.of(startedPlugins, disabledPlugins).flatMap(Collection::stream).collect(Collectors.toList());
        List<Plugin> scannedPlugins = new ArrayList<>();

        for (var pluginWrapper : combinedList) {
            if (!pluginId.equals(pluginWrapper.getDescriptor().getPluginId())) {
                continue;
            }

            checkDepsAndStart(combinedList, scannedPlugins, pluginWrapper);
        }

        scanAndInstantiate(scannedPlugins, true, false);

        return true;
    }

    public boolean uninstall(String pluginId, boolean skip) {
        var pluginPath = stopPlugin(pluginId);

        if (pluginPath == null) {
            log.warn("Cannot uninstall, plugin '{}' is not installed", pluginId);
            return false;
        }

        pluginManager.stopPlugin(pluginId);

        if (skip) {
            return true;
        }

        pluginManager.deletePlugin(pluginId);

        return true;
    }

    @Override
    public void update() {
    }

    public Set<String> getDependencies() {
        Set<String> deps = new HashSet<>();
        var startedPlugins = getStartedPlugins();

        for (var pluginWrapper : startedPlugins) {
            for (var pluginDependency : pluginWrapper.getDescriptor().getDependencies()) {
                deps.add(pluginDependency.getPluginId());
            }
        }

        return deps;
    }

    public List<PluginWrapper> getDisabledPlugins() {
        return pluginManager.getResolvedPlugins()
                .stream()
                .filter(not(pluginManager.getStartedPlugins()::contains))
                .collect(Collectors.toList());
    }

    public List<String> getDisabledPluginIds() {
        return getDisabledPlugins()
                .stream()
                .map(PluginWrapper::getPluginId)
                .collect(Collectors.toList());
    }

    public List<PluginWrapper> getStartedPlugins() {
        return pluginManager.getStartedPlugins();
    }

    @Override
    public boolean isDevMode() {
        return pluginManager.isDevMode();
    }
}
