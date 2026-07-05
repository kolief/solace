package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.entities.NPCs;

public class isAtCustomer extends DecisionNode {
    public isAtCustomer(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        var customer = context.getLibrary().getCustomer();
        return customer != null && NPCs.getNearest(customer.getName()) != null;
    }
}
