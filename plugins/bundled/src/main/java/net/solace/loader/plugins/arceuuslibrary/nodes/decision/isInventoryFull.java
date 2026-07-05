package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.items.Inventory;

public class isInventoryFull extends DecisionNode {

    public isInventoryFull(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return Inventory.isFull();
    }
}
