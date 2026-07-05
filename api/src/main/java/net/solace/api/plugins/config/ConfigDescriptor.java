package net.solace.api.plugins.config;

import java.util.Collection;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItemDescriptor;
import net.solace.api.plugins.config.ConfigSectionDescriptor;
import net.solace.api.plugins.config.ConfigTitleDescriptor;

public class ConfigDescriptor {
    private final ConfigGroup group;
    private final Collection<ConfigSectionDescriptor> sections;
    private final Collection<ConfigTitleDescriptor> titles;
    private final Collection<ConfigItemDescriptor> items;

    public ConfigGroup getGroup() {
        return this.group;
    }

    public Collection<ConfigSectionDescriptor> getSections() {
        return this.sections;
    }

    public Collection<ConfigTitleDescriptor> getTitles() {
        return this.titles;
    }

    public Collection<ConfigItemDescriptor> getItems() {
        return this.items;
    }

    public ConfigDescriptor(ConfigGroup group, Collection<ConfigSectionDescriptor> sections, Collection<ConfigTitleDescriptor> titles, Collection<ConfigItemDescriptor> items) {
        this.group = group;
        this.sections = sections;
        this.titles = titles;
        this.items = items;
    }
}

