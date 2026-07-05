package net.solace.loader.interact;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.PostClientTick;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.game.IClient;
import net.solace.api.items.IInventory;
import net.solace.api.magic.IMagic;

/**
 * Development version of InteractManager that simulates PreClientTick events
 * when running through the IDE (where @Subscribe events may not be injected properly).
 * <p>
 * This class extends the production InteractManagerImpl and only overrides
 * onPostClientTick to manually call onPreClientTick for simulation purposes.
 */
@Slf4j
public class DevInteractManagerImpl extends InteractManagerImpl {

    public DevInteractManagerImpl(IClient client, IMagic magic, IInventory inventory) {
        super(client, magic, inventory);
    }

    /**
     * Overrides onPostClientTick to manually call onPreClientTick for IDE simulation.
     * In the IDE, @Subscribe events may not be properly injected, so we manually
     * trigger PreClientTick processing after each PostClientTick.
     */
    @Override
    @Subscribe
    protected void onPostClientTick(PostClientTick e) {
        // Call parent's onPostClientTick for standard cleanup (minimap, release, etc.)
        super.onPostClientTick(e);

        // Manually simulate PreClientTick event for IDE development
        // This allows path execution and automation processing to continue
        onPreClientTick(null);
    }
}


