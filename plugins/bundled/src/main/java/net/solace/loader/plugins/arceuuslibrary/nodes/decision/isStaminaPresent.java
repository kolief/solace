package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.items.Inventory;

public class isStaminaPresent extends DecisionNode {

    public isStaminaPresent(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        if (!context.getConfig().replenishStaminaPotions()) {
            return true;
        }

        return Inventory.contains(x -> x.getName().contains("Stamina potion"));
    }
}
