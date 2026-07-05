package net.solace.loader.plugins.fighter.tasks.consumables;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;

@Slf4j
public class DrinkBoost extends FighterTask {
    public DrinkBoost(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        if (shouldSafespot()) {
            return false;
        }

        return shouldBoost()
                && getContext().getConsumableCooldown() == 0;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Drinking boost");
        var boostPotion = getBoost();

        if (boostPotion == null) {
            log.warn("No boost potion found to drink");
            return -1;
        }

        boostPotion.interact("Drink", "Invigorate");
        getContext().setConsumableCooldown(3);
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
