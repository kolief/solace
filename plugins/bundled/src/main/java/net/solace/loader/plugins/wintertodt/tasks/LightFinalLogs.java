package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.api.domain.tiles.ITileObject;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.items.Inventory;

public class LightFinalLogs extends WintertodtTask {
    public LightFinalLogs(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return isInside() && getEnergy() >= 0 && getEnergy() <= 15
                && (Inventory.contains("Bruma kindling") || Inventory.getCount("Bruma root") >= Math.floor(getEnergy() * 0.6))
                && isPyromancerAlive()
                && getLitBrazier() != null;
    }

    @Override
    public int execute() {
        if (isInterrupted()) {
            feedBrazier();
            return -1;
        }

        if (Players.getLocal().getAnimation() == WintertodtConstants.FEED_ANIMATION_ID) {
            return -1;
        }

        if (waitUntil > getClient().getTickCount()) {
            setCooldown(4);
            return -1;
        }

        feedBrazier();
        return -1;
    }

    private void feedBrazier() {
        getLitBrazier().interact("Feed");
        setInterrupted(false);
        setCooldown(4);
    }

    private ITileObject getLitBrazier() {
        return TileObjects.getFirstAt(WintertodtConstants.SW_BRAZIER_COORD, obj -> obj.hasAction("Feed"));
    }

    @Override
    public String toString() {
        return "Lighting final logs";
    }
}
