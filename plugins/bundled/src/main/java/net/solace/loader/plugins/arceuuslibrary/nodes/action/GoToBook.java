package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.loader.plugins.arceuuslibrary.util.MovementHelper;
import net.solace.sdk.movement.Movement;

@Slf4j
public class GoToBook extends ActionNode {
    private final boolean customer;

    public GoToBook(SolaceArceuusLibrary context, boolean customer) {
        super(context);
        this.customer = customer;
    }

    public GoToBook(SolaceArceuusLibrary context) {
        this(context, true);
    }

    @Override
    public int process() {
        var book = customer ? context.getLibrary().getCustomerBook() : context.getLibrary().getOtherBook();
        var bookcase = context.getLibrary().getBookshelfByBook(book);
        log.debug("Moving to bookcase: {}", bookcase);
        if (bookcase == null) {
            error("Cannot find bookshelf");
            context.getLibrary().reset();
            return 600;
        }

        if (Movement.isWalking()) {
            return -1;
        }

        MovementHelper.walkToPos(bookcase);
        return -1;
    }

    @Override
    public String toString() {
        return "Moving to book";
    }
}
