package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Bookcase;
import net.solace.loader.plugins.arceuuslibrary.domain.Library;
import net.solace.loader.plugins.arceuuslibrary.domain.Room;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.entities.Players;

public class isAtBook extends DecisionNode {
    private final boolean customer;

    public isAtBook(SolaceArceuusLibrary context, boolean customer) {
        super(context);
        this.customer = customer;
    }

    public isAtBook(SolaceArceuusLibrary context) {
        this(context, true);
    }

    @Override
    public boolean decide() {
        Library library = context.getLibrary();
        var book = customer ? library.getCustomerBook() : library.getOtherBook();
        Bookcase bookcase = library.getBookshelfByBook(book);
        if (bookcase == null) {
            error("Couldn't find bookshelf for %s", book.getShortName());
            return false;
        }

        return Room.getRoomByWorldPoint(bookcase.getWorldPoint()) == Room.getRoomByWorldPoint(Players.getLocal().getWorldLocation());
    }
}
