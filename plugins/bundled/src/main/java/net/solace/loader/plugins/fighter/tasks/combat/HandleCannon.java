package net.solace.loader.plugins.fighter.tasks.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.solace.api.commons.Rand;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.entities.Players;
import net.solace.sdk.game.Vars;
import net.solace.sdk.items.Inventory;

@Slf4j
public class HandleCannon extends FighterTask {
    public HandleCannon(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var cannon = getContext().getCannon();

        if (!getConfig().refillCannon() || cannon == null) {
            return false;
        }

        return shouldRepair()
                || (!isFiring() && (getAmmo() > 0 || hasAmmo()))
                || (hasAmmo() && getAmmo() <= getNextCannonballCount())
                || (!hasAmmo() && getAmmo() == 0 && Inventory.getFreeSlots() >= 4);
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Handling cannon");
        var cannon = getContext().getCannon();

        if (cannon == null) {
            log.warn("No cannon found to repair");
            return -1;
        }

        if (!hasAmmo() && getAmmo() == 0 && Inventory.getFreeSlots() >= 4) {
            if (Players.getLocal().isMoving()) {
                return -1;
            }

            cannon.interact("Pick-up");
            return -4;
        }

        if (shouldRepair()) {
            if (Players.getLocal().isMoving()) {
                return -1;
            }

            cannon.interact("Repair");
            return -4;
        }

        if (!isFiring() && (getAmmo() > 0 || hasAmmo())) {
            if (Players.getLocal().isMoving()) {
                return -1;
            }

            cannon.interact("Fire");
            return -4;
        }

        if (hasAmmo() && getAmmo() <= getNextCannonballCount()) {
            if (Players.getLocal().isMoving()) {
                return -1;
            }

            cannon.interact("Fire");
            setNextCannonballCount(Rand.nextInt(5, 17));
            return -4;
        }

        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    private boolean shouldRepair() {
        var cannon = getContext().getCannon();

        return cannon != null && cannon.hasAction("Repair");
    }

    private boolean hasAmmo() {
        return Inventory.contains(ItemID.MCANNONBALL, ItemID.GRANITE_CANNONBALL);
    }

    private int getAmmo() {
        return Vars.getVarp(VarPlayerID.ROCKTHROWER);
    }

    private int getState() {
        return Vars.getVarp(VarPlayerID.DROPCANNON);
    }

    private boolean isFiring() {
        return Vars.getVarp(1) > 0;
    }
}