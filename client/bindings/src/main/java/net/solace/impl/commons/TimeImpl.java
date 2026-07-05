package net.solace.impl.commons;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.solace.api.commons.ITime;
import net.solace.api.commons.Rand;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IGame;

import java.util.function.BooleanSupplier;

@RequiredArgsConstructor
@Slf4j
public class TimeImpl implements ITime {
    private final IClient client;
    private final IGame game;

    @Override
    public boolean sleep(long ms) {
        if (client.isClientThread()) {
            log.debug("Tried to sleep on client thread!");
            return false;
        }

        try {
            Thread.sleep(ms);
            return true;
        } catch (InterruptedException e) {
            log.debug("Sleep interrupted");
        }

        return false;
    }

    @Override
    public boolean sleep(int min, int max) {
        return sleep(Rand.nextInt(min, max));
    }

    @Override
    public boolean sleepUntil(BooleanSupplier supplier, BooleanSupplier resetSupplier, int pollingRate, int timeOut) {
        if (client.isClientThread()) {
            log.debug("Tried to sleepUntil on client thread!");
            return false;
        }

        long start = System.currentTimeMillis();
        while (!supplier.getAsBoolean()) {
            if (System.currentTimeMillis() > start + timeOut) {
                return false;
            }

            if (resetSupplier.getAsBoolean()) {
                start = System.currentTimeMillis();
            }

            if (!sleep(pollingRate)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean sleepTicks(int ticks) {
        return sleepTicksUntil(() -> false, ticks);
    }

    @Override
    public boolean sleepTicksUntil(BooleanSupplier supplier, int ticks) {
        if (client.isClientThread()) {
            log.debug("Tried to sleep on client thread!");
            return false;
        }

        if (game.getState() == GameState.LOGIN_SCREEN || game.getState() == GameState.LOGIN_SCREEN_AUTHENTICATOR) {
            return false;
        }

        var until = client.getTickCount() + ticks;
        while (client.getTickCount() < until && !supplier.getAsBoolean()) {
            if (!sleep(DEFAULT_POLLING_RATE)) {
                return false;
            }
        }

        return false;
    }
}
