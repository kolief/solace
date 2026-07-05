package net.solace.loader.plugins.sailinghotkeys;

import net.runelite.client.config.Keybind;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

@ConfigGroup("solacesailinghotkeys")
public interface SolaceSailingHotkeysConfig extends Config {
    @ConfigItem(
            keyName = "increaseSpeed",
            name = "Increase Speed",
            description = "Increases speed or sets sails",
            position = 2
    )
    default Keybind increaseSpeed() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "decreaseSpeed",
            name = "Decrease Speed",
            description = "Decreases speed or reverses",
            position = 3
    )
    default Keybind decreaseSpeed() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "steerLeft",
            name = "Steer Left",
            description = "Steers the boat left",
            position = 6
    )
    default Keybind steerLeft() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "steerRight",
            name = "Steer Right",
            description = "Steers the boat right",
            position = 7
    )
    default Keybind steerRight() {
        return Keybind.NOT_SET;
    }
}
