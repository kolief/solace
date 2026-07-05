package net.solace.api.game;

import java.time.Instant;
import net.runelite.api.GameState;
import net.solace.api.account.GameAccount;

public interface IGame {
    public GameAccount getGameAccount();

    public void setGameAccount(GameAccount var1);

    public boolean isLoggedIn();

    public GameState getState();

    public boolean isInCutscene();

    public int getWildyLevel();

    public boolean isInWilderness();

    default public boolean isOnLoginScreen() {
        return this.getState() == GameState.LOGIN_SCREEN || this.getState() == GameState.LOGIN_SCREEN_AUTHENTICATOR || this.getState() == GameState.LOGGING_IN;
    }

    public int getDeadmanLevel();

    public int getMembershipDays();

    public boolean isBlackScreen();

    public void logout();

    public Instant getLastLogin();
}

