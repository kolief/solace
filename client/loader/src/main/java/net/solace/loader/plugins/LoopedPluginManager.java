package net.solace.loader.plugins;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.EventBus;
import net.solace.api.commons.ITime;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IGame;
import net.solace.api.plugins.IPlugins;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.Task;
import net.solace.api.plugins.TaskPlugin;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoopedPluginManager {
    private final Map<LoopedPlugin, LoopedPluginExecutor<?>> loopedPlugins = new HashMap<>();

    private final EventBus eventBus;
    private final IGame game;
    private final IPlugins plugins;
    private final ITime time;
    private final IClient client;
    private final ChatMessageManager chatMessageManager;


    public void register(LoopedPlugin plugin) {
        log.debug("Registering {} as a LoopedPlugin", plugin.getName());

        var executor = new DefaultLoopedPluginExecutor(plugin, game, plugins, time, client, chatMessageManager);
        loopedPlugins.put(plugin, executor);

        var newThread = new Thread(executor);

        if (plugin instanceof TaskPlugin) {
            for (Task task : ((TaskPlugin) plugin).getTasks()) {
                if (task.subscribe()) {
                    eventBus.register(task);
                }

                if (task.inject()) {
                    plugin.getInjector().injectMembers(task);
                }
            }
        }

        newThread.start();
    }

    public void unregister(LoopedPlugin plugin) {
        var executor = loopedPlugins.remove(plugin);
        if (executor == null) {
            log.warn("Tried to unregister LoopedPlugin {}, but it was not registered", plugin.getName());
            return;
        }

        log.debug("Unregistering {} as a LoopedPlugin", plugin.getName());

        executor.stop();

        if (plugin instanceof TaskPlugin) {
            for (Task task : ((TaskPlugin) plugin).getTasks()) {
                if (task.subscribe()) {
                    eventBus.unregister(task);
                }
            }
        }
    }
}
