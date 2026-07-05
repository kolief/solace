package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.items.Inventory;

public class isSpaceAvailable extends DecisionNode {

    public isSpaceAvailable(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        int space = Inventory.getFreeSlots() + Inventory.getCount("Vial");

        return space >= context.getConfig().staminaPotionQuantity();
    }
}
