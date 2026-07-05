package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.entities.Players;

public class isAtTopFloor extends DecisionNode {
    public isAtTopFloor(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return Players.getLocal().getWorldLocation().getPlane() == 2;
    }
}
