package net.solace.loader.plugins.autologin;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.World;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.Static;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.Worlds;
import net.solace.sdk.input.Keyboard;
import net.solace.sdk.script.blocking_events.WelcomeScreenEvent;
import net.solace.sdk.widgets.Widgets;
import org.jboss.aerogear.security.otp.Totp;

@PluginDescriptor(name = "Solace Auto Login")
@Slf4j
public class SolaceAutoLoginPlugin extends LoopedPlugin {
    @Inject
    private SolaceAutoLoginConfig config;

    @Getter
    @Setter
    private int loginAttempts = 0;

    @Getter
    @Setter
    private String shutdownMessage = "";

    @Provides
    public SolaceAutoLoginConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceAutoLoginConfig.class);
    }

    @Override
    public void startUp() {
        setLoginAttempts(0);
        setShutdownMessage("");
        var gameAccount = Game.getGameAccount();
        if (gameAccount != null) {
            if (gameAccount.getAuth() != null) {
                config.auth(gameAccount.getAuth());
            }

            if (gameAccount.isJagexLauncher()) {
                Static.getClient().setSessionId(gameAccount.getUsername());
                Static.getClient().setCharacterId(gameAccount.getPassword());
                Static.getClient().setDisplayName(gameAccount.getDisplayName());
                return;
            }

            Static.getClient().setSessionId(null);
            Static.getClient().setCharacterId(null);
            Static.getClient().setDisplayName(null);
            config.username(gameAccount.getUsername());
            config.password(gameAccount.getPassword());
        }
    }


    @Override
    public int loop() {
        if (!isOnLoginScreen()) {
            return 600;
        }

        if (getLoginAttempts() >= config.maxRetries()) {
            throw new PluginStoppedException("Too many login attempts");
        }

        log.info("Current login attempts: {}", getLoginAttempts());

        if (Client.isWorldSelectOpen()) {
            Client.setWorldSelectOpen(false);
        }

        var sleep = Static.getGameThread().invokeAndWait(() -> handleLoginScreen(Client.getWrapped().getLoginIndex()));

        if (sleep == -999) {
            throw new PluginStoppedException(getShutdownMessage());
        }

        log.info("Sleeping for {} ms", sleep);
        return sleep;
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded e) {
        if (!config.welcomeScreen()) {
            return;
        }

        var group = e.getGroupId();
        if (group == 378 || group == 413) {
            var playButton = WelcomeScreenEvent.getPlayButton();
            if (Widgets.isVisible(playButton)) {
                Client.invokeWidgetAction(1, playButton.getId(), -1, -1, "");
            }
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.LOGGED_IN) {
            setLoginAttempts(0);
        }
    }

    private int handleLoginScreen(int loginIndex) {
        setLoginAttempts(getLoginAttempts() + 1);
        var attemptToSleep = getLoginAttempts() ^ 2;
        switch (loginIndex) {
            case LoginIndex.OAUTH2:
                log.info("OAuth2 login");
                jlLogin();
                return attemptToSleep * 5000;
            case LoginIndex.AUTHENTICATOR:
                log.info("Authenticator login");
                if (!enterAuth()) {
                    return -999;
                }
                return attemptToSleep * 5000;
            case LoginIndex.DISABLED:
                log.info("Account banned");
                setShutdownMessage("Account banned");
                return -999;
            case LoginIndex.INVALID_CREDENTIALS:
                log.info("Invalid credentials");
                setShutdownMessage("Invalid credentials");
                return -999;
            case LoginIndex.MEMBERS_REQUIRED:
                log.info("Members required");
                setShutdownMessage("Members required");
                return -999;
            default:
                log.info("Default login logic");
                prepareLogin();
                enterCredentials();
                return attemptToSleep * 5000;
        }
    }

    private void prepareLogin() {
        if (config.useWorld() && Client.getWorld() != config.world()) {
            World world = Worlds.getFirst(config.world());
            if (world != null) {
                Client.changeWorld(world);
                return;
            }
        } else {
            Client.promptCredentials(false);
        }

        if (Client.isOAuthCredentialsSet()) {
            Client.setLoginIndex(LoginIndex.OAUTH2);
        } else {
            Client.setLoginIndex(LoginIndex.ENTER_CREDENTIALS);
        }
    }

    private void enterCredentials() {
        Client.setNormalLoginMode();
        Client.setUsername(config.username());
        Client.setPassword(config.password());
        Keyboard.sendEnter();
        Keyboard.sendEnter();
    }

    private void jlLogin() {
        Client.setOAuthLoginMode();
        Client.setGameState(GameState.LOGGING_IN);
    }


    private boolean enterAuth() {
        if (config.auth().isBlank()) {
            log.debug("Authenticator code is blank");
            setShutdownMessage("Authenticator code is blank");
            return false;
        }

        Client.setOtp(new Totp(config.auth()).now());
        Client.setGameState(GameState.LOGGING_IN);
        return true;
    }

    private boolean isOnLoginScreen() {
        return Game.getState() == GameState.LOGIN_SCREEN || Game.getState() == GameState.LOGIN_SCREEN_AUTHENTICATOR;
    }
}
