package net.solace.sdk.game;

import java.util.concurrent.Callable;
import net.solace.api.Static;
import net.solace.api.domain.game.IClientThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameThread {
    private static final Logger log = LoggerFactory.getLogger(GameThread.class);
    private static final IClientThread CLIENT_THREAD = Static.getGameThread();

    public static void invoke(Runnable runnable) {
        CLIENT_THREAD.invoke(runnable);
    }

    public static <T> T invokeAndWait(Callable<T> callable) {
        return (T)CLIENT_THREAD.invokeAndWait(callable);
    }

    public static void invokeAndWait(Runnable runnable) {
        GameThread.invokeAndWait(() -> {
            runnable.run();
            return null;
        });
    }
}

