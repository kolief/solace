package net.solace.loader.plugins.fighter.tasks.consumables;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.VarbitID;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.game.Vars;

@Slf4j
public class DrinkGoading extends FighterTask {
    public DrinkGoading(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getContext().getConsumableCooldown() == 0
                && getConfig().useGoading()
                && getGoadingPotion() != null
                && Vars.getBit(VarbitID.GOADING_POTION_TIMER) == 0;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Drinking goading potion");
        var goading = getGoadingPotion();

        if (goading == null) {
            log.warn("No goading potion found to drink");
            return -1;
        }

        goading.interact("Drink", "Eat");
        getContext().setConsumableCooldown(3);
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
