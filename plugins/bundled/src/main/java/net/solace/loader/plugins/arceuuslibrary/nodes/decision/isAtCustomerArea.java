package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Customer;
import net.solace.loader.plugins.arceuuslibrary.domain.Room;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;

import java.util.Arrays;
import java.util.stream.Collectors;

public class isAtCustomerArea extends DecisionNode {
    public isAtCustomerArea(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        var customers = NPCs.getAll(npc -> Arrays.stream(Customer.values())
                .map(Customer::getName)
                .collect(Collectors.toList())
                .contains(npc.getName()));

        return (customers.size() == Customer.values().length || Room.BC.getArea().contains(Players.getLocal().getWorldLocation())) && Players.getLocal().getPlane() == 0;
    }
}
