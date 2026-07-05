package net.solace.api.plugins.exception;

public class PluginStoppedException
extends RuntimeException {
    public PluginStoppedException() {
    }

    public PluginStoppedException(String message) {
        super(message);
    }
}

