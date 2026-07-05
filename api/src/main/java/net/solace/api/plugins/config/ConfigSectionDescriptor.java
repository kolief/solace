package net.solace.api.plugins.config;

import net.solace.api.plugins.config.ConfigObject;
import net.solace.api.plugins.config.ConfigSection;

public final class ConfigSectionDescriptor
implements ConfigObject {
    private final String key;
    private final ConfigSection section;

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public String name() {
        return this.section.name();
    }

    @Override
    public int position() {
        return this.section.position();
    }

    public ConfigSectionDescriptor(String key, ConfigSection section) {
        this.key = key;
        this.section = section;
    }

    public String getKey() {
        return this.key;
    }

    public ConfigSection getSection() {
        return this.section;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigSectionDescriptor)) {
            return false;
        }
        ConfigSectionDescriptor other = (ConfigSectionDescriptor)o;
        String this$key = this.getKey();
        String other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        ConfigSection this$section = this.getSection();
        ConfigSection other$section = other.getSection();
        return !(this$section == null ? other$section != null : !this$section.equals(other$section));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        ConfigSection $section = this.getSection();
        result = result * 59 + ($section == null ? 43 : $section.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigSectionDescriptor(key=" + this.getKey() + ", section=" + String.valueOf(this.getSection()) + ")";
    }
}

