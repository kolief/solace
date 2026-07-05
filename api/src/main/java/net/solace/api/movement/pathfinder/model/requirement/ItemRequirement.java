package net.solace.api.movement.pathfinder.model.requirement;

import java.util.List;
import java.util.function.Predicate;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.requirement.Reduction;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;

public class ItemRequirement
implements Requirement {
    private final Reduction reduction;
    private final List<Integer> ids;
    private final Location location;
    private final int amount;

    public ItemRequirement(Reduction reduction, List<Integer> ids, int amount) {
        this(reduction, ids, Location.INVENTORY, amount);
    }

    @Override
    public Boolean get() {
        Predicate<Integer> countPredicate = this.getCountPredicate();
        switch (this.reduction) {
            case AND: {
                return this.ids.stream().allMatch(countPredicate);
            }
            case OR: {
                return this.ids.stream().anyMatch(countPredicate);
            }
            case NONE: {
                return this.ids.stream().noneMatch(countPredicate);
            }
        }
        return false;
    }

    private Predicate<Integer> getCountPredicate() {
        switch (this.location) {
            case EQUIPMENT: {
                return id -> Static.getEquipment().getCount(true, (int)id) >= this.amount;
            }
            case INVENTORY: {
                return id -> Static.getInventory().getCount(true, (int)id) >= this.amount;
            }
            case EITHER: {
                return id -> Static.getEquipment().getCount(true, (int)id) >= this.amount || Static.getInventory().getCount(true, (int)id) >= this.amount;
            }
        }
        throw new IllegalStateException("Unknown location: " + String.valueOf((Object)this.location));
    }

    public ItemRequirement(Reduction reduction, List<Integer> ids, Location location, int amount) {
        this.reduction = reduction;
        this.ids = ids;
        this.location = location;
        this.amount = amount;
    }

    public Reduction getReduction() {
        return this.reduction;
    }

    public List<Integer> getIds() {
        return this.ids;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemRequirement)) {
            return false;
        }
        ItemRequirement other = (ItemRequirement)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getAmount() != other.getAmount()) {
            return false;
        }
        Reduction this$reduction = this.getReduction();
        Reduction other$reduction = other.getReduction();
        if (this$reduction == null ? other$reduction != null : !((Object)((Object)this$reduction)).equals((Object)other$reduction)) {
            return false;
        }
        List<Integer> this$ids = this.getIds();
        List<Integer> other$ids = other.getIds();
        if (this$ids == null ? other$ids != null : !((Object)this$ids).equals(other$ids)) {
            return false;
        }
        Location this$location = this.getLocation();
        Location other$location = other.getLocation();
        return !(this$location == null ? other$location != null : !((Object)((Object)this$location)).equals((Object)other$location));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ItemRequirement;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getAmount();
        Reduction $reduction = this.getReduction();
        result = result * 59 + ($reduction == null ? 43 : ((Object)((Object)$reduction)).hashCode());
        List<Integer> $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : ((Object)$ids).hashCode());
        Location $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : ((Object)((Object)$location)).hashCode());
        return result;
    }

    public static enum Location {
        EQUIPMENT,
        EITHER,
        INVENTORY;

    }
}

