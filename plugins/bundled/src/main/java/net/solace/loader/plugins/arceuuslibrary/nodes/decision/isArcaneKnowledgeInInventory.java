package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.api.commons.Rand;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.LibraryItem;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.items.Inventory;

public class isArcaneKnowledgeInInventory extends DecisionNode {
    private int rand = Rand.nextInt(1, 13);

    public isArcaneKnowledgeInInventory(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        var decide = shouldOpen();
        if (decide) {
            rand = Rand.nextInt(1, 10);
        }

        return decide;
    }

    private boolean shouldOpen() {
        return (Inventory.isFull() && Inventory.contains(LibraryItem.BOOK_OF_ARCANE_KNOWLEDGE))
               || Inventory.getCount(LibraryItem.BOOK_OF_ARCANE_KNOWLEDGE) >= rand;
    }
}
