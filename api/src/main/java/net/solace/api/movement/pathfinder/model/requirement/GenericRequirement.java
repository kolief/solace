package net.solace.api.movement.pathfinder.model.requirement;

import java.util.function.Supplier;
import net.solace.api.movement.pathfinder.model.requirement.Comparison;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;

public final class GenericRequirement
implements Requirement {
    private final Supplier<Integer> valueSupplier;
    private final Integer expected;
    private final Comparison comparison;

    @Override
    public Boolean get() {
        return this.comparison.apply(this.valueSupplier.get(), this.expected);
    }

    public GenericRequirement(Supplier<Integer> valueSupplier, Integer expected, Comparison comparison) {
        this.valueSupplier = valueSupplier;
        this.expected = expected;
        this.comparison = comparison;
    }

    public Supplier<Integer> getValueSupplier() {
        return this.valueSupplier;
    }

    public Integer getExpected() {
        return this.expected;
    }

    public Comparison getComparison() {
        return this.comparison;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GenericRequirement)) {
            return false;
        }
        GenericRequirement other = (GenericRequirement)o;
        Integer this$expected = this.getExpected();
        Integer other$expected = other.getExpected();
        if (this$expected == null ? other$expected != null : !((Object)this$expected).equals(other$expected)) {
            return false;
        }
        Supplier<Integer> this$valueSupplier = this.getValueSupplier();
        Supplier<Integer> other$valueSupplier = other.getValueSupplier();
        if (this$valueSupplier == null ? other$valueSupplier != null : !this$valueSupplier.equals(other$valueSupplier)) {
            return false;
        }
        Comparison this$comparison = this.getComparison();
        Comparison other$comparison = other.getComparison();
        return !(this$comparison == null ? other$comparison != null : !this$comparison.equals(other$comparison));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $expected = this.getExpected();
        result = result * 59 + ($expected == null ? 43 : ((Object)$expected).hashCode());
        Supplier<Integer> $valueSupplier = this.getValueSupplier();
        result = result * 59 + ($valueSupplier == null ? 43 : $valueSupplier.hashCode());
        Comparison $comparison = this.getComparison();
        result = result * 59 + ($comparison == null ? 43 : $comparison.hashCode());
        return result;
    }

    public String toString() {
        return "GenericRequirement(valueSupplier=" + String.valueOf(this.getValueSupplier()) + ", expected=" + this.getExpected() + ", comparison=" + String.valueOf(this.getComparison()) + ")";
    }
}

