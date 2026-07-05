package net.solace.loader.plugins.fighter.tasks.combat;

import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.game.Combat;

public class HandleSpecial extends FighterTask {
    public HandleSpecial(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getConfig().useSpec()
                && !Combat.isSpecEnabled()
                && Combat.getSpecEnergy() >= getConfig().specAmount();
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Toggling special attack");
        Combat.toggleSpec();
        return -2;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
