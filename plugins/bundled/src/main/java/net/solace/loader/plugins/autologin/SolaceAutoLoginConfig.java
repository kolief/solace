package net.solace.loader.plugins.autologin;

import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;

@ConfigGroup("solaceautologin")
public interface SolaceAutoLoginConfig extends Config {
    @ConfigItem(
            keyName = "username",
            name = "Username",
            description = "Username",
            position = 0
    )
    default String username() {
        return "Username";
    }

    @ConfigItem(
            keyName = "username",
            name = "Username",
            description = "Username",
            position = 0
    )
    void username(String username);

    @ConfigItem(
            keyName = "password",
            name = "Password",
            description = "Password",
            secret = true,
            position = 1
    )
    default String password() {
        return "Password";
    }

    @ConfigItem(
            keyName = "password",
            name = "Password",
            description = "Password",
            secret = true,
            position = 1
    )
    void password(String password);

    @ConfigItem(
            keyName = "auth",
            name = "Authenticator",
            description = "Authenticator",
            secret = true,
            position = 2
    )
    default String auth() {
        return "Authenticator";
    }

    @ConfigItem(
            keyName = "auth",
            name = "Authenticator",
            description = "Authenticator",
            secret = true,
            position = 2
    )
    void auth(String auth);

    @ConfigItem(
            keyName = "useWorld",
            name = "Select world",
            description = "Select world to login to",
            position = 3
    )
    default boolean useWorld() {
        return false;
    }

    @ConfigItem(
            keyName = "world",
            name = "World",
            description = "World Selector",
            position = 4,
            hidden = true,
            unhide = "useWorld"
    )
    default int world() {
        return 301;
    }

    @ConfigItem(
            keyName = "lastWorld",
            name = "Save last world",
            description = "Save last world",
            position = 5,
            hidden = true,
            unhide = "useWorld"
    )
    default boolean lastWorld() {
        return false;
    }

    @ConfigItem(
            keyName = "welcomeScreen",
            name = "Complete Welcome screen",
            description = "Automatically presses the 'Click here to Play' button after login",
            position = 6
    )
    default boolean welcomeScreen() {
        return false;
    }

    @ConfigItem(
            keyName = "maxRetries",
            name = "Max retries",
            description = "Max retries before giving up",
            position = 7
    )
    default int maxRetries() {
        return 30;
    }
}
