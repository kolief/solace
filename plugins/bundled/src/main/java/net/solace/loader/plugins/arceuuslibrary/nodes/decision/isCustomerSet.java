package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;

public class isCustomerSet extends DecisionNode {
    public isCustomerSet(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return context.getLibrary().getCustomer() != null;
    }
}
