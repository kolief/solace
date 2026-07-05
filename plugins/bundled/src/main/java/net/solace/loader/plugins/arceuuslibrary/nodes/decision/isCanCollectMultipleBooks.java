package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Book;
import net.solace.loader.plugins.arceuuslibrary.domain.Bookcase;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.entities.Players;
import net.solace.sdk.items.Inventory;

import java.util.Comparator;
import java.util.Objects;

@Slf4j
public class isCanCollectMultipleBooks extends DecisionNode {
    static boolean handingInBooks;

    public isCanCollectMultipleBooks(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        if (handingInBooks) {
            return false;
        }

        if (!context.getConfig().collectMultipleBooks()) {
            return false;
        }

        if (!haveSpaceInInventory()) {
            handingInBooks = true;
            return false;
        }

        var booksNotInInventory = Book.getBooksNotInInventory();
        var decide = !booksNotInInventory.isEmpty();
        if (decide && context.getLibrary().getOtherBook() == null) {
            booksNotInInventory.stream()
                    .map(context.getLibrary().getByBook()::get)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingDouble(this::getPlane))
                    .min(Comparator.comparingDouble(this::getLinearDistance))
                    .map(Bookcase::getBook)
                    .ifPresent(book ->
                    {
                        log.debug("Setting other book: {}", book);
                        context.getLibrary().setOtherBook(book);
                    });
        }

        return decide;
    }

    private boolean haveSpaceInInventory() {
        return !Inventory.isFull() && Book.getBooksInInventory().size() < context.getConfig().collectMultipleBooksAmount();
    }

    private double getLinearDistance(Bookcase bookcase) {
        var x = bookcase.getWorldPoint().getX() - Players.getLocal().getWorldLocation().getX();
        var y = bookcase.getWorldPoint().getY() - Players.getLocal().getWorldLocation().getY();
        return Math.sqrt(x * x + y * y);
    }

    private double getPlane(Bookcase bookcase) {
        return bookcase.getWorldPoint().getPlane();
    }
}
