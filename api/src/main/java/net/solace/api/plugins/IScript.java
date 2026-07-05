package net.solace.api.plugins;

public interface IScript extends ILoopedPlugin {
    void onStop();

    void onLogin();
}
