package net.solace.api.events;

import net.solace.api.plugins.Plugin;

public final class PluginChanged {
    private final Plugin plugin;
    private final boolean loaded;

    public PluginChanged(Plugin plugin, boolean loaded) {
        this.plugin = plugin;
        this.loaded = loaded;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PluginChanged)) {
            return false;
        }
        PluginChanged other = (PluginChanged)o;
        if (this.isLoaded() != other.isLoaded()) {
            return false;
        }
        Plugin this$plugin = this.getPlugin();
        Plugin other$plugin = other.getPlugin();
        return !(this$plugin == null ? other$plugin != null : !((Object)this$plugin).equals(other$plugin));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isLoaded() ? 79 : 97);
        Plugin $plugin = this.getPlugin();
        result = result * 59 + ($plugin == null ? 43 : ((Object)$plugin).hashCode());
        return result;
    }

    public String toString() {
        return "PluginChanged(plugin=" + String.valueOf(this.getPlugin()) + ", loaded=" + this.isLoaded() + ")";
    }
}

