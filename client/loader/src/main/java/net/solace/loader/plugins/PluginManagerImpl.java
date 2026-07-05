package net.solace.loader.plugins;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.reflect.ClassPath;
import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.task.Schedule;
import net.runelite.client.task.ScheduledMethod;
import net.runelite.client.task.Scheduler;
import net.runelite.client.util.ReflectUtil;
import net.solace.api.Static;
import net.solace.api.events.ConfigChanged;
import net.solace.api.events.PluginChanged;
import net.solace.api.events.ProfileChanged;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDependency;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginInstantiationException;
import net.solace.api.util.SwingUtil;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
public class PluginManagerImpl implements PluginManager {
    private static final String PLUGIN_PACKAGE = "net.solace.loader.plugins";

    private final List<Plugin> plugins = new CopyOnWriteArrayList<>();
    private final List<Plugin> activePlugins = new CopyOnWriteArrayList<>();
    private final List<Plugin> corePlugins = new CopyOnWriteArrayList<>();

    private final Set<Plugin> enabledPlugins = new HashSet<>();

    private final ConfigManager configManager;
    private final EventBus eventBus;
    private final Scheduler scheduler;
    private final String script;
    private final LoopedPluginManager loopedPluginManager;

    public PluginManagerImpl(
            ConfigManager configManager,
            EventBus eventBus,
            Scheduler scheduler,
            String script,
            LoopedPluginManager loopedPluginManager
    ) {
        this.configManager = configManager;
        this.eventBus = eventBus;
        this.scheduler = scheduler;
        this.script = script;
        this.loopedPluginManager = loopedPluginManager;

        eventBus.register(this);
    }

    private static <T> List<T> topologicalSort(Graph<T> graph) {
        var graphCopy = Graphs.copyOf(graph);
        List<T> l = new ArrayList<>();
        var s = graphCopy.nodes().stream()
                .filter(node -> graphCopy.inDegree(node) == 0)
                .collect(Collectors.toSet());
        while (!s.isEmpty()) {
            var it = s.iterator();
            var n = it.next();
            it.remove();

            l.add(n);

            for (var m : new HashSet<>(graphCopy.successors(n))) {
                graphCopy.removeEdge(n, m);
                if (graphCopy.inDegree(m) == 0) {
                    s.add(m);
                }
            }
        }
        if (!graphCopy.edges().isEmpty()) {
            throw new RuntimeException("Graph has at least one cycle");
        }
        return l;
    }

    @Override
    public Config getPluginConfigProxy(Plugin plugin) {
        try {
            var injector = plugin.getInjector();
            if (injector == null) {
                // Create injector for the module
                Module pluginModule = (Binder binder) ->
                {
                    // Since the plugin itself is a module, it won't bind itself, so we'll bind it here
                    binder.bind((Class<Plugin>) plugin.getClass()).toInstance(plugin);
                    binder.install(plugin);
                };
                var pluginInjector = Static.injector.createChildInjector(pluginModule);
                pluginInjector.injectMembers(plugin);
                plugin.setInjector(pluginInjector);
                injector = pluginInjector;
            }
            for (var key : injector.getBindings().keySet()) {
                var type = key.getTypeLiteral().getRawType();
                if (Config.class.isAssignableFrom(type)) {
                    return (Config) injector.getInstance(key);
                }
            }
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable e) {
            log.error("Unable to get plugin config", e);
        }
        return null;
    }

    @Override
    public List<Config> getPluginConfigProxies(Collection<Plugin> plugins) {
        List<Injector> injectors = new ArrayList<>();
        if (plugins == null) {
            injectors.add(Static.injector);
            plugins = getPlugins();
        }

        plugins.forEach(pl -> {
            if (pl.getInjector() == null) {
                // Create injector for the module
                Module pluginModule = (Binder binder) ->
                {
                    // Since the plugin itself is a module, it won't bind itself, so we'll bind it here
                    binder.bind((Class<Plugin>) pl.getClass()).toInstance(pl);
                    binder.install(pl);
                };
                var pluginInjector = Static.injector.createChildInjector(pluginModule);
                pluginInjector.injectMembers(pl);
                pl.setInjector(pluginInjector);
            }

            injectors.add(pl.getInjector());
        });

        List<Config> list = new ArrayList<>();
        for (var injector : injectors) {
            for (var key : injector.getBindings().keySet()) {
                var type = key.getTypeLiteral().getRawType();
                if (Config.class.isAssignableFrom(type)) {
                    var config = (Config) injector.getInstance(key);
                    list.add(config);
                }
            }
        }

        return list;
    }

