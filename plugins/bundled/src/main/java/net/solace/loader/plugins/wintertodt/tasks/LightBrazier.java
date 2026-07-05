package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.api.domain.tiles.ITileObject;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;

public class LightBrazier extends WintertodtTask {
    public LightBrazier(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return isInside()
                && isPyromancerAlive()
                && getUnlitBrazier() != null
                && getUnlitBrazier().distanceTo(Players.getLocal()) <= 3;
    }

    @Override
    public int execute() {
        if (Players.getLocal().getAnimation() == WintertodtConstants.LIGHT_ANIMATION_ID) {
            return -1;
        }

        if (waitUntil > getClient().getTickCount()) {
            setCooldown(4);
            return -1;
        }

        getUnlitBrazier().interact("Light");
        setInterrupted(false);
        setCooldown(4);
        return -3;
    }

    private ITileObject getUnlitBrazier() {
        return TileObjects.getFirstSurrounding(WintertodtConstants.SW_BRAZIER_COORD, 5, obj -> obj.hasAction("Light"));
    }

    @Override
    public String toString() {
        return "Lighting brazier";
    }
}
