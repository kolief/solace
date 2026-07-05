package net.solace.loader.plugins.wintertodt.tasks;

import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.solace.api.domain.Locatable;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.entities.Tiles;
import net.solace.sdk.movement.Movement;

import java.util.Comparator;

public class EscapeSnow extends WintertodtTask {
    public EscapeSnow(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return isInside() && getSnowfall() != null;
    }

    @Override
    public int execute() {
        var local = Players.getLocal();
        if (local.isMoving()) {
            return -1;
        }

        var snowfall = getSnowfall();

        if (snowfall != null && (snowfall.contains(local.getWorldLocation()) || (snowfall.contains(getBrazier().getWorldLocation()) && snowfall.distanceTo(local.getWorldLocation()) <= 2))) {
            Tiles.getSurrounding(local.getWorldLocation(), 5)
                    .stream()
                    .filter(tile -> !snowfall.contains(tile.getWorldLocation()) && snowfall.distanceTo(tile.getWorldLocation()) > 1)
                    .filter(tile -> tile.distanceTo(local) <= 2)
                    .min(Comparator.comparingInt(tile -> tile.distanceTo(local)))
                    .filter(tile -> !local.getWorldLocation().equals(tile.getWorldLocation()))
                    .ifPresent(Movement::walk);
        }

        return -2;
    }

    private TileObject getBrazier() {
        return TileObjects.getFirstAt(WintertodtConstants.SW_BRAZIER_COORD, x -> x.getName() != null && x.getName().toLowerCase().contains("brazier"));
    }

    private WorldArea getSnowfall() {
        return TileObjects.getSurrounding(Players.getLocal().getWorldLocation(), 6, WintertodtConstants.SNOWFALL_ID)
                .stream()
                .min(Comparator.comparingInt(Locatable::getWorldY).thenComparing(Locatable::getWorldX))
                .map(obj -> new WorldArea(obj.getWorldLocation(), 3, 3))
                .filter(area -> !area.toWorldPoint().equals(NPCs.getNearest("Pyromancer").getWorldLocation()))
                .orElse(null);
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    @Override
    public String toString() {
        return "Escaping snow";
    }
}
