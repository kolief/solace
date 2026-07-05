package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.SolvedState;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;

public class isLibrarySolved extends DecisionNode {

    public isLibrarySolved(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return context.getLibrary().getState() == SolvedState.COMPLETE;
    }
}
