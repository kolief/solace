package net.solace.api.plugins;

import com.google.inject.Injector;

public interface IPlugin {
    Injector getInjector();

    void startUp() throws Exception;

    void shutDown() throws Exception;
}
