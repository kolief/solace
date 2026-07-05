package net.solace.api.magic;

import net.solace.api.magic.Rune;

public final class RuneRequirement {
    private final int quantity;
    private final Rune rune;

    public boolean meetsRequirements() {
        return this.rune.getQuantity() >= this.quantity;
    }

    public RuneRequirement(int quantity, Rune rune) {
        this.quantity = quantity;
        this.rune = rune;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public Rune getRune() {
        return this.rune;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RuneRequirement)) {
            return false;
        }
        RuneRequirement other = (RuneRequirement)o;
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        Rune this$rune = this.getRune();
        Rune other$rune = other.getRune();
        return !(this$rune == null ? other$rune != null : !((Object)((Object)this$rune)).equals((Object)other$rune));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getQuantity();
        Rune $rune = this.getRune();
        result = result * 59 + ($rune == null ? 43 : ((Object)((Object)$rune)).hashCode());
        return result;
    }

    public String toString() {
        return "RuneRequirement(quantity=" + this.getQuantity() + ", rune=" + String.valueOf((Object)this.getRune()) + ")";
    }
}

