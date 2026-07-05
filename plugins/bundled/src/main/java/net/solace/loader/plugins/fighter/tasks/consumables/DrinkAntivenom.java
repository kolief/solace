package net.solace.loader.plugins.fighter.tasks.consumables;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.data.Antivenom;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.game.Combat;

@Slf4j
public class DrinkAntivenom extends FighterTask {
    public DrinkAntivenom(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        if (shouldSafespot()) {
            return false;
        }

        return getContext().getConsumableCooldown() == 0
                && Combat.isPoisoned()
                && Antivenom.getAntiVenom() != null;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Drinking antivenom");
        var antidote = Antivenom.getAntiVenom();

        if (antidote == null) {
            log.warn("No antidote found to drink");
            return -1;
        }

        antidote.interact("Drink", "Eat");
        getContext().setConsumableCooldown(3);
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
