package net.solace.sdk.game;

import java.time.Instant;
import net.runelite.api.GameState;
import net.solace.api.Static;
import net.solace.api.account.GameAccount;
import net.solace.api.game.IGame;

public class Game {
    private static final IGame GAME = Static.getGame();

    public static GameAccount getGameAccount() {
        return GAME.getGameAccount();
    }

    public static void setGameAccount(GameAccount gameAccount) {
        GAME.setGameAccount(gameAccount);
    }

    public static boolean isLoggedIn() {
        return GAME.isLoggedIn();
    }

    public static GameState getState() {
        return GAME.getState();
    }

    public static boolean isInWilderness() {
        return GAME.isInWilderness();
    }

    public static int getWildyLevel() {
        return GAME.getWildyLevel();
    }

    public static boolean isInCutscene() {
        return GAME.isInCutscene();
    }

    public static boolean isOnLoginScreen() {
        return Game.getState() == GameState.LOGIN_SCREEN || Game.getState() == GameState.LOGIN_SCREEN_AUTHENTICATOR || Game.getState() == GameState.LOGGING_IN;
    }

    public static int getDeadmanLevel() {
        return GAME.getDeadmanLevel();
    }

    public static int getMembershipDays() {
        return GAME.getMembershipDays();
    }

    public static boolean isBlackScreen() {
        return GAME.isBlackScreen();
    }

    public static void logout() {
        GAME.logout();
    }

    public static Instant getLastLogin() {
        return GAME.getLastLogin();
    }
}

