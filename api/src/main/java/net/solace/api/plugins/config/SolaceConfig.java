package net.solace.api.plugins.config;

import java.util.Set;
import net.solace.api.movement.pathfinder.model.FairyRing;
import net.solace.api.movement.pathfinder.model.MagicMushtree;
import net.solace.api.movement.pathfinder.model.SpiritTree;
import net.solace.api.movement.pathfinder.model.poh.HousePortal;
import net.solace.api.movement.pathfinder.model.poh.JewelryBox;
import net.solace.api.movement.pathfinder.model.poh.SpiritFairyTree;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigSection;
import net.solace.api.plugins.config.Range;
import net.solace.api.plugins.config.Units;

@ConfigGroup(value="solace")
public interface SolaceConfig
extends Config {
    public static final String CONFIG_GROUP = "solace";
    @ConfigSection(name="Mouse movement", description="Enables moving the mouse before attempting to interact with objects", position=0)
    public static final String mouseMovement = "mouseMovement";
    @ConfigSection(name="Pathfinder", position=1, description="")
    public static final String pathfinderSection = "Pathfinder";
    @ConfigSection(name="Fairy Rings", position=2, description="", closedByDefault=true)
    public static final String fairyRingSection = "Fairy Rings";
    @ConfigSection(name="Spirit Trees", position=2, description="", closedByDefault=true)
    public static final String spiritTreeSection = "Spirit Trees";
    @ConfigSection(name="Magic Mushtrees", position=2, description="", closedByDefault=true)
    public static final String magicMushtreeSection = "Magic Mushtrees";
    @ConfigSection(name="House Portals", position=3, description="", closedByDefault=true)
    public static final String housePortalSection = "House Portals";
    @ConfigSection(name="Other settings", position=4, description="")
    public static final String otherSettings = "Other settings";

    @ConfigItem(keyName="minStepDistance", name="Min. step distance", description="", section="Pathfinder", position=0)
    default public int minStepDistance() {
        return 7;
    }

    @ConfigItem(keyName="maxStepDistance", name="Max. step distance", description="", position=1, section="Pathfinder")
    default public int maxStepDistance() {
        return 14;
    }

    @ConfigItem(keyName="minimapChance", name="Minimap click chance", description="Chance to click the minimap instead of walking directly to the destination", position=2, section="Pathfinder")
    default public int minimapChance() {
        return 100;
    }

    @ConfigItem(keyName="avoidWilderness", name="Avoid Wilderness", description="Avoids walking in the wilderness if the destination is not in the wildy", position=3, section="Pathfinder")
    default public boolean avoidWilderness() {
        return true;
    }

    @ConfigItem(keyName="proceedWarning", name="Auto proceed warning dialogs", description="Automatically clicks proceed on warning dialogs when entering dangerous caves", position=4, section="Pathfinder")
    default public boolean proceedWarning() {
        return true;
    }

    @ConfigItem(keyName="allowPathDeviation", name="Allow path deviation", description="Allows the walker to deviate from the calculated path to click on more accessible tiles", position=5, section="Pathfinder")
    default public boolean allowPathDeviation() {
        return false;
    }

    @ConfigItem(keyName="toggleRun", name="Toggle run", description="", position=6, section="Pathfinder")
    default public boolean toggleRun() {
        return true;
    }

    @ConfigItem(keyName="useTransports", name="Use transports", description="Include transport nodes when calculating paths", position=7, section="Pathfinder")
    default public boolean useTransports() {
        return true;
    }

    @ConfigItem(keyName="useCharterShips", name="Use charter ships", description="Include charter ships when calculating paths", position=8, section="Pathfinder")
    default public boolean useCharterShips() {
        return true;
    }

    @ConfigItem(keyName="useGnomeGliders", name="Use gnome gliders", description="Include gnome gliders when calculating paths", position=9, section="Pathfinder")
    default public boolean useGnomeGliders() {
        return true;
    }

    @ConfigItem(keyName="useMagicCarpets", name="Use magic carpets", description="Include magic carpets when calculating paths", position=10, section="Pathfinder")
    default public boolean useMagicCarpets() {
        return true;
    }

    @ConfigItem(keyName="useTeleports", name="Use teleports", description="Include teleportation when calculating paths", position=11, section="Pathfinder")
    default public boolean useTeleports() {
        return true;
    }

    @ConfigItem(keyName="useHomeTeleports", name="Use home teleports", description="", position=12, section="Pathfinder")
    default public boolean useHomeTeleports() {
        return true;
    }

    @ConfigItem(keyName="useMinigameTeleports", name="Use minigames teleports", description="", position=13, section="Pathfinder")
    default public boolean useMinigameTeleports() {
        return true;
    }

    @ConfigItem(keyName="usePoh", name="Use POH", description="", position=14, section="Pathfinder")
    default public boolean usePoh() {
        return false;
    }

    @ConfigItem(keyName="usePool", name="Use pool before teleporting", description="", position=15, section="Pathfinder")
    default public boolean usePool() {
        return false;
    }

    @Range(min=0, max=100)
    @ConfigItem(keyName="requiredMissingHealth", name="Required missing health", description="Missing health to use the pool below", position=16, section="Pathfinder", hidden=true, unhide="usePool")
    default public int requiredMissingHealth() {
        return 5;
    }

    @Range(min=0, max=100)
    @ConfigItem(keyName="requiredMissingPrayer", name="Required missing prayer", description="Missing prayer to use the pool below", position=17, section="Pathfinder", hidden=true, unhide="usePool")
    default public int requiredMissingPrayer() {
        return 5;
    }

    @Range(min=0, max=100)
    @ConfigItem(keyName="requiredMissingRunEnergy", name="Required missing run energy", description="Missing run energy to use the pool below", position=18, section="Pathfinder", hidden=true, unhide="usePool")
    default public int requiredMissingRunEnergy() {
        return 20;
    }

    @ConfigItem(keyName="hasMountedGlory", name="Mounted Glory", description="", position=19, section="Pathfinder")
    default public boolean hasMountedGlory() {
        return false;
    }

    @ConfigItem(keyName="hasMountedDigsitePendant", name="Mounted Digsite Pendant", description="", position=20, section="Pathfinder")
    default public boolean hasMountedDigsitePendant() {
        return false;
    }

    @ConfigItem(keyName="hasMountedMythicalCape", name="Mounted Mythical Cape", description="", position=21, section="Pathfinder")
    default public boolean hasMountedMythicalCape() {
        return false;
    }

    @ConfigItem(keyName="hasMountedXericsTalisman", name="Mounted Xerics Talisman", description="", position=22, section="Pathfinder")
    default public boolean hasMountedXericsTalisman() {
        return false;
    }

    @ConfigItem(keyName="hasJewelryBox", name="Jewelry Box", description="", position=23, section="Pathfinder")
    default public JewelryBox hasJewelryBox() {
        return JewelryBox.NONE;
    }

    @ConfigItem(keyName="spiritFairyTree", name="POH Spirit tree/Fairy ring", description="", position=24, section="Pathfinder")
    default public SpiritFairyTree spiritFairyTree() {
        return SpiritFairyTree.NONE;
    }

    @ConfigItem(keyName="fairyRings", name="Fairy Rings", description="", position=0, section="Fairy Rings")
    default public Set<FairyRing> fairyRings() {
        return FairyRing.getAllWithNoRequirements();
    }

    @ConfigItem(keyName="spiritTrees", name="Spirit Trees", description="", position=0, section="Spirit Trees")
    default public Set<SpiritTree> spiritTrees() {
        return SpiritTree.getAllWithNoRequirements();
    }

    @ConfigItem(keyName="magicMushtrees", name="Magic Mushtrees", description="", position=0, section="Magic Mushtrees")
    default public Set<MagicMushtree> magicMushtrees() {
        return Set.of();
    }

    @ConfigItem(keyName="housePortals", name="House Portals", description="", position=0, section="House Portals")
    default public Set<HousePortal> housePortals() {
        return Set.of();
    }

    @Units(value="ms")
    @ConfigItem(position=1, keyName="loadoutActionDelay", name="Loadout action delay", description="Delay between actions when withdrawing, depositing or equipping items from a loadout", section="Other settings")
    @Range(min=60, max=600)
    default public int loadoutActionDelay() {
        return 80;
    }

    @ConfigItem(position=1, keyName="loadoutActionDelay", name="Loadout action delay", description="Delay between actions when withdrawing, depositing or equipping items from a loadout", section="Other settings")
    public void loadoutActionDelay(int var1);

    @ConfigItem(keyName="mouseMovement", name="Mouse movement", description="Enables moving the mouse before attempting to interact with objects", position=0, section="mouseMovement")
    default public boolean mouseMovement() {
        return false;
    }

    @ConfigItem(keyName="pathDensity", name="Path Density", description="Points per 100 pixels of movement. Higher = smoother but slower (default: 15)", position=1, section="mouseMovement")
    default public int pathDensity() {
        return 15;
    }

    @ConfigItem(keyName="minPathPoints", name="Min Path Points", description="Minimum number of points in any path (default: 10)", position=2, section="mouseMovement")
    default public int minPathPoints() {
        return 10;
    }

    @ConfigItem(keyName="baseDelay", name="Base Delay (ms)", description="Base delay between each mouse position update. Lower = faster movement (default: 12)", position=3, section="mouseMovement")
    default public int baseDelay() {
        return 12;
    }

    @ConfigItem(keyName="delayVariation", name="Delay Variation (ms)", description="Random variation added to each delay for natural timing (default: 4)", position=4, section="mouseMovement")
    default public int delayVariation() {
        return 4;
    }

    @ConfigItem(keyName="easeMovement", name="Ease Movement", description="Start slow, speed up, then slow down at the end (ease-in-out). Makes movement feel smoother.", position=5, section="mouseMovement")
    default public boolean easeMovement() {
        return true;
    }

    @ConfigItem(keyName="easeStrength", name="Ease Strength", description="How strong the easing effect is. Higher = more pronounced slow-start/slow-end (default: 2.0)", position=6, section="mouseMovement")
    default public double easeStrength() {
        return 2.0;
    }

    @ConfigItem(keyName="fatigueEnabled", name="Fatigue Simulation", description="Gradually slow down movement over longer paths (mimics human fatigue)", position=7, section="mouseMovement")
    default public boolean fatigueEnabled() {
        return true;
    }

    @ConfigItem(keyName="fatigueMultiplier", name="Fatigue Multiplier", description="How much to slow down per 100 pixels of movement (0.1 = 10% slower, default: 0.05)", position=8, section="mouseMovement")
    default public double fatigueMultiplier() {
        return 0.05;
    }

    @ConfigItem(keyName="debugMouse", name="Mouse debug logs", description="Show mouse movement debug messages in chat", position=9, section="mouseMovement")
    default public boolean debugMouse() {
        return false;
    }
}

