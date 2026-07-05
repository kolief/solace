package net.solace.loader.plugins.fighter.tasks.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;
import net.solace.api.domain.items.IItem;
import net.solace.api.widgets.EquipmentSlot;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.items.Equipment;
import net.solace.sdk.items.Inventory;

@Slf4j
public class EquipBracelet extends FighterTask {
    public EquipBracelet(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var fromSlot = Equipment.fromSlot(EquipmentSlot.GLOVES);
        var bracelet = getBracelet();
        return bracelet != null
                && (fromSlot == null || fromSlot.getId() != bracelet.getId());
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Equipping bracelet");
        var bracelet = getBracelet();

        if (bracelet == null) {
            log.warn("No bracelet found to equip");
            return -1;
        }

        bracelet.interact("Wear");
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    private IItem getBracelet() {
        var slaughter = Inventory.getFirst(ItemID.BRACELET_OF_SLAUGHTER);
        if (slaughter != null) {
            return slaughter;
        }

        return Inventory.getFirst(ItemID.EXPEDITIOUS_BRACELET);
    }
}