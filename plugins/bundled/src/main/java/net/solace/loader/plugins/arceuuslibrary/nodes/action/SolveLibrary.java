package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.AnimationID;
import net.solace.loader.plugins.arceuuslibrary.domain.Bookcase;
import net.solace.loader.plugins.arceuuslibrary.domain.LibraryEvent;
import net.solace.loader.plugins.arceuuslibrary.domain.LibraryEventListener;
import net.solace.loader.plugins.arceuuslibrary.domain.SolvedState;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.loader.plugins.arceuuslibrary.util.MovementHelper;
import net.solace.sdk.commons.Time;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Worlds;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.widgets.Dialog;

import static java.lang.Math.floorMod;

@Slf4j
public class SolveLibrary extends ActionNode implements LibraryEventListener {
    private static int lastSearchedIndex = -1;

    public SolveLibrary(SolaceArceuusLibrary context) {
        super(context);
        context.getLibrary().register(this);
    }

    public void resetSearchIndex() {
        lastSearchedIndex = -1;
    }

    @Override
    public void notify(LibraryEvent event) {
        if (event.getAction().equalsIgnoreCase("reset")) {
            resetSearchIndex();
        }
    }

    @Override
    public int process() {
        if (Players.getLocal().isAnimating() || Movement.isWalking()) {
            return -1;
        }

        if (context.getLibrary().isShouldHop()) {
            info("Hopping to ensure clean library state");
            if (Dialog.isOpen()) {
                if (Dialog.canContinue()) {
                    Dialog.continueSpace();
                    return 600;
                }

                if (!Dialog.getOptions().isEmpty() && Dialog.hasOption(x -> x.contains("Magic"))) {
                    Movement.walk(Players.getLocal().getWorldLocation());
                    return 1200;
                }
            }

            var random = Worlds.getRandom(world -> Worlds.isNormal(world) && Worlds.isMembers(world));
            Worlds.hopTo(random);
            return 2000;
        }

        var playerPos = Players.getLocal().getWorldLocation();
        var bookshelves = context.getLibrary().getBookshelvesOnLevel(Client.getPlane());
        // Find closed bookshelf to search if no previous bookshelf searched
        Bookcase nearest;
        lastSearchedIndex = bookshelves.indexOf(bookshelves.get(2));

        if (lastSearchedIndex < 0) {
            nearest = bookshelves.get(0);
            double min = playerPos.distanceTo(nearest.getWorldPoint());
            for (var bookcase : bookshelves) {
                if (hasBookShelfBeenSearched(bookcase)) {
                    continue;
                }

                double newMin = playerPos.distanceTo(bookcase.getWorldPoint());
                if (newMin < min) {
                    nearest = bookcase;
                    min = newMin;
                }
            }
        } else {
            nearest = bookshelves.get(lastSearchedIndex);
        }

        log.debug("Searching bookshelf Index: {} at WorldPoint: {}", nearest.getIndex(), nearest.getWorldPoint());
        // Find next closest searchable bookshelf by index
        var newSearch = lastSearchedIndex;
        while (hasBookShelfBeenSearched(nearest)) {
            newSearch = floorMod((newSearch + 1), bookshelves.size());
            nearest = bookshelves.get(newSearch);

            if (newSearch == lastSearchedIndex) {
                log.info("Ground floor checked");

                if (playerPos.getPlane() == 2 && context.getLibrary().isGroundFloorChecked()) {
                    log.warn("Already on the top floor with ground floor checked");
                    context.getLibrary().reset();
                    return 600;
                }

                context.getLibrary().setGroundFloorChecked();
                return 600;
            }
        }

        log.info("Going to bookshelf Index: {} at WorldPoint: {}", nearest.getIndex(), nearest.getWorldPoint());
        var bookshelf = TileObjects.getFirstAt(nearest.getWorldPoint(), x -> x.hasAction("Search"));
        if (bookshelf != null) {
            if (bookshelf.isInteractable()) {
                searchBookshelf(bookshelf);
                lastSearchedIndex = bookshelves.indexOf(nearest);
                Time.sleepUntil(() -> Players.getLocal().getAnimation() == AnimationID.LOOKING_INTO, 10000);
                Time.sleepUntil(() -> Players.getLocal().getAnimation() == -1, 5000);
                MovementHelper.useStaminaPot();
                MovementHelper.checkToggleRun();
                return 300;
            } else {
                if (Movement.isWalking()) {
                    return -1;
                }

                MovementHelper.walkToPos(nearest);
                return -1;
            }
        } else {
            error("Couldn't find bookshelf Index: %s at WorldPoint: %s", nearest.getIndex(), nearest.getWorldPoint());
        }

        return 600;
    }

    public boolean hasBookShelfBeenSearched(Bookcase bookcase) {
        var bookIsKnown = bookcase.isBookSet();
        var book = bookcase.getBook();
        var possible = bookcase.getPossibleBooks();

        if (!bookIsKnown && possible.size() == 1) {
            book = possible.iterator().next();
            bookIsKnown = true;
        }

        return bookIsKnown && book == null || (context.getLibrary().getState() != SolvedState.NO_DATA && possible.isEmpty()) || book != null;
    }

    @Override
    public String toString() {
        return "Solving library";
    }
}
