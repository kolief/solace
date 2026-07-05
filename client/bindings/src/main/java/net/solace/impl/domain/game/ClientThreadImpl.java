package net.solace.impl.domain.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.game.IClientThread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Slf4j
public class ClientThreadImpl implements IClientThread {
    private static final long TIMEOUT = 1000;
    @Getter
    private final ClientThread wrapped;
    private final IClient client;

    @Override
    public void invoke(Runnable runnable) {
        if (client.isClientThread()) {
            runnable.run();
        } else {
            wrapped.invokeLater(runnable);
        }
    }

    @Override
    public <T> T invokeAndWait(Callable<T> callable) {
        if (client.isClientThread()) {
            try {
                return callable.call();
            } catch (Exception e) {
                log.error("Error invoking callable", e);
            }
        }

        try {
            FutureTask<T> futureTask = new FutureTask<>(callable);
            wrapped.invokeLater(futureTask);
            return futureTask.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.error("Error invoking callable", e);
            throw new RuntimeException("Client thread invoke timed out after " + TIMEOUT + " ms");
        } catch (Exception e) {
            log.error("Error invoking callable", e);
            throw new RuntimeException("Client thread invoke failed");
        }
    }
}
