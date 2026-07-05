package net.solace.loader.plugins.cooker.tasks;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.solace.api.plugins.Task;
import net.solace.loader.plugins.cooker.SolaceCookerPlugin;

@RequiredArgsConstructor
public abstract class CookerTask implements Task {
    @Delegate
    private final SolaceCookerPlugin context;

    protected int taskCooldown;
}
