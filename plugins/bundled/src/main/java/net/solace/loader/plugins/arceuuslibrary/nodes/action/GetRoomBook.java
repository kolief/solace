package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import lombok.Getter;
import lombok.Setter;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.AnimationID;
import net.solace.loader.plugins.arceuuslibrary.domain.Bookcase;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.sdk.commons.Time;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;

import java.util.List;

public class GetRoomBook extends ActionNode {
    @Getter
    @Setter
    private static List<Bookcase> bookshelves = null;

    public GetRoomBook(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        if (bookshelves == null || bookshelves.isEmpty()) {
            bookshelves = null;
            error("Bookshelves not found");
            return -2;
        }

        var bookcase = bookshelves.remove(0);
        var objBookshelf = TileObjects.getFirstAt(bookcase.getWorldPoint(), x -> x.hasAction("Search"));
        if (objBookshelf != null && objBookshelf.getName().equals("Bookshelf")) {
            searchBookshelf(objBookshelf);
            Time.sleepUntil(() -> Players.getLocal().getAnimation() == AnimationID.LOOKING_INTO, 15000);
            Time.sleepUntil(() -> Players.getLocal().getAnimation() == -1, 5000);
            if (bookshelves.isEmpty())
                bookshelves = null;
            return 200;

        } else {
            error("[GetRoomBook] Couldn't find bookshelf Index: %s at WorldPoint: %s", bookcase.getIndex(), bookcase.getWorldPoint());
        }

        return 600;
    }

    @Override
    public String toString() {
        return "Get room books";
    }
}
