package net.solace.api.plugins.config;

import java.util.List;
import javax.annotation.Nullable;

public class ConfigProfile {
    private final long id;
    private String name;
    private boolean sync;
    private boolean active;
    private long rev;
    @Nullable
    private List<String> defaultForRsProfiles;

    public boolean isInternal() {
        return this.name.startsWith("$");
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSync() {
        return this.sync;
    }

    public boolean isActive() {
        return this.active;
    }

    public long getRev() {
        return this.rev;
    }

    @Nullable
    public List<String> getDefaultForRsProfiles() {
        return this.defaultForRsProfiles;
    }

    public ConfigProfile(long id) {
        this.id = id;
    }

    public String toString() {
        return "ConfigProfile(id=" + this.getId() + ", name=" + this.getName() + ", sync=" + this.isSync() + ", active=" + this.isActive() + ", rev=" + this.getRev() + ", defaultForRsProfiles=" + String.valueOf(this.getDefaultForRsProfiles()) + ")";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRev(long rev) {
        this.rev = rev;
    }

    public void setDefaultForRsProfiles(@Nullable List<String> defaultForRsProfiles) {
        this.defaultForRsProfiles = defaultForRsProfiles;
    }
}

