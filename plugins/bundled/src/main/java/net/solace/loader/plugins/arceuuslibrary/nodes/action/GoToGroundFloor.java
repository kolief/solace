package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Room;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.loader.plugins.arceuuslibrary.util.MovementHelper;
import net.solace.sdk.movement.Movement;

public class GoToGroundFloor extends ActionNode {
    public GoToGroundFloor(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        var destination = Room.BNW.getWorldPoint();

        if (Movement.isWalking()) {
            return -1;
        }

        MovementHelper.walkToPos(destination);
        return -1;
    }

    @Override
    public String toString() {
        return "Go to ground floor";
    }
}
