package net.solace.loader.plugins.explorer;

import net.runelite.client.config.Keybind;
import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.api.plugins.config.Button;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.loader.plugins.explorer.util.Category;

@ConfigGroup("solaceexplorer")
public interface SolaceExplorerConfig extends Config {

    @ConfigItem(
            keyName = "categories",
            name = "Categories",
            description = "Predefined location categories",
            position = 0
    )
    default Category category() {
        return Category.QUEST;
    }

    @ConfigItem(
            keyName = "bankLocations",
            name = "Banks",
            description = "Walk to the specified bank",
            position = 5,
            hidden = true,
            unhide = "categories",
            unhideValue = "BANKS"
    )
    default BankLocation bankLocation() {
        return BankLocation.AL_KHARID_BANK;
    }

    @ConfigItem(
            keyName = "customcoords",
            name = "Custom coords",
            description = "Walk to the specified coordinates",
            position = 10,
            hidden = true,
            unhide = "categories",
            unhideValue = "CUSTOM"
    )
    default String coords() {
        return "3220 3220 0";
    }


    @ConfigItem(
            keyName = "toggleKeyBind",
            name = "Start/Stop hotkey",
            description = "Hotkey to start/stop the explorer",
            position = 24
    )
    default Keybind toggleKeyBind() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "closeMap",
            name = "Close map on selection",
            description = "Close the world map after selecting a destination",
            position = 25
    )
    default boolean closeMap() {
        return true;
    }

    @ConfigItem(
            keyName = "showOverlay",
            name = "Show overlay",
            description = "Show the overlay",
            position = 26
    )
    default boolean showOverlay() {
        return true;
    }

    @ConfigItem(
            keyName = "walk",
            name = "Walk to",
            description = "Walk to set coordinates",
            position = 27
    )
    default Button walk() {
        return new Button();
    }
}
