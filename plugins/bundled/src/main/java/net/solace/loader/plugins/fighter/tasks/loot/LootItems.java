package net.solace.loader.plugins.fighter.tasks.loot;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.magic.SpellBook;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;

@Slf4j
public class LootItems extends FighterTask {
    public LootItems(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var loot = getItemToLoot();

        return getConfig().looting()
                && loot != null
                && canLoot(loot);
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Looting");
        var loot = getItemToLoot();

        if (loot == null) {
            log.warn("No loot found");
            return -1;
        }

        if (getConfig().onlyUseTelegrab()) {
            if (!canTelegrab(loot)) {
                return -1;
            }

            if (!Players.getLocal().isAnimating()) {
                SpellBook.Standard.TELEKINETIC_GRAB.castOn(loot);
                return -2;
            }
            return -3;
        }

        if (!loot.isInteractable()) {
            if (Movement.isWalking()) {
                return -1;
            }

            Movement.walkTo(loot.getTile());
            return -1;
        }

        if (Players.getLocal().isMoving()) {
            return -1;
        }

        loot.interact("Take");
        return -2;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    private boolean canLoot(ITileItem item) {
        return !getConfig().onlyUseTelegrab() || canTelegrab(item);
    }

    private boolean canTelegrab(ITileItem item) {
        return SpellBook.Standard.TELEKINETIC_GRAB.canCast()
                && item.distanceTo(Players.getLocal()) <= 10
                && Players.getLocal().getWorldArea().hasLineOfSightTo(Players.getLocal().getWorldView(), item.getWorldLocation());
    }
}
