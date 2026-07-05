package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Room;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.sdk.movement.Movement;

public class WalkToLibrary extends ActionNode {
    public WalkToLibrary(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        if (!Movement.isWalking()) {
            Movement.walkTo(Room.BC.getWorldPoint());
        }

        return 600;
    }

    @Override
    public String toString() {
        return "Walking to library";
    }
}