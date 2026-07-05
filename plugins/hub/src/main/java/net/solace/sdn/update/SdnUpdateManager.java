package net.solace.sdn.update;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.pf4j.PluginRuntimeException;
import org.pf4j.PluginState;
import org.pf4j.VersionManager;
import org.pf4j.update.FileDownloader;
import org.pf4j.update.FileVerifier;
import org.pf4j.update.PluginInfo;
import org.pf4j.update.verifier.CompoundVerifier;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
public class SdnUpdateManager implements UpdateManager {
    private final PluginManager pluginManager;
    private final VersionManager versionManager;
    private final String systemVersion;

    protected final SdnRepository repository;

    public SdnUpdateManager(PluginManager pluginManager, SdnRepository repository) {
        this.pluginManager = pluginManager;
        this.versionManager = pluginManager.getVersionManager();
        this.systemVersion = pluginManager.getSystemVersion();
        this.repository = repository;
    }

    public List<PluginInfo> getAvailablePlugins() {
        List<PluginInfo> availablePlugins = new ArrayList<>();
        for (var plugin : getPlugins()) {
            if (pluginManager.getPlugin(plugin.id) == null) {
                availablePlugins.add(plugin);
            }
        }

        return availablePlugins;
    }

    /**
     * Return a list of plugins that are newer versions of already installed plugins.
     *
     * @return list of plugins that have updates
     */
    public List<PluginInfo> getUpdates() {
        List<PluginInfo> updates = new ArrayList<>();
        for (var installed : pluginManager.getPlugins()) {
            var pluginId = installed.getPluginId();
            if (hasPluginUpdate(pluginId)) {
                updates.add(getPluginsMap().get(pluginId));
            }
        }

        return updates;
    }

    @SuppressWarnings("UnstableApiUsage")
    public boolean hashMismatch(String id) {
        var plugin = pluginManager.getPlugin(id);
        if (plugin == null) {
            return true;
        }

        var pluginPath = plugin.getPluginPath();
        if (pluginPath == null) {
            return true;
        }

        var latestRelease = getLastPluginRelease(id);
        if (latestRelease == null) {
            return false;
        }

        try (var fis = Files.newInputStream(pluginPath);
             var his = new HashingInputStream(Hashing.sha512(), fis)) {
            // Read the stream to compute the hash
            ByteStreams.exhaust(his);

            return !Objects.equals(his.hash().toString().toUpperCase(), latestRelease.sha512sum.toUpperCase());
        } catch (IOException e) {
            log.debug("Error hashing plugin {}", id, e);
            return true;
        }
    }

    /**
     * Checks if Update Repositories has newer versions of some of the installed plugins.
     *
     * @return true if updates exist
     */
    public boolean hasUpdates() {
        return !getUpdates().isEmpty();
    }

    /**
     * Get the list of plugins from all repos.
     *
     * @return List of plugin info
     */
    public List<PluginInfo> getPlugins() {
        List<PluginInfo> list = new ArrayList<>(getPluginsMap().values());
        Collections.sort(list);

        return list;
    }

    /**
     * Get a map of all plugins from all repos where key is plugin id.
     *
     * @return List of plugin info
     */
    public Map<String, PluginInfo> getPluginsMap() {
        return repository.getPlugins();
    }

