package net.solace.loader.plugins.fighter.tasks.loot;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.Static;
import net.solace.api.commons.Rand;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.items.Inventory;

import java.util.List;

@Slf4j
public class DropJunk extends FighterTask {
    public DropJunk(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !getJunk().isEmpty() && !shouldSafespot();
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Dropping junk");
        var junk = getJunk();

        if (junk.isEmpty()) {
            log.warn("No junk found to drop");
            return -1;
        }

        for (var item : junk) {
            item.interact("Drop");
            Static.getClient().sleep(Rand.nextInt(3, 6));
        }

        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    public List<IInventoryItem> getJunk() {
        return Inventory.getAll("Vial", "Jug");
    }
}
