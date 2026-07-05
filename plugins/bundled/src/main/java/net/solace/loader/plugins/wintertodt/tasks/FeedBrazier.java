package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.api.domain.tiles.ITileObject;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.items.Inventory;

public class FeedBrazier extends WintertodtTask {
    public FeedBrazier(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return isInside()
                && (Inventory.contains("Bruma kindling") || Inventory.contains("Bruma root"))
                && getLitBrazier() != null
                && isPyromancerAlive()
                && (Inventory.isFull() || getLitBrazier().distanceTo(Players.getLocal()) <= 4);
    }

    @Override
    public int execute() {
        if (isInterrupted()) {
            feedBrazier();
            return -1;
        }

        if (Players.getLocal().getAnimation() == WintertodtConstants.FEED_ANIMATION_ID) {
            setCooldown(5);
            return -1;
        }

        if (waitUntil > getClient().getTickCount()) {
            return -1;
        }

        feedBrazier();
        return -1;
    }

    private void feedBrazier() {
        setInterrupted(false);
        getLitBrazier().interact("Feed");
        setCooldown(5);
    }

    private ITileObject getLitBrazier() {
        return TileObjects.getFirstSurrounding(WintertodtConstants.SW_BRAZIER_COORD, 5, obj -> obj.hasAction("Feed"));
    }

    @Override
    public String toString() {
        return "Feeding brazier";
    }
}
