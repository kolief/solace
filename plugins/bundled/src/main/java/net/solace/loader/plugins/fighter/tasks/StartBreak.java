package net.solace.loader.plugins.fighter.tasks;

import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;

public class StartBreak extends PluginTask<SolaceFighterPlugin> {
    public StartBreak(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var local = Players.getLocal();
        var targetingNpc = NPCs.query()
                .targeting(local)
                .results()
                .nearest(local);

        return getContext().getBreakHandler().shouldBreak(getContext())
                && !local.isHealthBarVisible()
                && targetingNpc == null
                && local.getInteracting() == null;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Breaking");
        getContext().getBreakHandler().startBreak(getContext());
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

}
