package net.solace.loader.plugins.birdhouses;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.Range;
import net.solace.loader.plugins.birdhouses.model.BirdHouseType;
import net.solace.loader.plugins.birdhouses.model.SeedType;

@ConfigGroup("solacebirdhouses")
public interface SolaceBirdHousesConfig extends Config {
    @ConfigItem(
            keyName = "type",
            name = "Birdhouse Type",
            description = ""
    )
    default BirdHouseType type() {
        return BirdHouseType.NORMAL;
    }

    @ConfigItem(
            keyName = "seedType",
            name = "Seed Type",
            description = ""
    )
    default SeedType seedType() {
        return SeedType.BARLEY_SEED;
    }

    @ConfigItem(
            keyName = "logout",
            name = "Log out when idle",
            description = ""
    )
    default boolean logout() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "drinkStamina",
            name = "Drink Stamina Potions",
            description = "Enable drinking staminas"
    )
    default boolean drinkStamina() {
        return false;
    }

    @Range(max = 100)
    @ConfigItem(
            position = 2,
            keyName = "minimumEnergy",
            name = "Minimum Energy",
            description = "The minimum energy level before drinking stamina potion",
            hidden = true,
            unhide = "drinkStamina"
    )
    default int minimumEnergy() {
        return 50;
    }

    @ConfigItem(
            position = 3,
            keyName = "username",
            name = "Username",
            description = "The username to log in with"
    )
    default String username() {
        return "";
    }

    @ConfigItem(
            position = 4,
            keyName = "password",
            name = "Password",
            description = "The password to log in with",
            secret = true
    )
    default String password() {
        return "";
    }
}
