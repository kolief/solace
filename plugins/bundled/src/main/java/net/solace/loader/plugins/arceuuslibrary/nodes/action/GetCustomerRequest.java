package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.runelite.api.NPC;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Customer;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.loader.plugins.arceuuslibrary.util.MovementHelper;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.movement.Movement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.StrictMath.floorMod;

public class GetCustomerRequest extends ActionNode {
    private final List<Customer> customers = Arrays.asList(Customer.values());

    private Customer nextCustomer;
    private boolean isMovingCloser;

    public GetCustomerRequest(SolaceArceuusLibrary context) {
        super(context);
        Collections.shuffle(customers);
    }

    @Override
    public int process() {
        var local = Players.getLocal();
        if (local.isAnimating() || local.isMoving()) {
            return -2;
        }

        var library = context.getLibrary();
        var customer = library.getLastCustomer();

        if (customer == null) {
            NPC closest = NPCs.query()
                    .names(customers.stream().filter(Customer::isRegular).map(Customer::getName).toArray(String[]::new))
                    .results()
                    .nearest(local);
            if (closest != null) {
                customer = Customer.getByName(NPCs.getNearest(npc -> Customer.getByName(npc.getName()) != null).getName());
            } else {
                customer = customers.get(0);
            }
        } else {
            if (!isMovingCloser) {
                customer = customers.get(floorMod((customers.indexOf(customer) + 1), customers.size()));
            } else {
                customer = nextCustomer;
            }
        }

        var npcCustomer = NPCs.getNearest(customer.getName());
        if (npcCustomer != null) {
            isMovingCloser = false;
            library.setLastCustomer(customer);
            npcCustomer.interact("Help");
            return 2000;
        } else {
            if (customer.getWorldPoint().distanceTo(local.getWorldLocation()) > 10) {
                if (Movement.isWalking()) {
                    return -1;
                }

                MovementHelper.walkToPos(customer.getWorldPoint());
                nextCustomer = customer;
                isMovingCloser = true;
            } else {
                error("Couldn't find customer name: %s%n", customer.getName());
            }
        }

        // Reset
        GetRoomBook.setBookshelves(null);

        return 600;
    }

    @Override
    public String toString() {
        return "Get book request";
    }
}
