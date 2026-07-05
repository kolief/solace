package net.solace.loader.plugins.arceuuslibrary;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.arceuuslibrary.domain.Library;
import net.solace.loader.plugins.arceuuslibrary.domain.LibraryListener;
import net.solace.loader.plugins.arceuuslibrary.tree.TreeExecutor;
import net.solace.sdk.script.paint.DefaultPaint;
import net.solace.sdk.widgets.Dialog;

@PluginDescriptor(name = "Solace Arceuus Library")
public class SolaceArceuusLibrary extends LoopedPlugin {
    private TreeExecutor tree;
    private DefaultPaint paint;
    @Getter
    @Setter
    private String currentAction = "Deciding...";
    @Getter
    @Setter
    private boolean shouldStop;

    @Inject
    private EventBus eventBus;

    @Inject
    private LibraryListener libraryListener;

    @Inject
    @Getter
    private Library library;

    @Inject
    @Getter
    private SolaceArceuusLibraryConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    @Getter
    private BreakHandler breakHandler;

    @Override
    public void startUp() {
        eventBus.register(libraryListener);

        tree = new TreeExecutor();
        tree.init(this);

        paint = new DefaultPaint();

        overlayManager.add(paint);

        setShouldStop(false);
        paint.setEnabled(true);
        paint.setHeader("Solace Arceuus Library");
        paint.trackSkill(Skill.MAGIC, false);
        paint.trackSkill(Skill.RUNECRAFT, false);
        paint.submit("Current Action", () -> currentAction);
        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
    }

    @Override
    public void shutDown() {
        setShouldStop(false);
        library.reset();
        library.clearListeners();
        eventBus.unregister(libraryListener);
        overlayManager.remove(paint);
        breakHandler.stopPlugin(this);
        breakHandler.unregisterPlugin(this);
    }

    @Override
    public int loop() {
        return tree.execute();
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if (Dialog.canContinue()) {
            Dialog.continueSpace();
        }

        var otherBook = getLibrary().getOtherBook();
        if (otherBook != null && otherBook.isInInventory()) {
            getLibrary().setOtherBook(null);
        }
    }

    @Provides
    SolaceArceuusLibraryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceArceuusLibraryConfig.class);
    }
}