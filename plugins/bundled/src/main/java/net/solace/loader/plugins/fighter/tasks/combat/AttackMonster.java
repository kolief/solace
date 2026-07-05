package net.solace.loader.plugins.fighter.tasks.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.NpcID;
import net.solace.api.domain.actors.INPC;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.entities.Players;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;

import java.util.List;
import java.util.Objects;

@Slf4j
public class AttackMonster extends FighterTask {
    public AttackMonster(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        if (shouldTag()) {
            return true;
        }

        if (shouldSafespot()) {
            return true;
        }

        return shouldSwitchTargets();
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Attacking monster");
        var interacting = Players.getLocal().getInteracting();
        if (shouldTag()) {
            var untagged = getUntaggedMonsters();

            if (untagged.isEmpty()) {
                log.warn("No untagged monsters found");
                return -1;
            }

            var target = untagged.stream().findFirst().orElse(null);

            if (interacting != null && interacting == target) {
                return -1;
            }

            target.interact("Attack");
            return -1;
        }

        var safespot = getContext().getSafespot();

        if (shouldSafespot()) {
            if (Movement.isWalking()) {
                return -1;
            }

            Movement.walkTo(safespot);
            return -1;
        }

        var local = Players.getLocal();
        var bestTarget = getBestTarget();

        if (Movement.isWalking()) {
            return -1;
        }

        if (bestTarget == null) {
            if (local.getWorldLocation().distanceTo(getContext().getCenter()) < 3) {
                log.warn("No valid target found");
                return -1;
            }

            if (getConfig().returnToCenter()) {
                Movement.walkTo(getContext().getCenter());
            }
            return -1;
        }

        if (!bestTarget.isInteractable(local) && !getConfig().disableReachability()) {
            Movement.walkTo(bestTarget);
            return -1;
        }

        //Only boss needs explosives
        if (bestTarget.hasAction("Disturb") && bestTarget.getId() == NpcID.SLAYER_KRAKEN_BOSS_WHIRLPOOL) {
            var explosive = Inventory.getFirst("Fishing explosive");
            if (explosive != null) {
                explosive.useOn(bestTarget);
                return -12;
            }
        }

        bestTarget.interact("Attack", "Disturb", "Awaken");
        return -1;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    private boolean shouldTag() {
        return getConfig().gatherMobs() && !getUntaggedMonsters().isEmpty();
    }

    private List<INPC> getUntaggedMonsters() {
        return getAttackableNpcs(x -> x.getInteracting() == null);
    }

    private boolean shouldSwitchTargets() {
        var bestTarget = getBestTarget();
        if (bestTarget == null) {
            return false;
        }

        return !Objects.equals(Players.getLocal().getInteracting(), bestTarget);
    }
}