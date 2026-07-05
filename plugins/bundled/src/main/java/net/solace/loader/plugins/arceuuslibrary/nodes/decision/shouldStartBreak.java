package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;

public class shouldStartBreak extends DecisionNode {

    public shouldStartBreak(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return context.getBreakHandler().shouldBreak(context) && !context.getBreakHandler().isBreakActive(context)
                || context.getBreakHandler().isBreakActive(context);
    }
}
