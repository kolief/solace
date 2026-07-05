package net.solace.loader.events;

import lombok.Data;
import net.solace.api.plugins.Plugin;

@Data
public class SdnPluginChanged {
    private final String pluginId;
    private final Plugin plugin;
    private final boolean added;
}
