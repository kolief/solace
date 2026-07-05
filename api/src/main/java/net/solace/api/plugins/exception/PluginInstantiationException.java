package net.solace.api.plugins.exception;

public class PluginInstantiationException
extends Exception {
    public PluginInstantiationException(String message) {
        super(message);
    }

    public PluginInstantiationException(Throwable cause) {
        super(cause);
    }
}

