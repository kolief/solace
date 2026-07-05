package net.solace.api.events;

import net.solace.api.domain.actors.IActor;

public class AnimationChanged {
    private IActor actor;

    public IActor getActor() {
        return this.actor;
    }

    public void setActor(IActor actor) {
        this.actor = actor;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnimationChanged)) {
            return false;
        }
        AnimationChanged other = (AnimationChanged)o;
        if (!other.canEqual(this)) {
            return false;
        }
        IActor this$actor = this.getActor();
        IActor other$actor = other.getActor();
        return !(this$actor == null ? other$actor != null : !this$actor.equals(other$actor));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AnimationChanged;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        IActor $actor = this.getActor();
        result = result * 59 + ($actor == null ? 43 : $actor.hashCode());
        return result;
    }

    public String toString() {
        return "AnimationChanged(actor=" + String.valueOf(this.getActor()) + ")";
    }
}

