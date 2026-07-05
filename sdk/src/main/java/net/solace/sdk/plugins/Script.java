package net.solace.sdk.plugins;

import lombok.Getter;
import net.runelite.api.GameState;
import net.solace.api.plugins.IScript;
import net.solace.sdk.game.Game;
import net.solace.sdk.script.blocking_events.BlockingEvent;
import net.solace.sdk.script.blocking_events.BlockingEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Script extends LoopedPlugin implements IScript {
    protected final Logger logger;
    @Getter
    private final BlockingEventManager blockingEventManager = new BlockingEventManager();
    private boolean restart;
    private boolean paused;
    private boolean onLogin;

    public Script() {
        logger = LoggerFactory.getLogger(getClass());
    }

    public abstract int loop();

    public abstract void onStart(String... args);

    public void onStop() {

    }

    public void onLogin() {

    }

    public int outerLoop() {
        int loopSleep;
        if (paused) {
            return 1000;
        }

        if (restart) {
            restart = false;
            return 1000;
        }

        if (Game.getState() == GameState.LOGGED_IN && !onLogin) {
            onLogin = true;
            onLogin();
            return 100;
        }

        if (!blockingEventManager.getBlockingEvents().isEmpty()) {
            for (BlockingEvent event : blockingEventManager.getBlockingEvents()) {
                if (event.validate()) {
                    return event.loop();
                }
            }
        }

        loopSleep = loop();
        return loopSleep != 0 ? loopSleep : 1000;
    }

    public void pauseScript() {
        paused = !paused;
    }

    public boolean isRestart() {
        return restart;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
