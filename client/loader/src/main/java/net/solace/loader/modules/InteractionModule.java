package net.solace.loader.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.solace.api.domain.game.IClient;
import net.solace.api.interact.InteractManager;
import net.solace.api.interact.mouse.MouseManager;
import net.solace.api.items.IInventory;
import net.solace.api.magic.IMagic;
import net.solace.loader.interact.InteractManagerImpl;

import javax.inject.Singleton;

public class InteractionModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    InteractManager provideInteractManager(IClient client, IMagic magic, IInventory inventory) {
        return new InteractManagerImpl(client, magic, inventory);
    }

    @Provides
    @Singleton
    MouseManager provideMouseManager(InteractManager interactManager) {
        return interactManager.getMouseManager();
    }
}
