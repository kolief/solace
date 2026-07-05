package net.solace.loader.plugins.fighter.tasks.consumables;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.commons.Rand;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.game.Combat;

@Slf4j
public class EatFood extends FighterTask {
    public EatFood(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var food = getContext().getFood();

        return getConfig().eat()
                && getContext().getConsumableCooldown() == 0
                && Combat.getHealthPercent() <= getNextEatPercent()
                && food != null;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Eating food");
        var food = getContext().getFood();

        if (food == null) {
            log.warn("No food found to eat");
            return -1;
        }

        food.interact("Eat", "Drink");
        setNextEatPercent(Rand.nextInt(getConfig().minHealthPercent(), getConfig().maxHealthPercent()));
        getContext().setConsumableCooldown(3);
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
