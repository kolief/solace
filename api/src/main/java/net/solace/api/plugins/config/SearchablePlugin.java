package net.solace.api.plugins.config;

import java.util.List;

public interface SearchablePlugin {
    public String getSearchableName();

    public List<String> getKeywords();

    default public boolean isPinned() {
        return false;
    }

    default public int installs() {
        return 0;
    }
}

