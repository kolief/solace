package net.solace.loader;

import com.google.inject.Module;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.solace.api.Static;
import net.solace.impl.ApiModule;
import net.solace.loader.modules.ExternalPluginsModule;
import net.solace.loader.modules.InteractionModule;
import net.solace.loader.modules.LoaderModule;
import net.solace.ui.module.UiModule;

@Slf4j
public class SolaceLoader {
    public static void inject() {
        try {
            var loaderModule = new LoaderModule();
            start(
                    new ApiModule(),
                    loaderModule,
                    new InteractionModule(),
                    new ExternalPluginsModule(loaderModule.getScript()),
                    new UiModule()
            );
        } catch (Exception e) {
            log.error("An error occurred during launch.", e);
            SolaceLauncher.showFatal("Solace failed to inject into RuneLite.", e);
        }
    }

    public static void start(Module... modules) throws Exception {
        var rlInjector = RuneLite.getInjector();
        var injector = Static.injector = rlInjector.createChildInjector(modules);
        var instance = injector.getInstance(SolaceManager.class);
        instance.start();
    }
}
