package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Book;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.sdk.items.Inventory;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class DropBook extends ActionNode {
    public DropBook(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        var bookItem = Inventory.getFirst(item -> Arrays.stream(Book.values())
                .filter(book -> book != context.getLibrary().getCustomerBook())
                .map(Book::getShortName)
                .collect(Collectors.toList())
                .contains(item.getName()));
        if (bookItem != null) {
            bookItem.interact("Drop");
        } else {
            error("Couldn't find book to drop");
        }

        return 1000;
    }

    @Override
    public String toString() {
        return "Dropping book";
    }
}
