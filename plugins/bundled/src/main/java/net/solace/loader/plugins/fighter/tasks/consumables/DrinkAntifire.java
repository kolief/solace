package net.solace.loader.plugins.fighter.tasks.consumables;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.fighter.data.AntifireType;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.game.Combat;

@Slf4j
public class DrinkAntifire extends FighterTask {
    public DrinkAntifire(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        if (shouldSafespot()) {
            return false;
        }

        return getConfig().antifireType() != AntifireType.NONE
                && getContext().getConsumableCooldown() == 0
                && getAntifire() != null
                && (!Combat.isAntifired() && !Combat.isSuperAntifired());
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Drinking antifire");
        var antifire = getAntifire();

        if (antifire == null) {
            log.warn("No antifire found to drink");
            return -1;
        }

        antifire.interact("Drink", "Eat");
        getContext().setConsumableCooldown(3);
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
