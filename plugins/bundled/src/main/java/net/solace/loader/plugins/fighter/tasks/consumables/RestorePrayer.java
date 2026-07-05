package net.solace.loader.plugins.fighter.tasks.consumables;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.solace.api.commons.Rand;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.game.Skills;
import net.solace.sdk.widgets.Prayers;

@Slf4j
public class RestorePrayer extends FighterTask {
    public RestorePrayer(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        if (shouldSafespot()) {
            return false;
        }

        var prayerRestore = getContext().getPrayerRestore();

        return getConfig().restore()
                && getContext().getConsumableCooldown() == 0
                && Prayers.getPoints() <= getNextPrayerRestore()
                && prayerRestore != null;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Restoring prayer");
        var prayerRestore = getContext().getPrayerRestore();

        if (prayerRestore == null) {
            log.warn("No restore found to drink");
            return -1;
        }

        prayerRestore.interact("Drink", "Eat", "Release");
        var maxPrayer = (int) (Skills.getLevel(Skill.PRAYER) * 0.6);
        var minPrayer = Math.min(8, maxPrayer);
        setNextPrayerRestore(Rand.nextInt(minPrayer, maxPrayer));
        getContext().setConsumableCooldown(3);
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
