package net.solace.loader.plugins.fighter.tasks;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.sdk.entities.Players;
import net.solace.sdk.game.Game;

@Slf4j
public class HandleStopTrigger extends FighterTask {
    public HandleStopTrigger(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getContext().isShouldStop()
                && getContext().getLogoutTimer() == 0
                && (Players.getLocal().getInteracting() == null);
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Stopping");
        Game.logout();
        return -3;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
