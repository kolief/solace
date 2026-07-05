package net.solace.loader.plugins.fighter.tasks;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;

@Slf4j
public class IdleTask extends FighterTask {
    public IdleTask(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Idle");
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
