package net.solace.loader.plugins.bankpin;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

@ConfigGroup("solacebankpin")
public interface SolaceBankPinConfig extends Config {
    @ConfigItem(
            keyName = "pin",
            name = "Pin",
            description = "Your bank pin"
    )
    default String pin() {
        return "0000";
    }
}
