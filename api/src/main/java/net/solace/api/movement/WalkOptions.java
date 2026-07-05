package net.solace.api.movement;

import java.util.List;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.CollisionMap;

public final class WalkOptions {
    private final boolean avoidWilderness;
    private final boolean toggleRun;
    private final boolean useTransports;
    private final boolean useTeleports;
    private final boolean useHomeTeleports;
    private final boolean useMinigameTeleports;
    private final boolean usePoh;
    private final boolean useCharterShips;
    private final boolean useGnomeGliders;
    private final boolean useMagicCarpets;
    private final boolean useCache;
    private final List<Integer> cachedItems;
    private final CollisionMap collisionMap;
    private final int minStepDistance;
    private final int maxStepDistance;
    private final int minimapClickChance;
    private final boolean allowPathDeviation;

    private static boolean $default$avoidWilderness() {
        return Static.getSolaceConfig().avoidWilderness();
    }

    private static boolean $default$toggleRun() {
        return Static.getSolaceConfig().toggleRun();
    }

    private static boolean $default$useTransports() {
        return Static.getSolaceConfig().useTransports();
    }

    private static boolean $default$useTeleports() {
        return Static.getSolaceConfig().useTeleports();
    }

    private static boolean $default$useHomeTeleports() {
        return Static.getSolaceConfig().useHomeTeleports();
    }

    private static boolean $default$useMinigameTeleports() {
        return Static.getSolaceConfig().useMinigameTeleports();
    }

    private static boolean $default$usePoh() {
        return Static.getSolaceConfig().usePoh();
    }

    private static boolean $default$useCharterShips() {
        return Static.getSolaceConfig().useCharterShips();
    }

    private static boolean $default$useGnomeGliders() {
        return Static.getSolaceConfig().useGnomeGliders();
    }

    private static boolean $default$useMagicCarpets() {
        return Static.getSolaceConfig().useMagicCarpets();
    }

    private static boolean $default$useCache() {
        return false;
    }

    private static List<Integer> $default$cachedItems() {
        return List.of();
    }

    private static CollisionMap $default$collisionMap() {
        return Static.getGlobalCollisionMap();
    }

    private static int $default$minStepDistance() {
        return Static.getSolaceConfig().minStepDistance();
    }

    private static int $default$maxStepDistance() {
        return Static.getSolaceConfig().maxStepDistance();
    }

    private static int $default$minimapClickChance() {
        return Static.getSolaceConfig().minimapChance();
    }

    private static boolean $default$allowPathDeviation() {
        return Static.getSolaceConfig().allowPathDeviation();
    }

    WalkOptions(boolean avoidWilderness, boolean toggleRun, boolean useTransports, boolean useTeleports, boolean useHomeTeleports, boolean useMinigameTeleports, boolean usePoh, boolean useCharterShips, boolean useGnomeGliders, boolean useMagicCarpets, boolean useCache, List<Integer> cachedItems, CollisionMap collisionMap, int minStepDistance, int maxStepDistance, int minimapClickChance, boolean allowPathDeviation) {
        this.avoidWilderness = avoidWilderness;
        this.toggleRun = toggleRun;
        this.useTransports = useTransports;
        this.useTeleports = useTeleports;
        this.useHomeTeleports = useHomeTeleports;
        this.useMinigameTeleports = useMinigameTeleports;
        this.usePoh = usePoh;
        this.useCharterShips = useCharterShips;
        this.useGnomeGliders = useGnomeGliders;
        this.useMagicCarpets = useMagicCarpets;
        this.useCache = useCache;
        this.cachedItems = cachedItems;
        this.collisionMap = collisionMap;
        this.minStepDistance = minStepDistance;
        this.maxStepDistance = maxStepDistance;
        this.minimapClickChance = minimapClickChance;
        this.allowPathDeviation = allowPathDeviation;
    }

    public static WalkOptionsBuilder builder() {
        return new WalkOptionsBuilder();
    }

