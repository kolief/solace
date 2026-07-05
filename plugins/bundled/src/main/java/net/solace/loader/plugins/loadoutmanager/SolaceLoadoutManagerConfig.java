package net.solace.loader.plugins.loadoutmanager;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

import static net.solace.loader.plugins.loadoutmanager.SolaceLoadoutManagerConfig.CONFIG_GROUP;

@ConfigGroup(CONFIG_GROUP)
public interface SolaceLoadoutManagerConfig extends Config {
    String CONFIG_GROUP = "solaceloadoutmanager";

    @ConfigItem(
            keyName = "loadoutConfigKeys",
            name = "",
            description = "",
            hidden = true
    )
    default String loadoutConfigKeys() {
        return "";
    }

    @ConfigItem(
            keyName = "loadoutConfigKeys",
            name = "",
            description = "",
            hidden = true
    )
    void loadoutConfigKeys(String keys);
}
