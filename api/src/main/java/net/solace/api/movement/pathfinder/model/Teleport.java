package net.solace.api.movement.pathfinder.model;

import java.util.Arrays;
import java.util.concurrent.Callable;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;

public class Teleport {
    private WorldPoint destination;
    private int radius;
    private Callable<Boolean> handler;
    private boolean poh;
    private boolean isMinigameTeleport;
    private boolean isHomeTeleport;
    private boolean isTimedTeleport;
    private int[] itemRequirements;
    private int[] objectIdRequirements;
    private String[] objectNameRequirements;
    private Requirements requirements;
    private int priority;
    private double weight;
    private boolean forceLoad;
    private int maximumWildernessLevel;
    private int teleportDelay;
    private int walkerDelay;

    public boolean equals(Object other) {
        if (!(other instanceof Teleport)) {
            return false;
        }
        Teleport otherTeleport = (Teleport)other;
        return otherTeleport.getDestination().equals((Object)this.getDestination()) && ((Teleport)other).poh == this.poh;
    }

    public boolean isItem() {
        return this.itemRequirements != null;
    }

    public boolean isObject() {
        return this.objectIdRequirements != null || this.objectNameRequirements != null;
    }

    private static int $default$radius() {
        return 2;
    }

    private static boolean $default$poh() {
        return false;
    }

    private static boolean $default$isMinigameTeleport() {
        return false;
    }

    private static boolean $default$isHomeTeleport() {
        return false;
    }

    private static boolean $default$isTimedTeleport() {
        return false;
    }

    private static int[] $default$itemRequirements() {
        return null;
    }

    private static int[] $default$objectIdRequirements() {
        return null;
    }

    private static String[] $default$objectNameRequirements() {
        return null;
    }

    private static Requirements $default$requirements() {
        return new Requirements();
    }

    private static int $default$priority() {
        return 1000;
    }

    private static double $default$weight() {
        return 0.0;
    }

    private static boolean $default$forceLoad() {
        return false;
    }

    private static int $default$maximumWildernessLevel() {
        return 20;
    }

    private static int $default$teleportDelay() {
        return 0;
    }

    private static int $default$walkerDelay() {
        return 0;
    }

    Teleport(WorldPoint destination, int radius, Callable<Boolean> handler, boolean poh, boolean isMinigameTeleport, boolean isHomeTeleport, boolean isTimedTeleport, int[] itemRequirements, int[] objectIdRequirements, String[] objectNameRequirements, Requirements requirements, int priority, double weight, boolean forceLoad, int maximumWildernessLevel, int teleportDelay, int walkerDelay) {
        this.destination = destination;
        this.radius = radius;
        this.handler = handler;
        this.poh = poh;
        this.isMinigameTeleport = isMinigameTeleport;
        this.isHomeTeleport = isHomeTeleport;
        this.isTimedTeleport = isTimedTeleport;
        this.itemRequirements = itemRequirements;
        this.objectIdRequirements = objectIdRequirements;
        this.objectNameRequirements = objectNameRequirements;
        this.requirements = requirements;
        this.priority = priority;
        this.weight = weight;
        this.forceLoad = forceLoad;
        this.maximumWildernessLevel = maximumWildernessLevel;
        this.teleportDelay = teleportDelay;
        this.walkerDelay = walkerDelay;
    }

    public static TeleportBuilder builder() {
        return new TeleportBuilder();
    }

    public WorldPoint getDestination() {
        return this.destination;
    }

    public int getRadius() {
        return this.radius;
    }

    public Callable<Boolean> getHandler() {
        return this.handler;
    }

    public boolean isPoh() {
        return this.poh;
    }

    public boolean isMinigameTeleport() {
        return this.isMinigameTeleport;
    }

    public boolean isHomeTeleport() {
        return this.isHomeTeleport;
    }

    public boolean isTimedTeleport() {
        return this.isTimedTeleport;
    }

    public int[] getItemRequirements() {
        return this.itemRequirements;
    }

    public int[] getObjectIdRequirements() {
        return this.objectIdRequirements;
    }

    public String[] getObjectNameRequirements() {
        return this.objectNameRequirements;
    }

    public Requirements getRequirements() {
        return this.requirements;
    }

    public int getPriority() {
        return this.priority;
    }

    public double getWeight() {
        return this.weight;
    }

    public boolean isForceLoad() {
        return this.forceLoad;
    }

    public int getMaximumWildernessLevel() {
        return this.maximumWildernessLevel;
    }

    public int getTeleportDelay() {
        return this.teleportDelay;
    }

    public int getWalkerDelay() {
        return this.walkerDelay;
    }

