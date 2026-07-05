package net.solace.api.plugins.config;

import net.solace.api.plugins.config.ConfigObject;
import net.solace.api.plugins.config.ConfigTitle;

public final class ConfigTitleDescriptor
implements ConfigObject {
    private final String key;
    private final ConfigTitle title;

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public String name() {
        return this.title.name();
    }

    @Override
    public int position() {
        return this.title.position();
    }

    public ConfigTitleDescriptor(String key, ConfigTitle title) {
        this.key = key;
        this.title = title;
    }

    public String getKey() {
        return this.key;
    }

    public ConfigTitle getTitle() {
        return this.title;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigTitleDescriptor)) {
            return false;
        }
        ConfigTitleDescriptor other = (ConfigTitleDescriptor)o;
        String this$key = this.getKey();
        String other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        ConfigTitle this$title = this.getTitle();
        ConfigTitle other$title = other.getTitle();
        return !(this$title == null ? other$title != null : !this$title.equals(other$title));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        ConfigTitle $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigTitleDescriptor(key=" + this.getKey() + ", title=" + String.valueOf(this.getTitle()) + ")";
    }
}

