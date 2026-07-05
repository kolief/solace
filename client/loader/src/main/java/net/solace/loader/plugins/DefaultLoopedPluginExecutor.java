package net.solace.loader.plugins;

import net.runelite.client.chat.ChatMessageManager;
import net.solace.api.commons.ITime;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IGame;
import net.solace.api.plugins.IPlugins;
import net.solace.api.plugins.LoopedPlugin;

public class DefaultLoopedPluginExecutor extends LoopedPluginExecutor<LoopedPlugin> {
    public DefaultLoopedPluginExecutor(LoopedPlugin loopedPlugin, IGame game, IPlugins plugins, ITime time, IClient client, ChatMessageManager chatMessageManager) {
        super(loopedPlugin, game, plugins, time, client, chatMessageManager);
    }

    @Override
    protected int loop() {
        return getPlugin().loop();
    }
}
