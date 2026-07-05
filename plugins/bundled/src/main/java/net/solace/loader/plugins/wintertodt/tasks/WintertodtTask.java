package net.solace.loader.plugins.wintertodt.tasks;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.solace.api.plugins.Task;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.sdk.game.Vars;

@RequiredArgsConstructor
public abstract class WintertodtTask implements Task {
    private static final int MAX_WARMTH = 1000;
    private static final int WARMTH_VARBIT = 11434;

    @Delegate
    private final SolaceWintertodtPlugin context;
    protected int waitUntil = 0;

    protected void setCooldown(int ticks) {
        waitUntil = getClient().getTickCount() + ticks;
    }

    protected int getWarmthPercent() {
        return Vars.getBit(WARMTH_VARBIT) * 100 / MAX_WARMTH;
    }
}