    public WalkOptionsBuilder toBuilder() {
        return new WalkOptionsBuilder().avoidWilderness(this.avoidWilderness).toggleRun(this.toggleRun).useTransports(this.useTransports).useTeleports(this.useTeleports).useHomeTeleports(this.useHomeTeleports).useMinigameTeleports(this.useMinigameTeleports).usePoh(this.usePoh).useCharterShips(this.useCharterShips).useGnomeGliders(this.useGnomeGliders).useMagicCarpets(this.useMagicCarpets).useCache(this.useCache).cachedItems(this.cachedItems).collisionMap(this.collisionMap).minStepDistance(this.minStepDistance).maxStepDistance(this.maxStepDistance).minimapClickChance(this.minimapClickChance).allowPathDeviation(this.allowPathDeviation);
    }

    public boolean isAvoidWilderness() {
        return this.avoidWilderness;
    }

    public boolean isToggleRun() {
        return this.toggleRun;
    }

    public boolean isUseTransports() {
        return this.useTransports;
    }

    public boolean isUseTeleports() {
        return this.useTeleports;
    }

    public boolean isUseHomeTeleports() {
        return this.useHomeTeleports;
    }

    public boolean isUseMinigameTeleports() {
        return this.useMinigameTeleports;
    }

    public boolean isUsePoh() {
        return this.usePoh;
    }

    public boolean isUseCharterShips() {
        return this.useCharterShips;
    }

    public boolean isUseGnomeGliders() {
        return this.useGnomeGliders;
    }

    public boolean isUseMagicCarpets() {
        return this.useMagicCarpets;
    }

    public boolean isUseCache() {
        return this.useCache;
    }

    public List<Integer> getCachedItems() {
        return this.cachedItems;
    }

    public CollisionMap getCollisionMap() {
        return this.collisionMap;
    }

    public int getMinStepDistance() {
        return this.minStepDistance;
    }

    public int getMaxStepDistance() {
        return this.maxStepDistance;
    }

    public int getMinimapClickChance() {
        return this.minimapClickChance;
    }

