package net.solace.api.plugins;

import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.Task;

public abstract class PluginTask<T extends LoopedPlugin>
implements Task {
    private final T context;

    public PluginTask(T context) {
        this.context = context;
    }

    public T getContext() {
        return this.context;
    }
}

