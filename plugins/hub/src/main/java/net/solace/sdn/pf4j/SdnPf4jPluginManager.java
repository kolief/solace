package net.solace.sdn.pf4j;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginManager;
import net.solace.sdn.pf4j.custom.SdnDefaultPluginManager;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.CompoundPluginLoader;
import org.pf4j.CompoundPluginRepository;
import org.pf4j.DependencyResolver;
import org.pf4j.DevelopmentPluginRepository;
import org.pf4j.JarPluginRepository;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginAlreadyLoadedException;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginLoader;
import org.pf4j.PluginRepository;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;
import org.pf4j.util.FileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

import static net.solace.loader.commons.Directories.EXTERNALPLUGIN_DIR;

@Slf4j
public class SdnPf4jPluginManager extends SdnDefaultPluginManager {
    public static final String DEVELOPMENT_MANIFEST_PATH = "build/tmp/jar/MANIFEST.MF";
    public static final String PLUGIN_DEVELOPMENT_PATH = "plugin.development.path";

    private final Set<String> disabledPlugins = new HashSet<>();

    private final PluginManager runeLitePluginManager;

    public SdnPf4jPluginManager(PluginManager runeLitePluginManager) {
        super(EXTERNALPLUGIN_DIR);
        this.runeLitePluginManager = runeLitePluginManager;
        this.systemVersion = "1.0.0";
        this.exactVersionAllowed = false;
        this.initialize();
    }

    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return new ManifestPluginDescriptorFinder() {
            @Override
            protected SdnPluginDescriptor createPluginDescriptor(Manifest manifest) {
                var pluginDescriptor = super.createPluginDescriptor(manifest);
                var attributes = manifest.getMainAttributes();
                var commitHash = attributes.getValue("Commit-Hash");
                return new SdnPluginDescriptor(pluginDescriptor, commitHash);
            }

            protected Manifest readManifestFromDirectory(Path pluginPath) {
                Path manifestPath;
                if (isDevelopment()) {
                    manifestPath = pluginPath.resolve(DEVELOPMENT_MANIFEST_PATH);
                } else {
                    manifestPath = FileUtils.findFile(pluginPath, "MANIFEST.MF");
                }

                if (manifestPath == null) {
                    throw new PluginRuntimeException("Cannot find the manifest path");
                }

                log.debug("Lookup plugin descriptor in '{}'", manifestPath);
                if (Files.notExists(manifestPath)) {
                    throw new PluginRuntimeException("Cannot find '{}' path", manifestPath);
                }

                try (var input = Files.newInputStream(manifestPath)) {
                    return new Manifest(input);
                } catch (IOException e) {
                    throw new PluginRuntimeException(e, "Cannot read manifest from {}", pluginPath);
                }
            }
        };
    }

    @Override
    protected PluginRepository createPluginRepository() {
        var compoundPluginRepository = new CompoundPluginRepository();

        if (isNotDevelopment()) {
            var jarPluginRepository = new JarPluginRepository(getPluginsRoot());
            compoundPluginRepository.add(jarPluginRepository);
        }

        if (isDevelopment()) {
            for (var developmentPluginPath : getPluginDevelopmentPath()) {
                var developmentPluginRepository = new DevelopmentPluginRepository(Paths.get(developmentPluginPath)) {
                    @Override
                    public boolean deletePluginPath(Path pluginPath) {
                        // Do nothing, because we'd be deleting our sources!
                        return filter.accept(pluginPath.toFile());
                    }
                };

                developmentPluginRepository.setFilter(new PluginFileFilter());
                compoundPluginRepository.add(developmentPluginRepository);
            }
        }

        return compoundPluginRepository;
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new CompoundPluginLoader()
                .add(new DevelopmentPluginLoader(this, new DevelopmentClasspath(), runeLitePluginManager), this::isDevelopment)
                .add(new SdnJarLoader(this, runeLitePluginManager), this::isNotDevelopment);
    }

    /**
     * Load all plugins from the plugins root directory.
     */
    @Override
    public void loadPlugins() {
        for (var path : pluginsRoots) {
            log.info("Loading plugins from '{}'", path);
            if (Files.notExists(path) || !Files.isDirectory(path)) {
                log.warn("No '{}' root", path);

                return;
            }
        }

        var pluginPaths = pluginRepository.getPluginPaths();

        Collections.reverse(pluginPaths);

        if (pluginPaths.isEmpty()) {
            log.warn("No plugins");
            return;
        }

        loadPlugins(pluginPaths);
    }

    protected void loadPlugins(List<Path> pluginPaths) {
        Set<String> duplicatePlugins = new HashSet<>();
        for (var pluginPath : pluginPaths) {
            var duplicate = loadIndividualPlugin(pluginPath);
            if (duplicate != null) {
                duplicatePlugins.add(duplicate);
            }
        }

        if (!duplicatePlugins.isEmpty()) {
            log.error("Duplicate plugins detected: {}", String.join(", ", duplicatePlugins));
        }

        try {
            resolvePlugins();
        } catch (PluginRuntimeException e) {
            if (e instanceof DependencyResolver.DependenciesNotFoundException) {
                throw e;
            }

            log.error("Could not resolve plugins", e);
        }
    }

    private String loadIndividualPlugin(Path pluginPath) {
        try {
            if (!isPluginEligibleForLoading(pluginPath) && isNotDevelopment()) {
                return null;
            }

            loadPluginFromPath(pluginPath);
        } catch (PluginRuntimeException e) {
            if (e instanceof PluginAlreadyLoadedException) {
                if (!isDevelopment()) {
                    return pluginPath.toString().substring(pluginsRoots.get(0).toString().length() + 1);
                }
            } else {
                log.error("Could not load plugin", e);
            }
        }

        return null;
    }

    @Override
    protected void resolvePlugins() {
        // retrieves the plugins descriptors
        List<PluginDescriptor> descriptors = new ArrayList<>();
        Multimap<String, String> reverseDepMap = MultimapBuilder.hashKeys().hashSetValues().build();
        for (var plugin : plugins.values()) {
            descriptors.add(plugin.getDescriptor());

            for (var dependency : plugin.getDescriptor().getDependencies()) {
                reverseDepMap.put(dependency.getPluginId(), plugin.getPluginId());
            }
        }

        // retrieves the plugins descriptors from the resolvedPlugins list. This allows to load plugins that have already loaded dependencies.
        for (var plugin : resolvedPlugins) {
            descriptors.add(plugin.getDescriptor());

            for (var dependency : plugin.getDescriptor().getDependencies()) {
                reverseDepMap.put(dependency.getPluginId(), plugin.getPluginId());
            }
        }

        var result = dependencyResolver.resolve(descriptors);

        if (result.hasCyclicDependency()) {
            throw new DependencyResolver.CyclicDependencyException();
        }

        var notFoundDependencies = result.getNotFoundDependencies();
        if (!notFoundDependencies.isEmpty()) {
            throw new MissingDependenciesException(notFoundDependencies, reverseDepMap);
        }

        var wrongVersionDependencies = result.getWrongVersionDependencies();
        if (!wrongVersionDependencies.isEmpty()) {
            throw new DependencyResolver.DependenciesWrongVersionException(wrongVersionDependencies);
        }

        var sortedPlugins = result.getSortedPlugins();

        // move plugins from "unresolved" to "resolved"
        for (var pluginId : sortedPlugins) {
            var pluginWrapper = plugins.get(pluginId);

            //The plugin is already resolved. Don't put a copy in the resolvedPlugins.
            if (resolvedPlugins.contains(pluginWrapper)) {
                continue;
            }

            if (unresolvedPlugins.remove(pluginWrapper)) {
                var pluginState = pluginWrapper.getPluginState();
                if (pluginState != PluginState.DISABLED) {
                    pluginWrapper.setPluginState(PluginState.RESOLVED);
                }

                resolvedPlugins.add(pluginWrapper);

                firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
            }
        }
    }

    public boolean isDevMode() {
        return getPluginDevelopmentPath().length > 0;
    }

    @Override
    public RuntimeMode getRuntimeMode() {
        return isDevMode() ? RuntimeMode.DEVELOPMENT : RuntimeMode.DEPLOYMENT;
    }

    /**
     * Fired when a plugin is uninstalled, not stopped.
     */
    @Override
    public PluginState stopPlugin(String pluginId) {
        if (!plugins.containsKey(pluginId)) {
            throw new IllegalArgumentException(String.format("Unknown pluginId %s", pluginId));
        }

        var pluginWrapper = getPlugin(pluginId);
        var pluginDescriptor = pluginWrapper.getDescriptor();
        var pluginState = pluginWrapper.getPluginState();
        if (PluginState.STOPPED == pluginState) {
            return PluginState.STOPPED;
        }

        // test for disabled plugin
        if (PluginState.DISABLED == pluginState) {
            // do nothing
            return pluginState;
        }

        pluginWrapper.getPlugin().stop();
        pluginWrapper.setPluginState(PluginState.STOPPED);
        startedPlugins.remove(pluginWrapper);

        firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));

        return pluginWrapper.getPluginState();
    }

    @Override
    public boolean unloadPlugin(String pluginId) {
        try {
            var pluginState = stopPlugin(pluginId);
            if (PluginState.STARTED == pluginState) {
                return false;
            }

            var pluginWrapper = getPlugin(pluginId);

            // remove the plugin
            plugins.remove(pluginId);
            getResolvedPlugins().remove(pluginWrapper);

            firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));

            // remove the classloader
            var pluginClassLoaders = getPluginClassLoaders();
            if (pluginClassLoaders.containsKey(pluginId)) {
                var classLoader = pluginClassLoaders.remove(pluginId);
                if (classLoader instanceof Closeable) {
                    try {
                        ((Closeable) classLoader).close();
                    } catch (IOException e) {
                        throw new PluginRuntimeException(e, "Cannot close classloader");
                    }
                }
            }

            return true;
        } catch (IllegalArgumentException e) {
            // ignore not found exceptions because this method is recursive
        }

        return false;
    }

    @Override
    public boolean deletePlugin(String pluginId) {
        if (!plugins.containsKey(pluginId)) {
            throw new IllegalArgumentException(String.format("Unknown pluginId %s", pluginId));
        }

        var pluginWrapper = getPlugin(pluginId);
        // stop the plugin if it's started
        var pluginState = stopPlugin(pluginId);
        if (PluginState.STARTED == pluginState) {
            log.error("Failed to stop plugin '{}' on delete", pluginId);
            return false;
        }

        // get an instance of plugin before the plugin is unloaded
        // for reason see https://github.com/pf4j/pf4j/issues/309

        var plugin = pluginWrapper.getPlugin();

        if (!unloadPlugin(pluginId)) {
            log.error("Failed to unload plugin '{}' on delete", pluginId);
            return false;
        }

        // notify the plugin as it's deleted
        plugin.delete();

        var pluginPath = pluginWrapper.getPluginPath();


        try {
            return pluginRepository.deletePluginPath(pluginPath);
        } catch (PluginRuntimeException e) {
            log.error("Cannot delete plugin '{}'", pluginId, e);
            return false;
        }
    }

    @Override
    protected PluginWrapper loadPluginFromPath(Path pluginPath) {
        // Test for plugin path duplication
        var pluginId = idForPath(pluginPath);
        if (pluginId != null) {
            throw new PluginAlreadyLoadedException(pluginId, pluginPath);
        }

        // Retrieve and validate the plugin descriptor
        var pluginDescriptorFinder = getPluginDescriptorFinder();

        SdnPluginDescriptor pluginDescriptor;

        try {
            pluginDescriptor = ((SdnPluginDescriptor) pluginDescriptorFinder.find(pluginPath));
        } catch (Exception e) {
            deletePluginFromPath(pluginPath);
            throw new PluginRuntimeException("Couldn't read manifest of plugin {}", pluginPath.toString()
                    .substring(pluginPath.toString().lastIndexOf(File.separator) + 1)
                    .replace(".jar", ""));
        }

        validatePluginDescriptor(pluginDescriptor);

        if (disabledPlugins.contains(pluginDescriptor.getPluginId())) {
            return null;
        }

        pluginId = pluginDescriptor.getPluginId();
        if (plugins.containsKey(pluginId)) {
            var loadedPlugin = getPlugin(pluginId);
            throw new PluginRuntimeException("There is an already loaded plugin ({}) "
                    + "with the same id ({}) as the plugin at path '{}'. Simultaneous loading "
                    + "of plugins with the same PluginId is not currently supported.\n"
                    + "As a workaround you may include PluginVersion and PluginProvider "
                    + "in PluginId.",
                    loadedPlugin, pluginId, pluginPath);
        }

        // load plugin
        var pluginClassLoader = getPluginLoader().loadPlugin(pluginPath, pluginDescriptor);

        // create the plugin wrapper
        var pluginWrapper = new PluginWrapper(this, pluginDescriptor, pluginPath, pluginClassLoader);

        pluginWrapper.setPluginFactory(getPluginFactory());

        // test for disabled plugin
        if (isPluginDisabled(pluginDescriptor.getPluginId())) {
            pluginWrapper.setPluginState(PluginState.DISABLED);
        }

        // validate the plugin
        if (!isPluginValid(pluginWrapper)) {
            pluginWrapper.setPluginState(PluginState.DISABLED);
        }

        pluginId = pluginDescriptor.getPluginId();

        if (isDevelopment() && StringUtils.isNumeric(pluginId)) {
            deletePluginFromPath(pluginPath);
            throw new PluginNotUsableException("You cannot use this plugin during development mode.");
        }

        // add plugin to the list with plugins
        plugins.put(pluginId, pluginWrapper);
        getUnresolvedPlugins().add(pluginWrapper);

        // add plugin class loader to the list with class loaders
        getPluginClassLoaders().put(pluginId, pluginClassLoader);

        return pluginWrapper;
    }

    public void disableLoading(String pluginId) {
        unloadPlugin(pluginId);
        disabledPlugins.add(pluginId);
    }

    private boolean isPluginEligibleForLoading(Path path) {
        return path.toFile().getName().endsWith(".jar");
    }

    private void deletePluginFromPath(Path pluginPath) {
        try {
            pluginRepository.deletePluginPath(pluginPath);
        } catch (Exception ex) {
        }
    }

    public static String[] getPluginDevelopmentPath() {
        var developmentPluginPaths = System.getenv(PLUGIN_DEVELOPMENT_PATH.replace('.', '_').toUpperCase());

        if (Strings.isNullOrEmpty(developmentPluginPaths)) {
            developmentPluginPaths = System.getProperty(PLUGIN_DEVELOPMENT_PATH);
        }

        return Strings.isNullOrEmpty(developmentPluginPaths) ? new String[0] : developmentPluginPaths.split(",");
    }
}
