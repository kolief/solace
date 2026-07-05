package net.solace.loader.plugins.fighter.tasks.loot;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileItems;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;

import java.util.Comparator;

@Slf4j
public class HandleSoulbearer extends FighterTask {
    public HandleSoulbearer(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var loot = getEnsouledLoot();

        return getSoulBearer() != null
                && getConfig().useSoulBearer()
                && ((loot != null && loot.canPick()) || (getEnsouledItem() != null && !shouldSafespot()));
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Handling soul bearer");
        var item = getEnsouledItem();
        var soulBearer = getSoulBearer();

        if (soulBearer == null) {
            log.warn("No soul bearer found");
            return -1;
        }

        if (item != null) {
            item.useOn(soulBearer);
            return -1;
        }

        var loot = getEnsouledLoot();

        if (loot == null) {
            log.warn("No loot found");
            return -1;
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

    private ITileItem getEnsouledLoot() {
        return TileItems.getAllMine(x -> x.distanceTo(getContext().getCenter()) <= getConfig().attackRange()
                        && x.getName().toLowerCase().contains("ensouled"))
                .stream()
                .min(Comparator.comparingInt(x -> x.distanceTo(Players.getLocal())))
                .orElse(null);
    }

    private IInventoryItem getEnsouledItem() {
        return Inventory.getFirst(x -> x.getName().toLowerCase().contains("ensouled"));
    }

    private IItem getSoulBearer() {
        return Inventory.getFirst("Soul bearer");
    }
}
