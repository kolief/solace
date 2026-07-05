package net.solace.api.events;

import net.solace.api.domain.actors.IActor;

public final class InteractingChanged {
    private final IActor source;
    private final IActor target;

    public InteractingChanged(IActor source, IActor target) {
        this.source = source;
        this.target = target;
    }

    public IActor getSource() {
        return this.source;
    }

    public IActor getTarget() {
        return this.target;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InteractingChanged)) {
            return false;
        }
        InteractingChanged other = (InteractingChanged)o;
        IActor this$source = this.getSource();
        IActor other$source = other.getSource();
        if (this$source == null ? other$source != null : !this$source.equals(other$source)) {
            return false;
        }
        IActor this$target = this.getTarget();
        IActor other$target = other.getTarget();
        return !(this$target == null ? other$target != null : !this$target.equals(other$target));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        IActor $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
        IActor $target = this.getTarget();
        result = result * 59 + ($target == null ? 43 : $target.hashCode());
        return result;
    }

    public String toString() {
        return "InteractingChanged(source=" + String.valueOf(this.getSource()) + ", target=" + String.valueOf(this.getTarget()) + ")";
    }
}

