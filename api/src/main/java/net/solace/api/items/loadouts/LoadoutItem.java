package net.solace.api.items.loadouts;

public final class LoadoutItem {
    private final int id;
    private final int quantity;
    private final int maxQuantity;
    private final boolean stackable;
    private final boolean noted;
    private boolean strict;
    private final Type type;
    private int slot;

    public LoadoutItem(int id, int quantity, int maxQuantity, boolean stackable, boolean noted, boolean strict, Type type, int slot) {
        this.id = id;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
        this.stackable = stackable;
        this.noted = noted;
        this.strict = strict;
        this.type = type;
        this.slot = slot;
    }

    public LoadoutItem(int id, int quantity, int maxQuantity, boolean stackable, boolean noted, Type type, int slot) {
        this(id, quantity, maxQuantity, stackable, noted, false, type, slot);
    }

    public LoadoutItem(int id, int quantity, boolean stackable, boolean noted, Type type, int slot) {
        this(id, quantity, !stackable ? 1 : quantity, stackable, noted, false, type, slot);
    }

    public int getId() {
        return this.id;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getMaxQuantity() {
        return this.maxQuantity;
    }

    public boolean isStackable() {
        return this.stackable;
    }

    public boolean isNoted() {
        return this.noted;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public Type getType() {
        return this.type;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoadoutItem)) {
            return false;
        }
        LoadoutItem other = (LoadoutItem)o;
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        if (this.getMaxQuantity() != other.getMaxQuantity()) {
            return false;
        }
        if (this.isStackable() != other.isStackable()) {
            return false;
        }
        if (this.isNoted() != other.isNoted()) {
            return false;
        }
        if (this.isStrict() != other.isStrict()) {
            return false;
        }
        if (this.getSlot() != other.getSlot()) {
            return false;
        }
        Type this$type = this.getType();
        Type other$type = other.getType();
        return !(this$type == null ? other$type != null : !((Object)((Object)this$type)).equals((Object)other$type));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        result = result * 59 + this.getQuantity();
        result = result * 59 + this.getMaxQuantity();
        result = result * 59 + (this.isStackable() ? 79 : 97);
        result = result * 59 + (this.isNoted() ? 79 : 97);
        result = result * 59 + (this.isStrict() ? 79 : 97);
        result = result * 59 + this.getSlot();
        Type $type = this.getType();
        result = result * 59 + ($type == null ? 43 : ((Object)((Object)$type)).hashCode());
        return result;
    }

    public String toString() {
        return "LoadoutItem(id=" + this.getId() + ", quantity=" + this.getQuantity() + ", maxQuantity=" + this.getMaxQuantity() + ", stackable=" + this.isStackable() + ", noted=" + this.isNoted() + ", strict=" + this.isStrict() + ", type=" + String.valueOf((Object)this.getType()) + ", slot=" + this.getSlot() + ")";
    }

    public static enum Type {
        INVENTORY,
        EQUIPMENT,
        RUNE_POUCH;

    }
}

