package net.solace.api.plugins;

import org.apache.commons.lang3.StringUtils;

public final class PluginMetaData {
    private final String pluginId;
    private final String pluginName;
    private final String version;
    private final Long subscriptionId;
    private final String commitHash;
    private final boolean sdn;

    public PluginMetaData(String pluginId, String pluginName, String version, Long subscriptionId, String commitHash) {
        this.pluginId = pluginId;
        this.pluginName = pluginName;
        this.version = version;
        this.subscriptionId = subscriptionId;
        this.commitHash = commitHash;
        this.sdn = StringUtils.isNumeric((CharSequence)pluginId);
    }

    public Long getId() {
        return Long.valueOf(this.pluginId);
    }

    public String getPluginId() {
        return this.pluginId;
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public String getVersion() {
        return this.version;
    }

    public Long getSubscriptionId() {
        return this.subscriptionId;
    }

    public String getCommitHash() {
        return this.commitHash;
    }

    public boolean isSdn() {
        return this.sdn;
    }
}

