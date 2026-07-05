package net.solace.sdk.script.blocking_events;

import net.runelite.api.GameState;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Game;
import net.solace.sdk.script.blocking_events.BlockingEvent;

public class ResizableEvent
extends BlockingEvent {
    @Override
    public boolean validate() {
        if (Game.getState() != GameState.LOGGED_IN) {
            return false;
        }
        return Client.getWindowedMode() != 1;
    }

    @Override
    public int loop() {
        if (Client.getWindowedMode() != 1) {
            Client.setWindowedMode(0);
            return 1000;
        }
        return 1000;
    }
}

