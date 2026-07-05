package net.solace.loader.plugins.chartershipper;


import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigTitle;

@ConfigGroup("solacechartershipper")
public interface SolaceCharterShipperConfig extends Config {
    @ConfigTitle(
            name = "Requirements:",
            description = "",
            position = 0
    )
    String reqs = "Requirements:";

    @ConfigTitle(
            name = "- Superglass make runes",
            description = "",
            position = 1,
            title = reqs
    )
    String one = "- Superglass make runes";

    @ConfigTitle(
            name = "- Coins",
            description = "",
            position = 2,
            title = reqs
    )
    String two = "- Coins";

    @ConfigTitle(
            name = "- Glassblowing pipe",
            description = "",
            position = 3,
            title = reqs
    )
    String three = "- Glassblowing pipe";

    @ConfigTitle(
            name = "Start near a charter ship",
            description = "",
            position = 4,
            title = reqs
    )
    String four = "Start near a charter ship";

    @ConfigItem(
            keyName = "item",
            name = "Item",
            description = "Item to glass blow",
            position = 5
    )
    default GlassBlowItem item() {
        return GlassBlowItem.LANTERN_LENS;
    }
}
