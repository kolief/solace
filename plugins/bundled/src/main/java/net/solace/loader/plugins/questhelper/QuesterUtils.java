package net.solace.loader.plugins.questhelper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.solace.loader.plugins.explorer.exclude.SolaceExplorerPlugin;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class QuesterUtils {
    private final SolaceExplorerPlugin explorerPlugin;
    private final SolaceQuestHelperPlugin plugin;

    public void setExplorerDestination(WorldPoint destination) {
        explorerPlugin.setDestination(destination);
        plugin.setPreviousDestination(destination);
    }

    public void resetExplorerDestination() {
        explorerPlugin.setDestination(null);
    }
}