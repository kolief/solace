package net.solace.impl.game;

import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.game.GameStateManager;

import java.time.Instant;

public class GameStateManagerImpl implements GameStateManager {
    private GameState previousState = GameState.LOGIN_SCREEN;

    @Getter
    private Instant lastLogin = Instant.now();

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if ((previousState == GameState.LOGIN_SCREEN || previousState == GameState.HOPPING)
                && event.getGameState() == GameState.LOGGED_IN) {

            lastLogin = Instant.now();
            previousState = event.getGameState();
        }

        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
            previousState = event.getGameState();
        }
    }
}