package net.solace.loader.plugins.fighter.tasks.misc;

import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;

public class WaitForStart extends FighterTask {
    public WaitForStart(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !getConfig().enabled();
    }

    @Override
    public int execute() {
        return 1000;
    }
}
