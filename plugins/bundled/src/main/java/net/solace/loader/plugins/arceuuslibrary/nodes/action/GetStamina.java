package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.runelite.api.gameval.ItemID;
import net.solace.api.domain.items.IItem;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Book;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Inventory;

import java.util.function.Predicate;

public class GetStamina extends ActionNode {
    private static final Predicate<IItem> FILTER = item ->
    {
        var name = item.getName();
        var id = item.getId();
        return id != ItemID.ARCEUUS_LIBRARY_REWARD
               && Book.byId(id) == null
               && !name.startsWith("Stamina potion");
    };

    public GetStamina(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        var filteredItems = Inventory.getAll(FILTER);

        if (!filteredItems.isEmpty()) {
            filteredItems.forEach(item -> Bank.depositAll(item.getId()));
            return 600;
        }

        var stamina = Bank.getFirst(x -> !x.isPlaceholder() && x.getName().contains("Stamina potion"));

        if (stamina != null) {
            Bank.withdraw(stamina.getId(), context.getConfig().staminaPotionQuantity());
            return 600;
        }

        if (context.getConfig().logoutNoStamina()) {
            context.setShouldStop(true);
            return 600;
        }

        error("No stamina potions found, disabling setting");
        context.getConfig().replenishStaminaPotions(false);
        return 1200;
    }

    @Override
    public String toString() {
        return "Withdrawing stamina";
    }
}