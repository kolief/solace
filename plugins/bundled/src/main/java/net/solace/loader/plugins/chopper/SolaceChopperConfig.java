package net.solace.loader.plugins.chopper;

import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.Range;

@ConfigGroup("solacechopper")
public interface SolaceChopperConfig extends Config {
    @ConfigItem(
            keyName = "tree",
            name = "Tree type",
            description = "The type of tree to chop",
            position = 0
    )
    default Tree tree() {
        return Tree.REGULAR;
    }

    @ConfigItem(
            keyName = "inventoryMode",
            name = "Inventory mode",
            description = "Make fire, drop or fletch",
            position = 1
    )
    default InventoryMode inventoryMode() {
        return InventoryMode.FIRE;
    }

    @ConfigItem(
            keyName = "fletchMode",
            name = "Fletching mode",
            description = "The type of item to fletch",
            position = 2,
            hidden = true,
            unhide = "inventoryMode",
            unhideValue = "FLETCH"
    )
    default FletchMode fletchMode() {
        return FletchMode.ARROW_SHAFT;
    }

    @ConfigItem(
            keyName = "bankLocation",
            name = "Bank Location",
            description = "Location to bank at",
            position = 3,
            hidden = true,
            unhide = "inventoryMode",
            unhideValue = "BANK"
    )
    default BankLocation bankLocation() {
        return BankLocation.DRAYNOR_BANK;
    }

    @ConfigItem(
            keyName = "ignoreNests",
            name = "Ignore Bird nests",
            description = "Prevents Bird nests from being collected",
            position = 4
    )
    default boolean ignoreNests() {
        return false;
    }

    @Range(min = 1, max = 20)
    @ConfigItem(
            keyName = "radius",
            name = "Radius",
            description = "The radius to chop trees and make fires in",
            position = 5
    )
    default int radius() {
        return 10;
    }

    @ConfigItem(
            keyName = "stopAtLevel",
            name = "Stop at level",
            description = "Stop at this level",
            position = 6
    )
    default int stopAtLevel() {
        return 99;
    }
}