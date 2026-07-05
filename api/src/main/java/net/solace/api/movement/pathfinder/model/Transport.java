package net.solace.api.movement.pathfinder.model;

import java.util.Arrays;
import java.util.concurrent.Callable;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;

public class Transport {
    private WorldPoint source;
    private WorldPoint destination;
    private int sourceRadius;
    private int destinationRadius;
    private Callable<Boolean> handler;
    private Requirements requirements;
    private int[] itemRequirements;
    private int weight;
    private int delay;

    private static int $default$sourceRadius() {
        return 5;
    }

    private static int $default$destinationRadius() {
        return 5;
    }

    private static Requirements $default$requirements() {
        return new Requirements();
    }

    private static int[] $default$itemRequirements() {
        return null;
    }

    private static int $default$weight() {
        return 0;
    }

    private static int $default$delay() {
        return 1;
    }

    Transport(WorldPoint source, WorldPoint destination, int sourceRadius, int destinationRadius, Callable<Boolean> handler, Requirements requirements, int[] itemRequirements, int weight, int delay) {
        this.source = source;
        this.destination = destination;
        this.sourceRadius = sourceRadius;
        this.destinationRadius = destinationRadius;
        this.handler = handler;
        this.requirements = requirements;
        this.itemRequirements = itemRequirements;
        this.weight = weight;
        this.delay = delay;
    }

    public static TransportBuilder builder() {
        return new TransportBuilder();
    }

    public WorldPoint getSource() {
        return this.source;
    }

    public WorldPoint getDestination() {
        return this.destination;
    }

    public int getSourceRadius() {
        return this.sourceRadius;
    }

    public int getDestinationRadius() {
        return this.destinationRadius;
    }

    public Callable<Boolean> getHandler() {
        return this.handler;
    }

    public Requirements getRequirements() {
        return this.requirements;
    }

    public int[] getItemRequirements() {
        return this.itemRequirements;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setSource(WorldPoint source) {
        this.source = source;
    }

    public void setDestination(WorldPoint destination) {
        this.destination = destination;
    }

    public void setSourceRadius(int sourceRadius) {
        this.sourceRadius = sourceRadius;
    }

    public void setDestinationRadius(int destinationRadius) {
        this.destinationRadius = destinationRadius;
    }

    public void setHandler(Callable<Boolean> handler) {
        this.handler = handler;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public void setItemRequirements(int[] itemRequirements) {
        this.itemRequirements = itemRequirements;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Transport)) {
            return false;
        }
        Transport other = (Transport)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getSourceRadius() != other.getSourceRadius()) {
            return false;
        }
        if (this.getDestinationRadius() != other.getDestinationRadius()) {
            return false;
        }
        if (this.getWeight() != other.getWeight()) {
            return false;
        }
        if (this.getDelay() != other.getDelay()) {
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
        Callable<Boolean> this$handler = this.getHandler();
        Callable<Boolean> other$handler = other.getHandler();
        if (this$handler == null ? other$handler != null : !this$handler.equals(other$handler)) {
            return false;
        }
        Requirements this$requirements = this.getRequirements();
        Requirements other$requirements = other.getRequirements();
        if (this$requirements == null ? other$requirements != null : !((Object)this$requirements).equals(other$requirements)) {
            return false;
        }
        return Arrays.equals(this.getItemRequirements(), other.getItemRequirements());
    }

    protected boolean canEqual(Object other) {
        return other instanceof Transport;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getSourceRadius();
        result = result * 59 + this.getDestinationRadius();
        result = result * 59 + this.getWeight();
        result = result * 59 + this.getDelay();
        WorldPoint $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
        WorldPoint $destination = this.getDestination();
        result = result * 59 + ($destination == null ? 43 : $destination.hashCode());
        Callable<Boolean> $handler = this.getHandler();
        result = result * 59 + ($handler == null ? 43 : $handler.hashCode());
        Requirements $requirements = this.getRequirements();
        result = result * 59 + ($requirements == null ? 43 : ((Object)$requirements).hashCode());
        result = result * 59 + Arrays.hashCode(this.getItemRequirements());
        return result;
    }

