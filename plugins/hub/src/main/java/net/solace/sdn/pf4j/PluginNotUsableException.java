package net.solace.sdn.pf4j;

import org.pf4j.PluginRuntimeException;

public class PluginNotUsableException extends PluginRuntimeException {
    public PluginNotUsableException(String message) {
        super(message);
    }
}
