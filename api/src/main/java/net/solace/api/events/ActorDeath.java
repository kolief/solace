package net.solace.api.events;

import net.solace.api.domain.actors.IActor;

public final class ActorDeath {
    private final IActor actor;

    public ActorDeath(IActor actor) {
        this.actor = actor;
    }

    public IActor getActor() {
        return this.actor;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ActorDeath)) {
            return false;
        }
        ActorDeath other = (ActorDeath)o;
        IActor this$actor = this.getActor();
        IActor other$actor = other.getActor();
        return !(this$actor == null ? other$actor != null : !this$actor.equals(other$actor));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        IActor $actor = this.getActor();
        result = result * 59 + ($actor == null ? 43 : $actor.hashCode());
        return result;
    }

    public String toString() {
        return "ActorDeath(actor=" + String.valueOf(this.getActor()) + ")";
    }
}