    @Override
    public void loadDefaultPluginConfiguration(Collection<Plugin> plugins) {
        try {
            for (var config : getPluginConfigProxies(plugins)) {
                configManager.setDefaultConfiguration(config, false);
            }
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable ex) {
            log.error("Unable to reset plugin configuration", ex);
        }
    }

    @Override
    public void startPlugins() {
        startPlugins(plugins);
    }

    @Override
    public void startPlugins(Collection<Plugin> pluginsToStart) {
        var scannedPlugins = new ArrayList<>(pluginsToStart);

        try {
            SwingUtilities.invokeAndWait(() -> {
                for (var plugin : scannedPlugins) {
                    try {
                        startPlugin(plugin);
                    } catch (PluginInstantiationException ex) {
                        log.error("Unable to start plugin {}", plugin.getName(), ex);
                        pluginsToStart.remove(plugin);
                    }
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        for (var plugin : pluginsToStart) {
            ReflectUtil.queueInjectorAnnotationCacheInvalidation(plugin.getInjector());
        }
    }


    @Override
    public void stopPlugins() {
        var scannedPlugins = new ArrayList<>(plugins);

        try {
            SwingUtil.syncExec(() -> {
                for (var plugin : scannedPlugins) {
                    try {
                        setPluginEnabled(plugin, false);
                        stopPlugin(plugin, false);
                    } catch (PluginInstantiationException ex) {
                        log.error("Unable to stop plugin {}", plugin.getName(), ex);
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Plugin> loadPlugins(List<Class<?>> plugins, BiConsumer<Integer, Integer> onPluginLoaded) throws PluginInstantiationException {
        MutableGraph<Class<? extends Plugin>> graph = GraphBuilder
                .directed()
                .build();

        for (var clazz : plugins) {
            var pluginDescriptor = clazz.getAnnotation(PluginDescriptor.class);

            if (Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            if (pluginDescriptor == null) {
                if (Plugin.class.isAssignableFrom(clazz)) {
                    log.error("Class {} is a plugin, but has no plugin descriptor", clazz);
                }
                continue;
            }

            if (!Plugin.class.isAssignableFrom(clazz)) {
                log.error("Class {} has plugin descriptor, but is not a plugin", clazz);
                continue;
            }

            graph.addNode((Class<Plugin>) clazz);
        }

        // Build plugin graph
        for (var pluginClazz : graph.nodes()) {
            var pluginDependencies = pluginClazz.getAnnotationsByType(PluginDependency.class);

            for (var pluginDependency : pluginDependencies) {
                if (graph.nodes().contains(pluginDependency.value())) {
                    graph.putEdge(pluginDependency.value(), pluginClazz);
                }
            }
        }

        if (Graphs.hasCycle(graph)) {
            throw new PluginInstantiationException("Plugin dependency graph contains a cycle!");
        }

        var sortedPlugins = topologicalSort(graph);

        var loaded = 0;
        List<Plugin> newPlugins = new ArrayList<>();
        for (var pluginClazz : sortedPlugins) {
            Plugin plugin;
            try {
                plugin = instantiate(this.plugins, (Class<Plugin>) pluginClazz);
                newPlugins.add(plugin);
                this.plugins.add(plugin);
            } catch (PluginInstantiationException ex) {
                log.error("Error instantiating plugin!", ex);
            }

            loaded++;
            if (onPluginLoaded != null) {
                onPluginLoaded.accept(loaded, sortedPlugins.size());
            }
        }

        return newPlugins;
    }

    @Override
    public boolean startPlugin(Plugin plugin) throws PluginInstantiationException {
        // plugins always start in the EDT
        assert SwingUtilities.isEventDispatchThread();

        if (activePlugins.contains(plugin) || !isPluginEnabled(plugin)) {
            return false;
        }

        var conflicts = conflictsForPlugin(plugin);
        for (var conflict : conflicts) {
            if (isPluginEnabled(conflict)) {
                setPluginEnabled(conflict, false);
            }
            if (activePlugins.contains(conflict)) {
                stopPlugin(conflict);
            }
        }

        activePlugins.add(plugin);

        try {
            plugin.startUp();


            eventBus.register(plugin);
            schedule(plugin);
            if (plugin instanceof LoopedPlugin) {
                loopedPluginManager.register(((LoopedPlugin) plugin));
            }
            eventBus.post(new PluginChanged(plugin, true));
        } catch (PluginInstantiationException e) {
            JOptionPane.showMessageDialog(
                    null,
                    String.format("An error occurred while starting plugin: '%s'.\n%s", plugin.getName(), e.getMessage()),
                    "Plugin error.",
                    JOptionPane.ERROR_MESSAGE
            );
            throw e;
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable ex) {
            log.error("Failed to start plugin: {}", plugin.getName(), ex);
            throw new PluginInstantiationException(ex);
        }

        return true;
    }

    @Override
    public boolean stopPlugin(Plugin plugin) throws PluginInstantiationException {
        return stopPlugin(plugin, true);
    }

    private boolean stopPlugin(Plugin plugin, boolean unregister) throws PluginInstantiationException {
        assert SwingUtilities.isEventDispatchThread();

        if (!activePlugins.remove(plugin)) {
            return false;
        }

        unschedule(plugin);
        eventBus.unregister(plugin);

        try {
            plugin.shutDown();
            if (plugin instanceof LoopedPlugin) {
                loopedPluginManager.unregister(((LoopedPlugin) plugin));
            }

            eventBus.post(new PluginChanged(plugin, false));
        } catch (Exception ex) {
            throw new PluginInstantiationException(ex);
        }

        return true;
    }

    @Override
    public void setPluginEnabled(Plugin plugin, boolean enabled) {
        var pluginDescriptor = plugin.getClass().getAnnotation(PluginDescriptor.class);
        var keyName = pluginDescriptor.name();

        if (enabled) {
            enabledPlugins.add(plugin);
            triggerConfigRebuild(keyName);

            var conflicts = conflictsForPlugin(plugin);
            for (var conflict : conflicts) {
                if (isPluginEnabled(conflict)) {
                    setPluginEnabled(conflict, false);
                }
            }
        } else {
            enabledPlugins.remove(plugin);
            triggerConfigRebuild(keyName);
        }
    }

    @Override
    public boolean isPluginEnabled(Plugin plugin) {
        return enabledPlugins.contains(plugin);
    }

    @Override
    public void add(Plugin plugin) {
        plugins.add(plugin);
    }

    @Override
    public void remove(Plugin plugin) {
        plugins.remove(plugin);
    }

    @Override
    public Collection<Plugin> getPlugins() {
        return plugins;
    }

    @Override
    public void loadCorePlugins() throws IOException, PluginInstantiationException {
        var classPath = ClassPath.from(getClass().getClassLoader());

        List<Class<?>> plugins = classPath.getTopLevelClassesRecursive(PLUGIN_PACKAGE).stream()
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toList());

        var loadedPlugins = loadPlugins(plugins, null);
        corePlugins.addAll(loadedPlugins);

        loadDefaultPluginConfiguration(corePlugins);

        for (var plugin : loadedPlugins) {
            var pluginDescriptor = plugin.getClass().getAnnotation(PluginDescriptor.class);
            var enabledByDefault = pluginDescriptor.enabledByDefault();
            if (enabledByDefault) {
                enabledPlugins.add(plugin);
            }
        }
    }

    @Override
    public void startCorePlugins() {
        startPlugins(corePlugins);
    }

    private void schedule(Plugin plugin) {
        for (var method : plugin.getClass().getMethods()) {
            var schedule = method.getAnnotation(Schedule.class);

            if (schedule == null) {
                continue;
            }

            Runnable runnable = null;
            try {
                final var clazz = method.getDeclaringClass();
                final var caller = ReflectUtil.privateLookupIn(clazz);
                final var subscription = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
                final var target = caller.findVirtual(clazz, method.getName(), subscription);
                final var site = LambdaMetafactory.metafactory(
                        caller,
                        "run",
                        MethodType.methodType(Runnable.class, clazz),
                        subscription,
                        target,
                        subscription);

                final var factory = site.getTarget();
                runnable = (Runnable) factory.bindTo(plugin).invokeExact();
            } catch (Throwable e) {
                log.warn("Unable to create lambda for method {}", method, e);
            }

            var scheduledMethod = new ScheduledMethod(schedule, method, plugin, runnable);

            scheduler.addScheduledMethod(scheduledMethod);
        }
    }

    private void unschedule(Plugin plugin) {
        List<ScheduledMethod> methods = new ArrayList<>(scheduler.getScheduledMethods());

        for (var method : methods) {
            if (method.getObject() != plugin) {
                continue;
            }

            scheduler.removeScheduledMethod(method);
        }
    }

    public List<Plugin> conflictsForPlugin(Plugin plugin) {
        Set<String> conflicts;
        {
            var desc = plugin.getClass().getAnnotation(PluginDescriptor.class);
            conflicts = new HashSet<>(Arrays.asList(desc.conflicts()));
            conflicts.add(desc.name());
        }

        return plugins.stream()
                .filter(p -> {
                    if (p == plugin) {
                        return false;
                    }

                    var desc = p.getClass().getAnnotation(PluginDescriptor.class);
                    if (conflicts.contains(desc.name())) {
                        return true;
                    }

                    for (var conflict : desc.conflicts()) {
                        if (conflicts.contains(conflict)) {
                            return true;
                        }
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    private Plugin instantiate(List<Plugin> scannedPlugins, Class<Plugin> clazz) throws PluginInstantiationException {
        var pluginDependencies = clazz.getAnnotationsByType(PluginDependency.class);
        List<Plugin> deps = new ArrayList<>();
        for (var pluginDependency : pluginDependencies) {
            var dependency = scannedPlugins.stream().filter(p -> p.getClass() == pluginDependency.value()).findFirst();
            if (dependency.isEmpty()) {
                throw new PluginInstantiationException("Unmet dependency for " + clazz.getSimpleName() + ": " + pluginDependency.value().getSimpleName());
            }

            deps.add(dependency.get());
        }

        Plugin plugin;
        try {
            plugin = clazz.getDeclaredConstructor().newInstance();
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable ex) {
            throw new PluginInstantiationException(ex);
        }

        try {
            var parent = Static.injector;

            if (deps.size() > 1) {
                List<Module> modules = new ArrayList<>(deps.size());
                for (var p : deps) {
                    // Create a module for each dependency
                    Module module = (Binder binder) ->
                    {
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
            Module pluginModule = (Binder binder) ->
            {
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

            plugin.setInjector(pluginInjector);
        } catch (CreationException ex) {
            throw new PluginInstantiationException(ex);
        }

        return plugin;
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        refreshPlugins();
    }

    private void refreshPlugins() {
        loadDefaultPluginConfiguration(null);
        SwingUtilities.invokeLater(() -> {
            for (var plugin : getPlugins()) {
                try {
                    if (plugin.isSdn()) {
                        setPluginEnabled(plugin, false);
                        stopPlugin(plugin);
                    }

                    if (isPluginEnabled(plugin) != activePlugins.contains(plugin)) {
                        if (activePlugins.contains(plugin)) {
                            stopPlugin(plugin);
                        } else {
                            startPlugin(plugin);
                        }
                    }

                    if (script != null && Objects.equals(plugin.getName(), script)) {
                        startPlugin(plugin);
                    }
                } catch (PluginInstantiationException e) {
                    log.error("Error during starting/stopping plugin {}", plugin.getClass().getSimpleName(), e);
                }
            }
        });
    }

    private void triggerConfigRebuild(String keyName) {
        var event = new ConfigChanged();
        event.setGroup(keyName);
        eventBus.post(event);
    }
}