    public String toString() {
        return "Transport(source=" + String.valueOf(this.getSource()) + ", destination=" + String.valueOf(this.getDestination()) + ", sourceRadius=" + this.getSourceRadius() + ", destinationRadius=" + this.getDestinationRadius() + ", handler=" + String.valueOf(this.getHandler()) + ", requirements=" + String.valueOf(this.getRequirements()) + ", itemRequirements=" + Arrays.toString(this.getItemRequirements()) + ", weight=" + this.getWeight() + ", delay=" + this.getDelay() + ")";
    }

    public static class TransportBuilder {
        private WorldPoint source;
        private WorldPoint destination;
        private boolean sourceRadius$set;
        private int sourceRadius$value;
        private boolean destinationRadius$set;
        private int destinationRadius$value;
        private Callable<Boolean> handler;
        private boolean requirements$set;
        private Requirements requirements$value;
        private boolean itemRequirements$set;
        private int[] itemRequirements$value;
        private boolean weight$set;
        private int weight$value;
        private boolean delay$set;
        private int delay$value;

        TransportBuilder() {
        }

        public TransportBuilder source(WorldPoint source) {
            this.source = source;
            return this;
        }

        public TransportBuilder destination(WorldPoint destination) {
            this.destination = destination;
            return this;
        }

        public TransportBuilder sourceRadius(int sourceRadius) {
            this.sourceRadius$value = sourceRadius;
            this.sourceRadius$set = true;
            return this;
        }

        public TransportBuilder destinationRadius(int destinationRadius) {
            this.destinationRadius$value = destinationRadius;
            this.destinationRadius$set = true;
            return this;
        }

        public TransportBuilder handler(Callable<Boolean> handler) {
            this.handler = handler;
            return this;
        }

        public TransportBuilder requirements(Requirements requirements) {
            this.requirements$value = requirements;
            this.requirements$set = true;
            return this;
        }

        public TransportBuilder itemRequirements(int[] itemRequirements) {
            this.itemRequirements$value = itemRequirements;
            this.itemRequirements$set = true;
            return this;
        }

        public TransportBuilder weight(int weight) {
            this.weight$value = weight;
            this.weight$set = true;
            return this;
        }

        public TransportBuilder delay(int delay) {
            this.delay$value = delay;
            this.delay$set = true;
            return this;
        }

        public Transport build() {
            int sourceRadius$value = this.sourceRadius$value;
            if (!this.sourceRadius$set) {
                sourceRadius$value = Transport.$default$sourceRadius();
            }
            int destinationRadius$value = this.destinationRadius$value;
            if (!this.destinationRadius$set) {
                destinationRadius$value = Transport.$default$destinationRadius();
            }
            Requirements requirements$value = this.requirements$value;
            if (!this.requirements$set) {
                requirements$value = Transport.$default$requirements();
            }
            int[] itemRequirements$value = this.itemRequirements$value;
            if (!this.itemRequirements$set) {
                itemRequirements$value = Transport.$default$itemRequirements();
            }
            int weight$value = this.weight$value;
            if (!this.weight$set) {
                weight$value = Transport.$default$weight();
            }
            int delay$value = this.delay$value;
            if (!this.delay$set) {
                delay$value = Transport.$default$delay();
            }
            return new Transport(this.source, this.destination, sourceRadius$value, destinationRadius$value, this.handler, requirements$value, itemRequirements$value, weight$value, delay$value);
        }

        public String toString() {
            return "Transport.TransportBuilder(source=" + String.valueOf(this.source) + ", destination=" + String.valueOf(this.destination) + ", sourceRadius$value=" + this.sourceRadius$value + ", destinationRadius$value=" + this.destinationRadius$value + ", handler=" + String.valueOf(this.handler) + ", requirements$value=" + String.valueOf(this.requirements$value) + ", itemRequirements$value=" + Arrays.toString(this.itemRequirements$value) + ", weight$value=" + this.weight$value + ", delay$value=" + this.delay$value + ")";
        }
    }
}

