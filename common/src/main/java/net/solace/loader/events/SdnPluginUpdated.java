package net.solace.loader.events;

import lombok.Value;
import org.pf4j.update.PluginInfo;

@Value
public class SdnPluginUpdated {
    PluginInfo pluginInfo;
    String lastVersion;
}
