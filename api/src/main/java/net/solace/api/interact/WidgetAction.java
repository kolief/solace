package net.solace.api.interact;

import net.solace.api.interact.Automation;

public final class WidgetAction
implements Automation {
    private final int identifier;
    private final int componentId;
    private final int index;
    private final int itemId;

    public WidgetAction(int identifier, int componentId, int index, int itemId) {
        this.identifier = identifier;
        this.componentId = componentId;
        this.index = index;
        this.itemId = itemId;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public int getComponentId() {
        return this.componentId;
    }

    public int getIndex() {
        return this.index;
    }

    public int getItemId() {
        return this.itemId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WidgetAction)) {
            return false;
        }
        WidgetAction other = (WidgetAction)o;
        if (this.getIdentifier() != other.getIdentifier()) {
            return false;
        }
        if (this.getComponentId() != other.getComponentId()) {
            return false;
        }
        if (this.getIndex() != other.getIndex()) {
            return false;
        }
        return this.getItemId() == other.getItemId();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getIdentifier();
        result = result * 59 + this.getComponentId();
        result = result * 59 + this.getIndex();
        result = result * 59 + this.getItemId();
        return result;
    }

    public String toString() {
        return "WidgetAction(identifier=" + this.getIdentifier() + ", componentId=" + this.getComponentId() + ", index=" + this.getIndex() + ", itemId=" + this.getItemId() + ")";
    }
}

