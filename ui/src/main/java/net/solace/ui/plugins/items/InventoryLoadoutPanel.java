package net.solace.ui.plugins.items;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemManager;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.plugins.config.ConfigManager;

import java.awt.GridBagLayout;

@Slf4j
public class InventoryLoadoutPanel extends LoadoutPanel {
    private static final int INV_ROWS = 7;
    private static final int INV_COLS = 4;

    private final Loadout loadout;

    public InventoryLoadoutPanel(
            ItemSelector itemSelector,
            Loadout loadout,
            ConfigManager configManager,
            String configGroup,
            String configKey,
            IClientThread clientThread,
            ItemManager itemManager
    ) {
        super(LoadoutItem.Type.INVENTORY, itemSelector, configManager, configGroup, configKey,
                clientThread, itemManager, loadout);
        this.loadout = loadout;

        setLayout(new GridBagLayout());
        rebuild();
    }

    @Override
    public void rebuild() {
        removeAll();

        loadAllImages();

        var slot = 0;
        for (int y = 0; y < INV_ROWS; y++) {
            for (int x = 0; x < INV_COLS; x++) {
                addSlot(slot, x, y);
                slot++;
            }
        }

        revalidate();
    }

    @Override
    protected LoadoutItem[] getItems() {
        return loadout.getInventory();
    }
}