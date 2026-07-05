package net.solace.api.movement.pathfinder.model;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.Skill;
import net.solace.api.Static;
import net.solace.api.domain.tiles.ITileObject;

public enum PohPool {
    ALTAR(() -> Static.getPrayers().getMissingPoints() < Static.getSolaceConfig().requiredMissingPrayer(), null, object -> object != null && object.getName() != null && object.getName().toLowerCase().contains("altar") && object.hasAction(new String[]{"Pray"})),
    RESTORATION(() -> Static.getCombat().getSpecEnergy() == 100, null, 29237, 40844),
    REVITALISATION(() -> 100 - Static.getMovement().getRunEnergy() < Static.getSolaceConfig().requiredMissingRunEnergy(), RESTORATION, 29238, 40845),
    REJUVENATION(() -> Static.getPrayers().getMissingPoints() < Static.getSolaceConfig().requiredMissingPrayer(), REVITALISATION, 29239, 40846),
    FANCY_REJUVENATION(() -> Static.getSkills().getReducedSkills().stream().noneMatch(x -> x != Skill.PRAYER && x != Skill.HITPOINTS), REJUVENATION, 29240, 40847),
    ORNATE_REJUVENATION(() -> Static.getCombat().getMissingHealth() < Static.getSolaceConfig().requiredMissingHealth() && !Static.getCombat().isVenomed() && !Static.getCombat().isPoisoned(), FANCY_REJUVENATION, 29241, 40848, 49993);

    private final Supplier<Boolean> usageResult;
    private final PohPool previousPool;
    private final Predicate<ITileObject> objectPredicate;

    private PohPool(Supplier<Boolean> usageResult, PohPool previousPool, int ... ids) {
        this.usageResult = usageResult;
        this.previousPool = previousPool;
        this.objectPredicate = object -> object != null && Arrays.stream(ids).anyMatch(id -> id == object.getId());
    }

    private PohPool(Supplier<Boolean> usageResult, PohPool previousPool, Predicate<ITileObject> objectPredicate) {
        this.usageResult = usageResult;
        this.previousPool = previousPool;
        this.objectPredicate = objectPredicate;
    }

    public static PohPool get() {
        if (!Static.getHouse().isInside()) {
            return null;
        }
        for (int i = PohPool.values().length - 1; i >= 0; --i) {
            PohPool pool = PohPool.values()[i];
            ITileObject object = pool.getObject();
            if (object == null || pool.isUsed()) continue;
            return pool;
        }
        return null;
    }

    public boolean isUsed() {
        if (!this.usageResult.get().booleanValue()) {
            return false;
        }
        PohPool current = this.previousPool;
        while (current != null) {
            if (!current.usageResult.get().booleanValue()) {
                return false;
            }
            current = current.previousPool;
        }
        return true;
    }

    public ITileObject getObject() {
        return Static.getTileObjects().getNearest(this.objectPredicate);
    }
}

