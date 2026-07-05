package net.solace.loader.plugins.cannonballer;


import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigTitle;

@ConfigGroup("solacecannonballer")
public interface SolaceCannonballerConfig extends Config {
    @ConfigTitle(
            name = "Instructions",
            description = "",
            position = 0
    )
    String instructions = "Instructions";

    @ConfigTitle(
            name = "1. Start the plugin",
            description = "",
            position = 1,
            title = instructions
    )
    String startPlugin = "1. Start the plugin";

    @ConfigTitle(
            name = "2. Right click an NPC/Object",
            description = "",
            position = 2,
            title = instructions
    )
    String rightClick = "2. Right click an NPC/Object";

    @ConfigTitle(
            name = "3. Select a Bank & Furnace",
            description = "",
            position = 3,
            title = instructions
    )
    String furnace = "3. Select a Bank & Furnace";

    @ConfigTitle(
            name = "4. Plugin will now start",
            description = "",
            position = 3,
            title = instructions
    )
    String start = "4. Plugin will now start";

    @ConfigItem(
            keyName = "mould",
            name = "Mould type",
            description = "Type of mould to use",
            position = 0
    )
    default Mould mould() {
        return Mould.NORMAL_MOULD;
    }
}
