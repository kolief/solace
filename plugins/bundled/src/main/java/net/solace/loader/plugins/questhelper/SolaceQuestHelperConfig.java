package net.solace.loader.plugins.questhelper;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

@ConfigGroup("solacequesthelper")
public interface SolaceQuestHelperConfig extends Config {
    @ConfigItem(
            keyName = "skipDialogs",
            name = "Auto complete dialogs",
            description = "Automatically complete dialogs"
    )
    default boolean skipDialogs() {
        return true;
    }

    @ConfigItem(
            keyName = "autoWalk",
            name = "Auto walk to new step",
            description = "Automatically walk to new step when previous step completed"
    )
    default boolean autoWalk() {
        return true;
    }

    @ConfigItem(
            keyName = "talkNpcs",
            name = "Auto talk to NPCs",
            description = "Automatically talks to NPCs"
    )
    default boolean talkNpcs() {
        return false;
    }

    @ConfigItem(
            keyName = "interactObjects",
            name = "Auto interact with objects",
            description = "Automatically interacts with highlighted objects"
    )
    default boolean interactObjects() {
        return false;
    }
}
