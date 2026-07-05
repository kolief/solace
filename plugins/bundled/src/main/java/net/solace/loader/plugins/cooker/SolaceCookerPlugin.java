package net.solace.loader.plugins.cooker;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Skill;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.Task;
import net.solace.api.plugins.TaskPlugin;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.loader.plugins.cooker.tasks.Cook;
import net.solace.sdk.game.Skills;

@PluginDescriptor(
        name = "Solace Cooker"
)
public class SolaceCookerPlugin extends TaskPlugin {
    private final Task[] tasks =
            {
                    new Cook(this)
            };

    @Inject
    @Getter
    private SolaceCookerConfig config;

    @Inject
    private BreakHandler breakHandler;

    @Override
    public void startUp() throws Exception {
        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
    }

    @Override
    public void shutDown() throws Exception {
        breakHandler.unregisterPlugin(this);
        breakHandler.stopPlugin(this);
    }

    @Override
    public Task[] getTasks() {
        return tasks;
    }

    @Override
    public int loop() {
        if (breakHandler.isBreakActive(this)) {
            return 1000;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return -1;
        }

        if (Skills.getLevel(Skill.COOKING) >= config.stopAtLevel()) {
            throw new PluginStoppedException("Reached target level");
        }

        return super.loop();
    }

    @Provides
    SolaceCookerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceCookerConfig.class);
    }
}