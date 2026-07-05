package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.runelite.api.coords.WorldArea;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;

public class WalkToBank extends ActionNode {
    private static final WorldArea BANK_AREA = new WorldArea(1621, 3736, 18, 18, 0);

    public WalkToBank(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        var banker = NPCs.getNearest("Banker");
        if (banker != null && BANK_AREA.contains(Players.getLocal().getWorldLocation())) {
            banker.interact("Bank");
            return -4;
        }

        if (!Movement.isWalking()) {
            Movement.walkTo(BANK_AREA);
        }

        return 600;
    }

    @Override
    public String toString() {
        return "Moving to bank";
    }
}