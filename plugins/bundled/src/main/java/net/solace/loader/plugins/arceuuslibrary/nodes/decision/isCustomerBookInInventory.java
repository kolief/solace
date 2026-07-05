package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Book;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.items.Inventory;

public class isCustomerBookInInventory extends DecisionNode {
    public isCustomerBookInInventory(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        Book book = context.getLibrary().getCustomerBook();
        var decide = book != null && Inventory.getFirst(item -> item.getName().equals(book.getShortName())) != null;
        if (!decide) {
            isCanCollectMultipleBooks.handingInBooks = false;
        }

        return decide;
    }
}
