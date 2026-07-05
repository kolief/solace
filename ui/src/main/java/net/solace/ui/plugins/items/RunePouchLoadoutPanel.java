package net.solace.ui.plugins.items;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.plugins.config.ConfigManager;

import java.awt.GridBagLayout;
import java.util.Set;

public class RunePouchLoadoutPanel extends LoadoutPanel {

    private static final Set<Integer> ALLOWED_ITEM_IDS = Set.of(ItemID.AIRRUNE, ItemID.WATERRUNE,
            ItemID.EARTHRUNE, ItemID.FIRERUNE, ItemID.MINDRUNE, ItemID.CHAOSRUNE, ItemID.DEATHRUNE,
            ItemID.BLOODRUNE, ItemID.COSMICRUNE, ItemID.NATURERUNE, ItemID.LAWRUNE, ItemID.BODYRUNE,
            ItemID.SOULRUNE, ItemID.ASTRALRUNE, ItemID.MISTRUNE, ItemID.MUDRUNE, ItemID.DUSTRUNE,
            ItemID.LAVARUNE, ItemID.STEAMRUNE, ItemID.SMOKERUNE, ItemID.WRATHRUNE, ItemID.SUNFIRERUNE,
            ItemID.AETHERRUNE
    );

    private final Loadout loadout;

    public RunePouchLoadoutPanel(
            ItemSelector itemSelector,
            Loadout loadout,
            ConfigManager configManager,
            String configGroup,
            String configKey,
            IClientThread clientThread,
            ItemManager itemManager
    ) {
        super(LoadoutItem.Type.RUNE_POUCH, itemSelector, configManager, configGroup, configKey,
                clientThread, itemManager, loadout);
        this.loadout = loadout;

        setLayout(new GridBagLayout());
        itemConsumer = this::checkRunePouch;
        rebuild();
    }

    private void checkRunePouch(LoadoutItem item) {
        if (item != null && !ALLOWED_ITEM_IDS.contains(item.getId())) {
            throw new InvalidItemException("This item is not allowed in the rune pouch.");
        }
    }

    @Override
    protected void rebuild() {
        removeAll();

        loadAllImages();

        addSlot(0, 0, 0);
        addSlot(1, 1, 0);
        addSlot(2, 2, 0);
        addSlot(3, 3, 0);

        revalidate();
    }

    @Override
    protected LoadoutItem[] getItems() {
        return loadout.getRunePouch();
    }
}