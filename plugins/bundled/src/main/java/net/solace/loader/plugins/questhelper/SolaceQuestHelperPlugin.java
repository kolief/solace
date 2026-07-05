package net.solace.loader.plugins.questhelper;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.task.Schedule;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDependency;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginInstantiationException;
import net.solace.loader.plugins.explorer.exclude.SolaceExplorerPlugin;
import net.solace.loader.plugins.questhelper.util.ReflectionBridge;
import net.solace.sdk.plugins.Plugins;
import net.solace.sdk.utils.MessageUtils;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.getDefinedPoint;
import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.getWorldPointFromDefined;
import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.isConditionalStep;
import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.isDetailedQuestStep;

@Slf4j
@PluginDescriptor(
        name = "Solace Quest Helper",
        description = "Assists with questing",
        tags = {"quest", "helper"}
)
@PluginDependency(SolaceExplorerPlugin.class)
public class SolaceQuestHelperPlugin extends Plugin {
    @Getter
    private Object questHelperPlugin;

    @Inject
    private SolaceQuestHelperConfig config;

    @Inject
    private SolaceExplorerPlugin explorerPlugin;

    @Inject
    private DialogSkipper dialogSkipper;

    @Inject
    private PluginManager rlPluginManager;

    @Inject
    private StepHandler stepHandler;

    @Inject
    private EventBus eventBus;

    @Getter
    private Object currentStep;

    @Getter
    private WorldPoint currentArrow;

    @Setter
    private WorldPoint previousDestination;

    @Override
    public void startUp() throws Exception {
        getQuestHelper();
        tryEnableExplorer();

        eventBus.register(dialogSkipper);
        eventBus.register(stepHandler);
    }

    @Override
    public void shutDown() throws Exception {
        eventBus.unregister(dialogSkipper);
        eventBus.unregister(stepHandler);
    }

    @Schedule(
            period = 300,
            unit = ChronoUnit.MILLIS
    )
    public void loop() {
        if (config.skipDialogs()) {
            dialogSkipper.handleDialogs();
        }
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        var selectedQuest = ReflectionBridge.getSelectedQuest(questHelperPlugin);
        if (selectedQuest == null) {
            return;
        }

        // If destination was reset because it was reached or any other reason, notify StepHandler
        WorldPoint explorerDestination = explorerPlugin.getDestination();
        if (!Objects.equals(explorerDestination, previousDestination)) {
            if (explorerDestination == null) {
                MessageUtils.addMessage("Quest destination was reset.");
                stepHandler.handleQuestStep(currentStep);
            } else {
                MessageUtils.addMessage("Quest destination changed from " + previousDestination + " to " + explorerDestination);
            }

            previousDestination = explorerDestination;
        }

        var currStep = ReflectionBridge.getCurrentStep(selectedQuest);
        if (isConditionalStep(currStep.getClass())) {
            currStep = ReflectionBridge.getActiveStep(currStep);
        }

        if (currStep == null) {
            MessageUtils.addMessage("Quest arrow was reset.");
            currentArrow = null;
        } else if (isDetailedQuestStep(currStep.getClass())) {
            var definedPoint = getDefinedPoint(currStep);
            if (definedPoint != null) {
                var worldPoint = getWorldPointFromDefined(definedPoint);
                if (!Objects.equals(currentArrow, worldPoint)) {
                    MessageUtils.addMessage("Quest arrow changed from " + currentArrow + " to " + worldPoint);
                    currentArrow = worldPoint;

                    stepHandler.handleQuestArrow(currentArrow);
                }
            }
        }

        if (currentStep != currStep) {
            MessageUtils.addMessage("Quest step changed from " + currentStep + " to " + currStep);
            currentStep = currStep;

            stepHandler.handleQuestStep(currentStep);
        }
    }

    @Provides
    SolaceQuestHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceQuestHelperConfig.class);
    }

    private void tryEnableExplorer() throws PluginInstantiationException {
        try {
            if (!Plugins.isEnabled(explorerPlugin)) {
                // we are already on ED thread
                Plugins.startPlugin(explorerPlugin);
            }
        } catch (Exception e) {
            throw new PluginInstantiationException("Failed to enable Explorer plugin");
        }
    }

    private void getQuestHelper() throws PluginInstantiationException {
        try {
            rlPluginManager.getPlugins().stream()
                    .filter(ReflectionBridge::isQuestHelperPlugin)
                    .findFirst()
                    .ifPresent(p -> questHelperPlugin = p);
        } catch (Throwable e) {
            throw new PluginInstantiationException("Failed to get QuestHelperPlugin");
        }
    }
}