    /**
     * Installs a plugin by id and version.
     *
     * @param id      the id of plugin to install
     * @param version the version of plugin to install, on SemVer format, or null for latest
     * @return true if installation successful and plugin started
     * @throws PluginRuntimeException if plugin does not exist in repos or problems during
     */
    public synchronized boolean installPlugin(String id, String version) throws IOException {
        // Download to temporary location
        var downloaded = downloadPlugin(id, version);

        var pluginsRoot = pluginManager.getPluginsRoot();
        var file = pluginsRoot.resolve(downloaded.getFileName());


        try {
            Files.move(downloaded, file, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to write file '" + file + "' to the plugins folder", e);
        }

        var pluginId = pluginManager.loadPlugin(file);

        var state = pluginManager.startPlugin(pluginId);

        return PluginState.STARTED.equals(state);
    }

    /**
     * Downloads a plugin with given coordinates, runs all {@link FileVerifier}s
     * and returns a path to the downloaded file.
     *
     * @param id      of plugin
     * @param version of plugin or null to download latest
     * @return Path to file which will reside in a temporary folder in the system default temp area
     * @throws PluginRuntimeException if download failed
     */
    public Path downloadPlugin(String id, String version) {
        try {
            var release = findReleaseForPlugin(id, version);
            var downloaded = getFileDownloader().downloadFile(new URL(release.url));
            getFileVerifier().verify(new FileVerifier.Context(id, release), downloaded);
            return downloaded;
        } catch (IOException e) {
            throw new PluginRuntimeException(e, "Error during download of plugin {}", id);
        }
    }

    /**
     * Finds the {@link FileDownloader} to use for this repository.
     *
     * @return FileDownloader instance
     */
    public FileDownloader getFileDownloader() {
        return repository.getFileDownloader();
    }

    /**
     * Gets a file verifier to use for this plugin. First tries to use custom verifier
     * configured for the repository, then fallback to the default {@link CompoundVerifier}
     *
     * @return FileVerifier instance
     */
    public FileVerifier getFileVerifier() {
        return repository.getFileVerifier();
    }

    /**
     * Resolves Release from id and version.
     *
     * @param id      of plugin
     * @param version of plugin or null to locate latest version
     * @return PluginRelease for downloading
     * @throws PluginRuntimeException if id or version does not exist
     */
    protected SdnPluginInfo.PluginRelease findReleaseForPlugin(String id, String version) {
        var pluginInfo = getPluginsMap().get(id);
        if (pluginInfo == null) {
            log.info("Plugin with id {} does not exist in any repository", id);
            throw new PluginRuntimeException("Plugin with id {} not found in any repository", id);
        }

        if (version == null) {
            return getLastPluginRelease(id);
        }

        for (var release : pluginInfo.releases) {
            if (versionManager.compareVersions(version, release.version) == 0 && release.url != null) {
                return release;
            }
        }

        throw new PluginRuntimeException("Plugin {} with version @{} does not exist in the repository", id, version);
    }

    /**
     * Updates a plugin id to given version or to latest version if {@code version == null}.
     *
     * @param version the version to update to, on SemVer format, or null for latest
     * @return true if update successful
     * @throws PluginRuntimeException in case the given version is not available, plugin id not already installed etc
     */
    public boolean updatePlugin(PluginInfo pluginInfo, String version) {
        var id = pluginInfo.id;
        if (pluginManager.getPlugin(id) == null) {
            throw new PluginRuntimeException("Plugin {} cannot be updated since it is not installed", id);
        }

        // Download to temp folder
        var downloaded = downloadPlugin(id, version);

        if (!pluginManager.deletePlugin(id)) {
            log.warn("Failed to delete plugin while updating {}", id);
            return false;
        }

        var pluginsRoot = pluginManager.getPluginsRoot();
        var file = pluginsRoot.resolve(downloaded.getFileName());

        try {
            Files.move(downloaded, file, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PluginRuntimeException("Failed to write plugin file {} to plugin folder", file);
        }

        var newPluginId = pluginManager.loadPlugin(file);
        var state = pluginManager.startPlugin(newPluginId);

        return PluginState.STARTED.equals(state);
    }

    /**
     * Returns the last release version of this plugin for given system version, regardless of release date.
     *
     * @return PluginRelease which has the highest version number
     */
    public SdnPluginInfo.PluginRelease getLastPluginRelease(String id) {
        var pluginInfo = getPluginsMap().get(id);
        if (pluginInfo == null) {
            return null;
        }

        var lastPluginRelease = new HashMap<String, SdnPluginInfo.PluginRelease>();
        for (var release : pluginInfo.releases) {
            if (systemVersion.equals("0.0.0") || versionManager.checkVersionConstraint(systemVersion, release.requires)) {
                if (lastPluginRelease.get(id) == null) {
                    lastPluginRelease.put(id, release);
                } else if (versionManager.compareVersions(release.version, lastPluginRelease.get(id).version) > 0) {
                    lastPluginRelease.put(id, release);
                }
            }
        }

        return lastPluginRelease.get(id);
    }

    /**
     * Finds whether the newer version of the plugin.
     *
     * @return true if there is a newer version available which is compatible with system
     */
    public boolean hasPluginUpdate(String id) {
        var pluginInfo = getPluginsMap().get(id);
        if (pluginInfo == null) {
            return false;
        }

        var installedVersion = pluginManager.getPlugin(id).getDescriptor().getVersion();
        var last = getLastPluginRelease(id);

        return last != null
               && (versionManager.compareVersions(last.version, installedVersion) > 0 || hashMismatch(id));
    }
}
