package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;

public class isGroundFloorChecked extends DecisionNode {

    public isGroundFloorChecked(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return context.getLibrary().isGroundFloorChecked();
    }
}
