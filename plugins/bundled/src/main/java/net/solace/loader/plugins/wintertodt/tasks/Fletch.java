package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.api.domain.tiles.ITileObject;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;

public class Fletch extends WintertodtTask {
    public Fletch(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getConfig().fletch() && isInside()
                && shouldFletch()
                && Inventory.contains("Bruma root");
    }

    @Override
    public int execute() {
        var brazier = getBrazier();
        var local = Players.getLocal();
        if (brazier.distanceTo(local) > 2) {
            Movement.walk(brazier);
            return -2;
        }

        if (isInterrupted()) {
            fletch();
            return -1;
        }

        if (local.getAnimation() == WintertodtConstants.FLETCH_ANIMATION_ID) {
            return -1;
        }

        if (waitUntil > getClient().getTickCount()) {
            setCooldown(2);
            return -1;
        }

        fletch();
        return -1;
    }

    private boolean shouldFletch() {
        return Inventory.isFull()
                || (Inventory.contains("Bruma kindling") && Players.getLocal().getAnimation() != WintertodtConstants.FLETCH_ANIMATION_ID)
                || (Inventory.contains("Bruma root") && getBrazier().distanceTo(Players.getLocal().getWorldLocation()) <= 3);
    }

    private void fletch() {
        setInterrupted(false);
        Inventory.getFirst("Knife").useOn(Inventory.getFirst("Bruma root"));
        setCooldown(2);
    }

    private ITileObject getBrazier() {
        return TileObjects.getFirstAt(WintertodtConstants.SW_BRAZIER_COORD, obj -> obj.getName() != null && obj.getName().toLowerCase().contains("brazier"));
    }

    @Override
    public String toString() {
        return "Fletching";
    }
}
