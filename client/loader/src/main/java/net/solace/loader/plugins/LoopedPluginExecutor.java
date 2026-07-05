package net.solace.loader.plugins;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.solace.api.commons.ITime;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IGame;
import net.solace.api.plugins.IPlugins;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.exception.PluginStoppedException;

import javax.swing.SwingUtilities;

@RequiredArgsConstructor
@Slf4j
public abstract class LoopedPluginExecutor<T extends LoopedPlugin> implements Runnable {
    private static final int BASE_SLEEP = 10;
    private static final int EXCEPTION_SLEEP = 100;
    private static final int DEFAULT_SLEEP = 1000;

    @Getter
    private final T plugin;
    private final IGame game;
    private final IPlugins plugins;
    private final ITime time;
    private final IClient client;
    private final ChatMessageManager chatMessageManager;

    @Override
    public void run() {
        plugin.setStopped(false);

        var sleepUntil = 0;
        while (!plugin.isStopped()) {
            var nextSleep = BASE_SLEEP;

            try {
                if (sleepUntil > 0 && sleepUntil > client.getTickCount() && game.isLoggedIn()) {
                    continue;
                }

                sleepUntil = 0;

                var currentSleep = loop();
                if (currentSleep < 0 && game.isLoggedIn()) {
                    sleepUntil = client.getTickCount() + Math.abs(currentSleep);
                } else {
                    nextSleep = currentSleep < 0 ? DEFAULT_SLEEP : currentSleep;
                }
            } catch (PluginStoppedException e) {
                SwingUtilities.invokeLater(() -> plugins.stopPlugin(plugin));

                var msg = e.getMessage();
                if (msg == null) {
                    return;
                }

                if (!game.isLoggedIn()) {
                    log.warn("Plugin {} stopped due to {}", plugin.getName(), msg);
                    return;
                }

                chatMessageManager.queue(QueuedMessage.builder()
                        .runeLiteFormattedMessage(new ChatMessageBuilder()
                                .append(ChatColorType.HIGHLIGHT)
                                .append(String.format("%s stopped - ", plugin.getName()))
                                .append(msg)
                                .build())
                        .type(ChatMessageType.ITEM_EXAMINE)
                        .build());
            } catch (Exception e) {
                log.error("Error in plugin {}", plugin.getName(), e);
                nextSleep = EXCEPTION_SLEEP;
            } finally {
                time.sleep(nextSleep);
            }
        }

        log.debug("Plugin {} stopped", plugin.getName());
    }

    public void stop() {
        plugin.setStopped(true);
    }

    protected abstract int loop();
}
