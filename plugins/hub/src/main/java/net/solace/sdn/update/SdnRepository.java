package net.solace.sdn.update;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.update.FileDownloader;
import org.pf4j.update.FileVerifier;
import org.pf4j.update.PluginInfo;
import org.pf4j.update.verifier.CompoundVerifier;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SdnRepository implements org.pf4j.update.UpdateRepository {
    private final String id;

    @Getter
    private final Map<String, PluginInfo> plugins = new HashMap<>();
    @Getter
    private final List<String> allowedHashes = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public PluginInfo getPlugin(String id) {
        return getPlugins().get(id);
    }

    public void addPlugin(SdnPluginInfo sdnPluginInfo) {
        sdnPluginInfo.setRepositoryId(getId());
        plugins.put(sdnPluginInfo.id, sdnPluginInfo);
    }

    public void addHash(String hash) {
        allowedHashes.add(hash);
    }

    @Override
    public FileDownloader getFileDownloader() {
        return new SdnDownloader();
    }

    /**
     * Causes {@code plugins.json} to be read again to look for new updates from repositories.
     */
    @Override
    public void refresh() {
        plugins.clear();
    }

    /**
     * Gets a file verifier to execute on the downloaded file for it to be claimed valid.
     * May be a CompoundVerifier in order to chain several verifiers.
     *
     * @return list of {@link FileVerifier}s
     */
    @Override
    public FileVerifier getFileVerifier() {
        return new CompoundVerifier();
    }
}
