package net.solace.api.events;

public class ItemObtained {
    private int itemId;
    private int amount;

    public int getItemId() {
        return this.itemId;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemObtained)) {
            return false;
        }
        ItemObtained other = (ItemObtained)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getItemId() != other.getItemId()) {
            return false;
        }
        return this.getAmount() == other.getAmount();
    }

    protected boolean canEqual(Object other) {
        return other instanceof ItemObtained;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getItemId();
        result = result * 59 + this.getAmount();
        return result;
    }

    public String toString() {
        return "ItemObtained(itemId=" + this.getItemId() + ", amount=" + this.getAmount() + ")";
    }

    public ItemObtained() {
    }

    public ItemObtained(int itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }
}

