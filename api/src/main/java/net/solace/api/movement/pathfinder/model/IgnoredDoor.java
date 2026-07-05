package net.solace.api.movement.pathfinder.model;

import java.util.Objects;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;

public class IgnoredDoor {
    private WorldPoint source;
    private WorldPoint destination;
    private Boolean requireMembers;
    private Requirements requirements;

    public boolean shouldIgnore(boolean isMembers) {
        if (this.requireMembers.booleanValue()) {
            return !isMembers;
        }
        return this.requirements == null || this.requirements.fulfilled();
    }

    public boolean blocks(WorldPoint from, WorldPoint to) {
        if (this.destination == null) {
            return Objects.equals(this.source, to);
        }
        return Objects.equals(this.source, from) && Objects.equals(this.destination, to);
    }

    private static Boolean $default$requireMembers() {
        return false;
    }

    private static Requirements $default$requirements() {
        return new Requirements();
    }

    IgnoredDoor(WorldPoint source, WorldPoint destination, Boolean requireMembers, Requirements requirements) {
        this.source = source;
        this.destination = destination;
        this.requireMembers = requireMembers;
        this.requirements = requirements;
    }

    public static IgnoredDoorBuilder builder() {
        return new IgnoredDoorBuilder();
    }

    public WorldPoint getSource() {
        return this.source;
    }

    public WorldPoint getDestination() {
        return this.destination;
    }

    public Boolean getRequireMembers() {
        return this.requireMembers;
    }

    public Requirements getRequirements() {
        return this.requirements;
    }

    public void setSource(WorldPoint source) {
        this.source = source;
    }

    public void setDestination(WorldPoint destination) {
        this.destination = destination;
    }

    public void setRequireMembers(Boolean requireMembers) {
        this.requireMembers = requireMembers;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IgnoredDoor)) {
            return false;
        }
        IgnoredDoor other = (IgnoredDoor)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Boolean this$requireMembers = this.getRequireMembers();
        Boolean other$requireMembers = other.getRequireMembers();
        if (this$requireMembers == null ? other$requireMembers != null : !((Object)this$requireMembers).equals(other$requireMembers)) {
            return false;
        }
        WorldPoint this$source = this.getSource();
        WorldPoint other$source = other.getSource();
        if (this$source == null ? other$source != null : !this$source.equals(other$source)) {
            return false;
        }
        WorldPoint this$destination = this.getDestination();
        WorldPoint other$destination = other.getDestination();
        if (this$destination == null ? other$destination != null : !this$destination.equals(other$destination)) {
            return false;
        }
        Requirements this$requirements = this.getRequirements();
        Requirements other$requirements = other.getRequirements();
        return !(this$requirements == null ? other$requirements != null : !((Object)this$requirements).equals(other$requirements));
    }

    protected boolean canEqual(Object other) {
        return other instanceof IgnoredDoor;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Boolean $requireMembers = this.getRequireMembers();
        result = result * 59 + ($requireMembers == null ? 43 : ((Object)$requireMembers).hashCode());
        WorldPoint $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
        WorldPoint $destination = this.getDestination();
        result = result * 59 + ($destination == null ? 43 : $destination.hashCode());
        Requirements $requirements = this.getRequirements();
        result = result * 59 + ($requirements == null ? 43 : ((Object)$requirements).hashCode());
        return result;
    }

    public String toString() {
        return "IgnoredDoor(source=" + String.valueOf(this.getSource()) + ", destination=" + String.valueOf(this.getDestination()) + ", requireMembers=" + this.getRequireMembers() + ", requirements=" + String.valueOf(this.getRequirements()) + ")";
    }

    public static class IgnoredDoorBuilder {
        private WorldPoint source;
        private WorldPoint destination;
        private boolean requireMembers$set;
        private Boolean requireMembers$value;
        private boolean requirements$set;
        private Requirements requirements$value;

        IgnoredDoorBuilder() {
        }

        public IgnoredDoorBuilder source(WorldPoint source) {
            this.source = source;
            return this;
        }

        public IgnoredDoorBuilder destination(WorldPoint destination) {
            this.destination = destination;
            return this;
        }

        public IgnoredDoorBuilder requireMembers(Boolean requireMembers) {
            this.requireMembers$value = requireMembers;
            this.requireMembers$set = true;
            return this;
        }

        public IgnoredDoorBuilder requirements(Requirements requirements) {
            this.requirements$value = requirements;
            this.requirements$set = true;
            return this;
        }

        public IgnoredDoor build() {
            Boolean requireMembers$value = this.requireMembers$value;
            if (!this.requireMembers$set) {
                requireMembers$value = IgnoredDoor.$default$requireMembers();
            }
            Requirements requirements$value = this.requirements$value;
            if (!this.requirements$set) {
                requirements$value = IgnoredDoor.$default$requirements();
            }
            return new IgnoredDoor(this.source, this.destination, requireMembers$value, requirements$value);
        }

        public String toString() {
            return "IgnoredDoor.IgnoredDoorBuilder(source=" + String.valueOf(this.source) + ", destination=" + String.valueOf(this.destination) + ", requireMembers$value=" + this.requireMembers$value + ", requirements$value=" + String.valueOf(this.requirements$value) + ")";
        }
    }
}

