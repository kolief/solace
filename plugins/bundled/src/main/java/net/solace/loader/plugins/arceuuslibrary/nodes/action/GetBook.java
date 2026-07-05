package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.AnimationID;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.sdk.commons.Time;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;

@Slf4j
public class GetBook extends ActionNode {
    private final boolean customer;

    public GetBook(SolaceArceuusLibrary context, boolean customer) {
        super(context);
        this.customer = customer;
    }

    public GetBook(SolaceArceuusLibrary context) {
        this(context, true);
    }

    @Override
    public int process() {
        var library = context.getLibrary();
        var book = customer ? library.getCustomerBook() : library.getOtherBook();
        log.debug("Getting book: {}", book);
        var bookcase = library.getBookshelfByBook(book);
        var objBookshelf = TileObjects.getFirstAt(bookcase.getWorldPoint(), x -> x.hasAction("Search"));
        if (objBookshelf != null && objBookshelf.getName().equals("Bookshelf")) {
            searchBookshelf(objBookshelf);
            Time.sleepUntil(() -> Players.getLocal().getAnimation() == AnimationID.LOOKING_INTO, 10000);
            Time.sleepUntil(() -> Players.getLocal().getAnimation() == -1, 5000);
            return 200;
        } else {
            error("Couldn't find bookshelf Index: %s at WorldPoint: %s", bookcase.getIndex(), bookcase.getWorldPoint());
        }

        return 600;
    }

    @Override
    public String toString() {
        var target = customer ? "customer" : "other";
        return "Get " + target + " book";
    }
}
