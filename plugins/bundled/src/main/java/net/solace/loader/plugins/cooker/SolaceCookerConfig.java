package net.solace.loader.plugins.cooker;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

@ConfigGroup("solacecooker")
public interface SolaceCookerConfig extends Config {
    @ConfigItem(
            keyName = "item",
            name = "Item",
            description = "",
            position = 0
    )
    default Meat item() {
        return Meat.KARAMBWAN;
    }

    @ConfigItem(
            keyName = "stopAtLevel",
            name = "Stop at level",
            description = "Stop at this level",
            position = 1

    )
    default int stopAtLevel() {
        return 99;
    }
}
