package net.solace.loader.plugins.fighter.tasks.misc;

import net.solace.api.movement.pathfinder.LocalCollisionMap;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.utils.MessageUtils;
import net.solace.sdk.widgets.Prayers;

public class PrayAltar extends FighterTask {
    public PrayAltar(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getConfig().prayAltar() && Prayers.getPoints() <= 5;
    }

    @Override
    public int execute() {
        var altar = TileObjects.getNearest(x -> x.hasAction("Pray-at"));
        if (altar == null) {
            MessageUtils.addMessage("No altar found, disabling this setting.");
            getConfig().prayAltar(false);
            return 1000;
        }

        if (!altar.isInteractable()) {
            Movement.walkTo(altar.getWorldLocation(), new LocalCollisionMap(false));
            return 1000;
        }

        altar.interact("Pray-at");
        return 2000;
    }
}
