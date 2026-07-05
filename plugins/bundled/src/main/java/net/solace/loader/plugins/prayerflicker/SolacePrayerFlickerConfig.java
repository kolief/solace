package net.solace.loader.plugins.prayerflicker;

import net.runelite.client.config.ModifierlessKeybind;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

import java.awt.event.KeyEvent;

@ConfigGroup("solaceprayerflicker")
public interface SolacePrayerFlickerConfig extends Config {
    @ConfigItem(
            keyName = "disable",
            name = "Turn off prayer when toggling",
            description = "",
            position = 1
    )
    default boolean disablePrayer() {
        return true;
    }

    @ConfigItem(
            keyName = "keyBind",
            name = "Toggle Hotkey",
            description = "",
            position = 2
    )
    default ModifierlessKeybind flickToggleKeybind() {
        return new ModifierlessKeybind(KeyEvent.VK_BACK_QUOTE, 0);
    }

    @ConfigItem(
            keyName = "onlyInCombat",
            name = "Only flick in combat",
            description = "",
            position = 3
    )
    default boolean onlyInCombat() {
        return false;
    }
}
