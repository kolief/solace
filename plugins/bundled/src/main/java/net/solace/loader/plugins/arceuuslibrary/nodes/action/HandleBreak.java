package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;

public class HandleBreak extends ActionNode {

    public HandleBreak(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        if (context.getBreakHandler().isBreakActive(context)) {
            return 600;
        }

        if (context.getBreakHandler().shouldBreak(context)) {
            context.getBreakHandler().startBreak(context);
        }
        return 600;
    }

    @Override
    public String toString() {
        return "Handle Break";
    }
}