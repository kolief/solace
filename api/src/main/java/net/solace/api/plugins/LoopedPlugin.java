package net.solace.api.plugins;

import net.solace.api.plugins.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoopedPlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(LoopedPlugin.class);
    private boolean stopped;

    @Deprecated(forRemoval=true)
    public void stop() {
        this.stopped = true;
    }

    public abstract int loop();

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public boolean isStopped() {
        return this.stopped;
    }
}