    public boolean isAllowPathDeviation() {
        return this.allowPathDeviation;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WalkOptions)) {
            return false;
        }
        WalkOptions other = (WalkOptions)o;
        if (this.isAvoidWilderness() != other.isAvoidWilderness()) {
            return false;
        }
        if (this.isToggleRun() != other.isToggleRun()) {
            return false;
        }
        if (this.isUseTransports() != other.isUseTransports()) {
            return false;
        }
        if (this.isUseTeleports() != other.isUseTeleports()) {
            return false;
        }
        if (this.isUseHomeTeleports() != other.isUseHomeTeleports()) {
            return false;
        }
        if (this.isUseMinigameTeleports() != other.isUseMinigameTeleports()) {
            return false;
        }
        if (this.isUsePoh() != other.isUsePoh()) {
            return false;
        }
        if (this.isUseCharterShips() != other.isUseCharterShips()) {
            return false;
        }
        if (this.isUseGnomeGliders() != other.isUseGnomeGliders()) {
            return false;
        }
        if (this.isUseMagicCarpets() != other.isUseMagicCarpets()) {
            return false;
        }
        if (this.isUseCache() != other.isUseCache()) {
            return false;
        }
        if (this.getMinStepDistance() != other.getMinStepDistance()) {
            return false;
        }
        if (this.getMaxStepDistance() != other.getMaxStepDistance()) {
            return false;
        }
        if (this.getMinimapClickChance() != other.getMinimapClickChance()) {
            return false;
        }
        if (this.isAllowPathDeviation() != other.isAllowPathDeviation()) {
            return false;
        }
        List<Integer> this$cachedItems = this.getCachedItems();
        List<Integer> other$cachedItems = other.getCachedItems();
        if (this$cachedItems == null ? other$cachedItems != null : !((Object)this$cachedItems).equals(other$cachedItems)) {
            return false;
        }
        CollisionMap this$collisionMap = this.getCollisionMap();
        CollisionMap other$collisionMap = other.getCollisionMap();
        return !(this$collisionMap == null ? other$collisionMap != null : !this$collisionMap.equals(other$collisionMap));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isAvoidWilderness() ? 79 : 97);
        result = result * 59 + (this.isToggleRun() ? 79 : 97);
        result = result * 59 + (this.isUseTransports() ? 79 : 97);
        result = result * 59 + (this.isUseTeleports() ? 79 : 97);
        result = result * 59 + (this.isUseHomeTeleports() ? 79 : 97);
        result = result * 59 + (this.isUseMinigameTeleports() ? 79 : 97);
        result = result * 59 + (this.isUsePoh() ? 79 : 97);
        result = result * 59 + (this.isUseCharterShips() ? 79 : 97);
        result = result * 59 + (this.isUseGnomeGliders() ? 79 : 97);
        result = result * 59 + (this.isUseMagicCarpets() ? 79 : 97);
        result = result * 59 + (this.isUseCache() ? 79 : 97);
        result = result * 59 + this.getMinStepDistance();
        result = result * 59 + this.getMaxStepDistance();
        result = result * 59 + this.getMinimapClickChance();
        result = result * 59 + (this.isAllowPathDeviation() ? 79 : 97);
        List<Integer> $cachedItems = this.getCachedItems();
        result = result * 59 + ($cachedItems == null ? 43 : ((Object)$cachedItems).hashCode());
        CollisionMap $collisionMap = this.getCollisionMap();
        result = result * 59 + ($collisionMap == null ? 43 : $collisionMap.hashCode());
        return result;
    }

    public String toString() {
        return "WalkOptions(avoidWilderness=" + this.isAvoidWilderness() + ", toggleRun=" + this.isToggleRun() + ", useTransports=" + this.isUseTransports() + ", useTeleports=" + this.isUseTeleports() + ", useHomeTeleports=" + this.isUseHomeTeleports() + ", useMinigameTeleports=" + this.isUseMinigameTeleports() + ", usePoh=" + this.isUsePoh() + ", useCharterShips=" + this.isUseCharterShips() + ", useGnomeGliders=" + this.isUseGnomeGliders() + ", useMagicCarpets=" + this.isUseMagicCarpets() + ", useCache=" + this.isUseCache() + ", cachedItems=" + String.valueOf(this.getCachedItems()) + ", collisionMap=" + String.valueOf(this.getCollisionMap()) + ", minStepDistance=" + this.getMinStepDistance() + ", maxStepDistance=" + this.getMaxStepDistance() + ", minimapClickChance=" + this.getMinimapClickChance() + ", allowPathDeviation=" + this.isAllowPathDeviation() + ")";
    }

    public static class WalkOptionsBuilder {
        private boolean avoidWilderness$set;
        private boolean avoidWilderness$value;
        private boolean toggleRun$set;
        private boolean toggleRun$value;
        private boolean useTransports$set;
        private boolean useTransports$value;
        private boolean useTeleports$set;
        private boolean useTeleports$value;
        private boolean useHomeTeleports$set;
        private boolean useHomeTeleports$value;
        private boolean useMinigameTeleports$set;
        private boolean useMinigameTeleports$value;
        private boolean usePoh$set;
        private boolean usePoh$value;
        private boolean useCharterShips$set;
        private boolean useCharterShips$value;
        private boolean useGnomeGliders$set;
        private boolean useGnomeGliders$value;
        private boolean useMagicCarpets$set;
        private boolean useMagicCarpets$value;
        private boolean useCache$set;
        private boolean useCache$value;
        private boolean cachedItems$set;
        private List<Integer> cachedItems$value;
        private boolean collisionMap$set;
        private CollisionMap collisionMap$value;
        private boolean minStepDistance$set;
        private int minStepDistance$value;
        private boolean maxStepDistance$set;
        private int maxStepDistance$value;
        private boolean minimapClickChance$set;
        private int minimapClickChance$value;
        private boolean allowPathDeviation$set;
        private boolean allowPathDeviation$value;

        WalkOptionsBuilder() {
        }

        public WalkOptionsBuilder avoidWilderness(boolean avoidWilderness) {
            this.avoidWilderness$value = avoidWilderness;
            this.avoidWilderness$set = true;
            return this;
        }

        public WalkOptionsBuilder toggleRun(boolean toggleRun) {
            this.toggleRun$value = toggleRun;
            this.toggleRun$set = true;
            return this;
        }

        public WalkOptionsBuilder useTransports(boolean useTransports) {
            this.useTransports$value = useTransports;
            this.useTransports$set = true;
            return this;
        }

        public WalkOptionsBuilder useTeleports(boolean useTeleports) {
            this.useTeleports$value = useTeleports;
            this.useTeleports$set = true;
            return this;
        }

        public WalkOptionsBuilder useHomeTeleports(boolean useHomeTeleports) {
            this.useHomeTeleports$value = useHomeTeleports;
            this.useHomeTeleports$set = true;
            return this;
        }

        public WalkOptionsBuilder useMinigameTeleports(boolean useMinigameTeleports) {
            this.useMinigameTeleports$value = useMinigameTeleports;
            this.useMinigameTeleports$set = true;
            return this;
        }

        public WalkOptionsBuilder usePoh(boolean usePoh) {
            this.usePoh$value = usePoh;
            this.usePoh$set = true;
            return this;
        }

        public WalkOptionsBuilder useCharterShips(boolean useCharterShips) {
            this.useCharterShips$value = useCharterShips;
            this.useCharterShips$set = true;
            return this;
        }

        public WalkOptionsBuilder useGnomeGliders(boolean useGnomeGliders) {
            this.useGnomeGliders$value = useGnomeGliders;
            this.useGnomeGliders$set = true;
            return this;
        }

        public WalkOptionsBuilder useMagicCarpets(boolean useMagicCarpets) {
            this.useMagicCarpets$value = useMagicCarpets;
            this.useMagicCarpets$set = true;
            return this;
        }

        public WalkOptionsBuilder useCache(boolean useCache) {
            this.useCache$value = useCache;
            this.useCache$set = true;
            return this;
        }

        public WalkOptionsBuilder cachedItems(List<Integer> cachedItems) {
            this.cachedItems$value = cachedItems;
            this.cachedItems$set = true;
            return this;
        }

        public WalkOptionsBuilder collisionMap(CollisionMap collisionMap) {
            this.collisionMap$value = collisionMap;
            this.collisionMap$set = true;
            return this;
        }

        public WalkOptionsBuilder minStepDistance(int minStepDistance) {
            this.minStepDistance$value = minStepDistance;
            this.minStepDistance$set = true;
            return this;
        }

        public WalkOptionsBuilder maxStepDistance(int maxStepDistance) {
            this.maxStepDistance$value = maxStepDistance;
            this.maxStepDistance$set = true;
            return this;
        }

        public WalkOptionsBuilder minimapClickChance(int minimapClickChance) {
            this.minimapClickChance$value = minimapClickChance;
            this.minimapClickChance$set = true;
            return this;
        }

        public WalkOptionsBuilder allowPathDeviation(boolean allowPathDeviation) {
            this.allowPathDeviation$value = allowPathDeviation;
            this.allowPathDeviation$set = true;
            return this;
        }

        public WalkOptions build() {
            boolean avoidWilderness$value = this.avoidWilderness$value;
            if (!this.avoidWilderness$set) {
                avoidWilderness$value = WalkOptions.$default$avoidWilderness();
            }
            boolean toggleRun$value = this.toggleRun$value;
            if (!this.toggleRun$set) {
                toggleRun$value = WalkOptions.$default$toggleRun();
            }
            boolean useTransports$value = this.useTransports$value;
            if (!this.useTransports$set) {
                useTransports$value = WalkOptions.$default$useTransports();
            }
            boolean useTeleports$value = this.useTeleports$value;
            if (!this.useTeleports$set) {
                useTeleports$value = WalkOptions.$default$useTeleports();
            }
            boolean useHomeTeleports$value = this.useHomeTeleports$value;
            if (!this.useHomeTeleports$set) {
                useHomeTeleports$value = WalkOptions.$default$useHomeTeleports();
            }
            boolean useMinigameTeleports$value = this.useMinigameTeleports$value;
            if (!this.useMinigameTeleports$set) {
                useMinigameTeleports$value = WalkOptions.$default$useMinigameTeleports();
            }
            boolean usePoh$value = this.usePoh$value;
            if (!this.usePoh$set) {
                usePoh$value = WalkOptions.$default$usePoh();
            }
            boolean useCharterShips$value = this.useCharterShips$value;
            if (!this.useCharterShips$set) {
                useCharterShips$value = WalkOptions.$default$useCharterShips();
            }
            boolean useGnomeGliders$value = this.useGnomeGliders$value;
            if (!this.useGnomeGliders$set) {
                useGnomeGliders$value = WalkOptions.$default$useGnomeGliders();
            }
            boolean useMagicCarpets$value = this.useMagicCarpets$value;
            if (!this.useMagicCarpets$set) {
                useMagicCarpets$value = WalkOptions.$default$useMagicCarpets();
            }
            boolean useCache$value = this.useCache$value;
            if (!this.useCache$set) {
                useCache$value = WalkOptions.$default$useCache();
            }
            List<Integer> cachedItems$value = this.cachedItems$value;
            if (!this.cachedItems$set) {
                cachedItems$value = WalkOptions.$default$cachedItems();
            }
            CollisionMap collisionMap$value = this.collisionMap$value;
            if (!this.collisionMap$set) {
                collisionMap$value = WalkOptions.$default$collisionMap();
            }
            int minStepDistance$value = this.minStepDistance$value;
            if (!this.minStepDistance$set) {
                minStepDistance$value = WalkOptions.$default$minStepDistance();
            }
            int maxStepDistance$value = this.maxStepDistance$value;
            if (!this.maxStepDistance$set) {
                maxStepDistance$value = WalkOptions.$default$maxStepDistance();
            }
            int minimapClickChance$value = this.minimapClickChance$value;
            if (!this.minimapClickChance$set) {
                minimapClickChance$value = WalkOptions.$default$minimapClickChance();
            }
            boolean allowPathDeviation$value = this.allowPathDeviation$value;
            if (!this.allowPathDeviation$set) {
                allowPathDeviation$value = WalkOptions.$default$allowPathDeviation();
            }
            return new WalkOptions(avoidWilderness$value, toggleRun$value, useTransports$value, useTeleports$value, useHomeTeleports$value, useMinigameTeleports$value, usePoh$value, useCharterShips$value, useGnomeGliders$value, useMagicCarpets$value, useCache$value, cachedItems$value, collisionMap$value, minStepDistance$value, maxStepDistance$value, minimapClickChance$value, allowPathDeviation$value);
        }

        public String toString() {
            return "WalkOptions.WalkOptionsBuilder(avoidWilderness$value=" + this.avoidWilderness$value + ", toggleRun$value=" + this.toggleRun$value + ", useTransports$value=" + this.useTransports$value + ", useTeleports$value=" + this.useTeleports$value + ", useHomeTeleports$value=" + this.useHomeTeleports$value + ", useMinigameTeleports$value=" + this.useMinigameTeleports$value + ", usePoh$value=" + this.usePoh$value + ", useCharterShips$value=" + this.useCharterShips$value + ", useGnomeGliders$value=" + this.useGnomeGliders$value + ", useMagicCarpets$value=" + this.useMagicCarpets$value + ", useCache$value=" + this.useCache$value + ", cachedItems$value=" + String.valueOf(this.cachedItems$value) + ", collisionMap$value=" + String.valueOf(this.collisionMap$value) + ", minStepDistance$value=" + this.minStepDistance$value + ", maxStepDistance$value=" + this.maxStepDistance$value + ", minimapClickChance$value=" + this.minimapClickChance$value + ", allowPathDeviation$value=" + this.allowPathDeviation$value + ")";
        }
    }
}

