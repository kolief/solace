package net.solace.ui.plugins.items;

import net.runelite.client.game.ItemManager;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.widgets.EquipmentSlot;

import java.awt.GridBagLayout;

public class EquipmentLoadoutPanel extends LoadoutPanel {
    private final Loadout loadout;

    public EquipmentLoadoutPanel(
            ItemSelector itemSelector,
            Loadout loadout,
            ConfigManager configManager,
            String configGroup,
            String configKey,
            IClientThread clientThread,
            ItemManager itemManager
    ) {
        super(LoadoutItem.Type.EQUIPMENT, itemSelector, configManager, configGroup, configKey,
                clientThread, itemManager, loadout);
        this.loadout = loadout;

        setLayout(new GridBagLayout());
        rebuild();
    }

    @Override
    protected void rebuild() {
        removeAll();

        loadAllImages();

        addSlot(EquipmentSlot.HEAD.getSlot(), 1, 0);
        addSlot(EquipmentSlot.CAPE.getSlot(), 0, 1);
        addSlot(EquipmentSlot.AMULET.getSlot(), 1, 1);
        addSlot(EquipmentSlot.AMMO.getSlot(), 2, 1);
        addSlot(EquipmentSlot.WEAPON.getSlot(), 0, 3);
        addSlot(EquipmentSlot.BODY.getSlot(), 1, 3);
        addSlot(EquipmentSlot.SHIELD.getSlot(), 2, 3);
        addSlot(EquipmentSlot.LEGS.getSlot(), 1, 4);
        addSlot(EquipmentSlot.GLOVES.getSlot(), 0, 5);
        addSlot(EquipmentSlot.BOOTS.getSlot(), 1, 5);
        addSlot(EquipmentSlot.RING.getSlot(), 2, 5);

        revalidate();
    }

    @Override
    protected LoadoutItem[] getItems() {
        return loadout.getEquipment();
    }
}