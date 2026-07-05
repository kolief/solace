package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;

public class isArceuusFavourKnown extends DecisionNode {

    public isArceuusFavourKnown(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return true;
    }
}
