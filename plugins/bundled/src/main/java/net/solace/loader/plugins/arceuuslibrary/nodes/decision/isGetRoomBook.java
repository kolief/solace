package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.Book;
import net.solace.loader.plugins.arceuuslibrary.domain.Room;
import net.solace.loader.plugins.arceuuslibrary.nodes.action.GetRoomBook;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.entities.Players;
import net.solace.sdk.items.Inventory;

import java.util.ArrayList;

public class isGetRoomBook extends DecisionNode {
    private final boolean customer;

    public isGetRoomBook(SolaceArceuusLibrary context, boolean customer) {
        super(context);
        this.customer = customer;
    }

    public isGetRoomBook(SolaceArceuusLibrary context) {
        this(context, true);
    }

    @Override
    public boolean decide() {
        var roomBookshelves = GetRoomBook.getBookshelves();
        var targetBook = customer ? context.getLibrary().getCustomerBook() : context.getLibrary().getOtherBook();
        if (roomBookshelves == null) {
            roomBookshelves = new ArrayList<>();
            var room = Room.getRoomByWorldPoint(Players.getLocal().getWorldLocation());
            var bookshelves = context.getLibrary().getBookshelvesByRoom(room);
            for (var b : bookshelves) {
                if (b.getPossibleBooks().isEmpty()) {
                    continue;
                }

                var book = (Book) b.getPossibleBooks().toArray()[0];
                if (book != targetBook
                    && !Inventory.contains(book.getShortName())
                    && book != Book.VARLAMORE_ENVOY) {
                    roomBookshelves.add(b);
                }
            }

            if (!roomBookshelves.isEmpty()) {
                GetRoomBook.setBookshelves(roomBookshelves);
            }
        }

        return GetRoomBook.getBookshelves() != null && !GetRoomBook.getBookshelves().isEmpty();
    }
}