    public void setDestination(WorldPoint destination) {
        this.destination = destination;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setHandler(Callable<Boolean> handler) {
        this.handler = handler;
    }

    public void setPoh(boolean poh) {
        this.poh = poh;
    }

    public void setMinigameTeleport(boolean isMinigameTeleport) {
        this.isMinigameTeleport = isMinigameTeleport;
    }

    public void setHomeTeleport(boolean isHomeTeleport) {
        this.isHomeTeleport = isHomeTeleport;
    }

    public void setTimedTeleport(boolean isTimedTeleport) {
        this.isTimedTeleport = isTimedTeleport;
    }

    public void setItemRequirements(int[] itemRequirements) {
        this.itemRequirements = itemRequirements;
    }

    public void setObjectIdRequirements(int[] objectIdRequirements) {
        this.objectIdRequirements = objectIdRequirements;
    }

    public void setObjectNameRequirements(String[] objectNameRequirements) {
        this.objectNameRequirements = objectNameRequirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setForceLoad(boolean forceLoad) {
        this.forceLoad = forceLoad;
    }

    public void setMaximumWildernessLevel(int maximumWildernessLevel) {
        this.maximumWildernessLevel = maximumWildernessLevel;
    }

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
    }

    public void setWalkerDelay(int walkerDelay) {
        this.walkerDelay = walkerDelay;
    }

    public String toString() {
        return "Teleport(destination=" + String.valueOf(this.getDestination()) + ", radius=" + this.getRadius() + ", handler=" + String.valueOf(this.getHandler()) + ", poh=" + this.isPoh() + ", isMinigameTeleport=" + this.isMinigameTeleport() + ", isHomeTeleport=" + this.isHomeTeleport() + ", isTimedTeleport=" + this.isTimedTeleport() + ", itemRequirements=" + Arrays.toString(this.getItemRequirements()) + ", objectIdRequirements=" + Arrays.toString(this.getObjectIdRequirements()) + ", objectNameRequirements=" + Arrays.deepToString(this.getObjectNameRequirements()) + ", requirements=" + String.valueOf(this.getRequirements()) + ", priority=" + this.getPriority() + ", weight=" + this.getWeight() + ", forceLoad=" + this.isForceLoad() + ", maximumWildernessLevel=" + this.getMaximumWildernessLevel() + ", teleportDelay=" + this.getTeleportDelay() + ", walkerDelay=" + this.getWalkerDelay() + ")";
    }

    public static class TeleportBuilder {
        private WorldPoint destination;
        private boolean radius$set;
        private int radius$value;
        private Callable<Boolean> handler;
        private boolean poh$set;
        private boolean poh$value;
        private boolean isMinigameTeleport$set;
        private boolean isMinigameTeleport$value;
        private boolean isHomeTeleport$set;
        private boolean isHomeTeleport$value;
        private boolean isTimedTeleport$set;
        private boolean isTimedTeleport$value;
        private boolean itemRequirements$set;
        private int[] itemRequirements$value;
        private boolean objectIdRequirements$set;
        private int[] objectIdRequirements$value;
        private boolean objectNameRequirements$set;
        private String[] objectNameRequirements$value;
        private boolean requirements$set;
        private Requirements requirements$value;
        private boolean priority$set;
        private int priority$value;
        private boolean weight$set;
        private double weight$value;
        private boolean forceLoad$set;
        private boolean forceLoad$value;
        private boolean maximumWildernessLevel$set;
        private int maximumWildernessLevel$value;
        private boolean teleportDelay$set;
        private int teleportDelay$value;
        private boolean walkerDelay$set;
        private int walkerDelay$value;

        TeleportBuilder() {
        }

        public TeleportBuilder destination(WorldPoint destination) {
            this.destination = destination;
            return this;
        }

        public TeleportBuilder radius(int radius) {
            this.radius$value = radius;
            this.radius$set = true;
            return this;
        }

        public TeleportBuilder handler(Callable<Boolean> handler) {
            this.handler = handler;
            return this;
        }

        public TeleportBuilder poh(boolean poh) {
            this.poh$value = poh;
            this.poh$set = true;
            return this;
        }

        public TeleportBuilder isMinigameTeleport(boolean isMinigameTeleport) {
            this.isMinigameTeleport$value = isMinigameTeleport;
            this.isMinigameTeleport$set = true;
            return this;
        }

        public TeleportBuilder isHomeTeleport(boolean isHomeTeleport) {
            this.isHomeTeleport$value = isHomeTeleport;
            this.isHomeTeleport$set = true;
            return this;
        }

        public TeleportBuilder isTimedTeleport(boolean isTimedTeleport) {
            this.isTimedTeleport$value = isTimedTeleport;
            this.isTimedTeleport$set = true;
            return this;
        }

        public TeleportBuilder itemRequirements(int[] itemRequirements) {
            this.itemRequirements$value = itemRequirements;
            this.itemRequirements$set = true;
            return this;
        }

        public TeleportBuilder objectIdRequirements(int[] objectIdRequirements) {
            this.objectIdRequirements$value = objectIdRequirements;
            this.objectIdRequirements$set = true;
            return this;
        }

        public TeleportBuilder objectNameRequirements(String[] objectNameRequirements) {
            this.objectNameRequirements$value = objectNameRequirements;
            this.objectNameRequirements$set = true;
            return this;
        }

        public TeleportBuilder requirements(Requirements requirements) {
            this.requirements$value = requirements;
            this.requirements$set = true;
            return this;
        }

        public TeleportBuilder priority(int priority) {
            this.priority$value = priority;
            this.priority$set = true;
            return this;
        }

        public TeleportBuilder weight(double weight) {
            this.weight$value = weight;
            this.weight$set = true;
            return this;
        }

        public TeleportBuilder forceLoad(boolean forceLoad) {
            this.forceLoad$value = forceLoad;
            this.forceLoad$set = true;
            return this;
        }

        public TeleportBuilder maximumWildernessLevel(int maximumWildernessLevel) {
            this.maximumWildernessLevel$value = maximumWildernessLevel;
            this.maximumWildernessLevel$set = true;
            return this;
        }

        public TeleportBuilder teleportDelay(int teleportDelay) {
            this.teleportDelay$value = teleportDelay;
            this.teleportDelay$set = true;
            return this;
        }

        public TeleportBuilder walkerDelay(int walkerDelay) {
            this.walkerDelay$value = walkerDelay;
            this.walkerDelay$set = true;
            return this;
        }

        public Teleport build() {
            int radius$value = this.radius$value;
            if (!this.radius$set) {
                radius$value = Teleport.$default$radius();
            }
            boolean poh$value = this.poh$value;
            if (!this.poh$set) {
                poh$value = Teleport.$default$poh();
            }
            boolean isMinigameTeleport$value = this.isMinigameTeleport$value;
            if (!this.isMinigameTeleport$set) {
                isMinigameTeleport$value = Teleport.$default$isMinigameTeleport();
            }
            boolean isHomeTeleport$value = this.isHomeTeleport$value;
            if (!this.isHomeTeleport$set) {
                isHomeTeleport$value = Teleport.$default$isHomeTeleport();
            }
            boolean isTimedTeleport$value = this.isTimedTeleport$value;
            if (!this.isTimedTeleport$set) {
                isTimedTeleport$value = Teleport.$default$isTimedTeleport();
            }
            int[] itemRequirements$value = this.itemRequirements$value;
            if (!this.itemRequirements$set) {
                itemRequirements$value = Teleport.$default$itemRequirements();
            }
            int[] objectIdRequirements$value = this.objectIdRequirements$value;
            if (!this.objectIdRequirements$set) {
                objectIdRequirements$value = Teleport.$default$objectIdRequirements();
            }
            String[] objectNameRequirements$value = this.objectNameRequirements$value;
            if (!this.objectNameRequirements$set) {
                objectNameRequirements$value = Teleport.$default$objectNameRequirements();
            }
            Requirements requirements$value = this.requirements$value;
            if (!this.requirements$set) {
                requirements$value = Teleport.$default$requirements();
            }
            int priority$value = this.priority$value;
            if (!this.priority$set) {
                priority$value = Teleport.$default$priority();
            }
            double weight$value = this.weight$value;
            if (!this.weight$set) {
                weight$value = Teleport.$default$weight();
            }
            boolean forceLoad$value = this.forceLoad$value;
            if (!this.forceLoad$set) {
                forceLoad$value = Teleport.$default$forceLoad();
            }
            int maximumWildernessLevel$value = this.maximumWildernessLevel$value;
            if (!this.maximumWildernessLevel$set) {
                maximumWildernessLevel$value = Teleport.$default$maximumWildernessLevel();
            }
            int teleportDelay$value = this.teleportDelay$value;
            if (!this.teleportDelay$set) {
                teleportDelay$value = Teleport.$default$teleportDelay();
            }
            int walkerDelay$value = this.walkerDelay$value;
            if (!this.walkerDelay$set) {
                walkerDelay$value = Teleport.$default$walkerDelay();
            }
            return new Teleport(this.destination, radius$value, this.handler, poh$value, isMinigameTeleport$value, isHomeTeleport$value, isTimedTeleport$value, itemRequirements$value, objectIdRequirements$value, objectNameRequirements$value, requirements$value, priority$value, weight$value, forceLoad$value, maximumWildernessLevel$value, teleportDelay$value, walkerDelay$value);
        }

        public String toString() {
            return "Teleport.TeleportBuilder(destination=" + String.valueOf(this.destination) + ", radius$value=" + this.radius$value + ", handler=" + String.valueOf(this.handler) + ", poh$value=" + this.poh$value + ", isMinigameTeleport$value=" + this.isMinigameTeleport$value + ", isHomeTeleport$value=" + this.isHomeTeleport$value + ", isTimedTeleport$value=" + this.isTimedTeleport$value + ", itemRequirements$value=" + Arrays.toString(this.itemRequirements$value) + ", objectIdRequirements$value=" + Arrays.toString(this.objectIdRequirements$value) + ", objectNameRequirements$value=" + Arrays.deepToString(this.objectNameRequirements$value) + ", requirements$value=" + String.valueOf(this.requirements$value) + ", priority$value=" + this.priority$value + ", weight$value=" + this.weight$value + ", forceLoad$value=" + this.forceLoad$value + ", maximumWildernessLevel$value=" + this.maximumWildernessLevel$value + ", teleportDelay$value=" + this.teleportDelay$value + ", walkerDelay$value=" + this.walkerDelay$value + ")";
        }
    }
}

