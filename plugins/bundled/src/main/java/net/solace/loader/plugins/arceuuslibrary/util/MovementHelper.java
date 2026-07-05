package net.solace.loader.plugins.arceuuslibrary.util;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.commons.Calculations;
import net.solace.api.commons.Rand;
import net.solace.loader.plugins.arceuuslibrary.domain.Bookcase;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.entities.Tiles;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.utils.MessageUtils;

import java.util.Comparator;

@Slf4j
public class MovementHelper {
    public static int lastFloor = Players.getLocal().getPlane();

    public static boolean shouldEnableRun() {
        if (Movement.isRunEnabled()) {
            return false;
        }

        if ((Rand.nextInt(1, 5000) == 1) || Movement.getRunEnergy() > 90) {
            return true;
        }

        return Movement.getRunEnergy() > Rand.nextInt(40, 60);
    }

    public static void walkToPos(WorldPoint pos) {
        checkToggleRun();
        useStaminaPot();
        Movement.walkTo(pos);
    }

    public static void walkToPos(Bookcase bookcase) {
        WorldPoint targetTile;
        var nearestObject = TileObjects.getFirstAt(bookcase.getWorldPoint(), "Bookshelf");

        if (nearestObject == null) {
            var tile = Movement.getNearestWalkableTile(bookcase.getWorldPoint(), x -> x.distanceTo(bookcase.getWorldPoint()) <= 1 && x != bookcase.getWorldPoint());
            if (tile != null) {
                targetTile = tile;
            } else {
                targetTile = bookcase.getWorldPoint();
            }
        } else {
            var closestTile = Tiles.getAll(x -> !x.isObstructed() && x.distanceTo(nearestObject.getWorldLocation()) <= 1 && Calculations.isInteractable(Static.getWrappedClient(), x.getWorldLocation(), nearestObject))
                    .stream().min(Comparator.comparingDouble(x -> x.distanceTo(nearestObject.getWorldLocation()))).orElse(null);

            targetTile = closestTile != null ? closestTile.getWorldLocation() : bookcase.getWorldPoint();
        }

        if (targetTile != null) {
            log.info("Walking to bookcase: " + targetTile);
            walkToPos(targetTile);
        } else {
            MessageUtils.addMessage("Error finding tile to walk to for bookcase: " + bookcase.getWorldPoint());
        }
    }

    public static boolean useStaminaPot() {
        if (Movement.isStaminaBoosted()) {
            return false;
        }

        if (Movement.getRunEnergy() < Rand.nextInt(2, 35)) {
            var item = Inventory.getFirst(x -> x.getName().contains("Stamina potion"));
            if (item != null) {
                item.interact("Drink");
                return true;
            }
        }

        return false;
    }

    public static boolean checkToggleRun() {
        if (MovementHelper.shouldEnableRun()) {
            Movement.toggleRun();
            return true;
        }

        return false;
    }
}
