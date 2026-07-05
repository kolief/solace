package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;

public class shouldHandleStop extends DecisionNode {

    public shouldHandleStop(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return context.isShouldStop();
    }
}
