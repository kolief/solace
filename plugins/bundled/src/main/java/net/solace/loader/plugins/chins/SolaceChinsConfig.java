package net.solace.loader.plugins.chins;

import net.runelite.api.coords.WorldPoint;
import net.solace.api.plugins.config.Button;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

import java.util.ArrayList;
import java.util.List;

@ConfigGroup("solacechins")
public interface SolaceChinsConfig extends Config {
    @ConfigItem(
            keyName = "selectTile",
            name = "Select Tiles",
            description = "Enables selection of trap tiles"
    )
    default Button selectTile() {
        return new Button();
    }

    @ConfigItem(
            keyName = "resetTiles",
            name = "Reset Tiles",
            description = "Resets the selected tiles"
    )
    default Button resetTiles() {
        return new Button();
    }

    @ConfigItem(
            keyName = "tiles",
            name = "Tiles",
            description = "The tiles to place traps on",
            hidden = true
    )
    default List<WorldPoint> tiles() {
        return new ArrayList<>();
    }

    @ConfigItem(
            keyName = "tiles",
            name = "Tiles",
            description = "The tiles to place traps on",
            hidden = true
    )
    void tiles(List<WorldPoint> tiles);
}
