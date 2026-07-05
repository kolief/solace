package net.solace.api.plugins;

public interface ILoopedPlugin extends IPlugin, Runnable {
    void stop();

    boolean isRunning();
}
