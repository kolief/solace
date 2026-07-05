package net.solace.loader.plugins.fighter.tasks.loot;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.fighter.data.AlchSpell;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.magic.Magic;

@Slf4j
public class HandleAlch extends FighterTask {
    public HandleAlch(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var alchSpell = getConfig().alchSpell();

        if (!getConfig().alching()
                || shouldSafespot()
                || alchSpell == AlchSpell.NONE) {
            return false;
        }

        return getContext().getAlchCooldown() == 0
                && alchSpell.getSpell().canCast()
                && getItemToAlch() != null;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Alching");
        var itemToAlch = getItemToAlch();

        if (itemToAlch == null) {
            log.warn("No item to alch found");
            return -1;
        }

        var alchSpell = getConfig().alchSpell().getSpell();

        if (!alchSpell.canCast()) {
            log.warn("Alch spell can't be cast");
            return -1;
        }

        Magic.cast(alchSpell, itemToAlch);
        getContext().setAlchCooldown(5);
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}