package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.loader.plugins.arceuuslibrary.util.MovementHelper;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.movement.Reachable;

import java.util.Objects;

public class HandInBook extends ActionNode {
    public HandInBook(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        var library = context.getLibrary();
        var customer = library.getCustomer();
        if (customer == null) {
            return 600;
        }

        var npcCustomer = NPCs.getNearest(customer.getName());
        if (npcCustomer != null) {
            var interacting = Players.getLocal().getInteracting();
            if (Reachable.isInteractable(npcCustomer) && (interacting == null || !Objects.equals(interacting, npcCustomer))) {
                npcCustomer.interact("Help");
                return 600;
            }

            if (Movement.isWalking()) {
                return 600;
            }

            MovementHelper.walkToPos(npcCustomer.getWorldLocation());
        } else {
            error("Couldn't find customer Name: %s", customer.getName());
        }

        return 1200;
    }

    @Override
    public String toString() {
        return "Hand in book";
    }
}
