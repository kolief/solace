package net.solace.api.movement.pathfinder.model.requirement;

import net.solace.api.movement.pathfinder.model.requirement.Comparison;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;
import net.solace.api.movement.pathfinder.model.requirement.VarType;

public final class VarRequirement
implements Requirement {
    private final Comparison comparison;
    private final VarType type;
    private final int var;
    private final int value;

    @Override
    public Boolean get() {
        return this.comparison.apply(this.type.apply(this.var), this.value);
    }

    public VarRequirement(Comparison comparison, VarType type, int var, int value) {
        this.comparison = comparison;
        this.type = type;
        this.var = var;
        this.value = value;
    }

    public Comparison getComparison() {
        return this.comparison;
    }

    public VarType getType() {
        return this.type;
    }

    public int getVar() {
        return this.var;
    }

    public int getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof VarRequirement)) {
            return false;
        }
        VarRequirement other = (VarRequirement)o;
        if (this.getVar() != other.getVar()) {
            return false;
        }
        if (this.getValue() != other.getValue()) {
            return false;
        }
        Comparison this$comparison = this.getComparison();
        Comparison other$comparison = other.getComparison();
        if (this$comparison == null ? other$comparison != null : !this$comparison.equals(other$comparison)) {
            return false;
        }
        VarType this$type = this.getType();
        VarType other$type = other.getType();
        return !(this$type == null ? other$type != null : !this$type.equals(other$type));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getVar();
        result = result * 59 + this.getValue();
        Comparison $comparison = this.getComparison();
        result = result * 59 + ($comparison == null ? 43 : $comparison.hashCode());
        VarType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        return result;
    }

    public String toString() {
        return "VarRequirement(comparison=" + String.valueOf(this.getComparison()) + ", type=" + String.valueOf(this.getType()) + ", var=" + this.getVar() + ", value=" + this.getValue() + ")";
    }
}

