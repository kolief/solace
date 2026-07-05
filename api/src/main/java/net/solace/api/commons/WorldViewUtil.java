package net.solace.api.commons;

import net.runelite.api.Projection;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.game.IClient;

public class WorldViewUtil {
    public static WorldPoint getTopWorldLocation() {
        return WorldViewUtil.getTopWorldLocation(Static.getPlayers().getLocal().getWorldLocation());
    }

    public static WorldPoint getTopWorldLocation(WorldPoint worldPoint) {
        if (worldPoint == null) {
            return null;
        }
        if (!Static.getSailing().isOnBoat()) {
            return worldPoint;
        }
        WorldView wv = Static.getWrappedClient().findWorldViewFromWorldPoint(worldPoint);
        if (wv.getId() == -1) {
            return worldPoint;
        }
        WorldPoint adjustedWorldPoint = new WorldPoint(worldPoint.getX(), worldPoint.getY(), wv.getPlane());
        LocalPoint lp = LocalPoint.fromWorld((WorldView)wv, (WorldPoint)adjustedWorldPoint);
        if (lp == null) {
            return worldPoint;
        }
        Projection mainWorldProjection = wv.getMainWorldProjection();
        if (mainWorldProjection == null) {
            return worldPoint;
        }
        float[] projectedToMainWorld = mainWorldProjection.project((float)lp.getX(), 0.0f, (float)lp.getY());
        IClient client = Static.getClient();
        WorldView topLevelWorldView = client.getTopLevelWorldView();
        float xWithDecimals = projectedToMainWorld[0] / 128.0f + (float)topLevelWorldView.getBaseX();
        float yWithDecimals = projectedToMainWorld[2] / 128.0f + (float)topLevelWorldView.getBaseY();
        return new WorldPoint(Math.round(xWithDecimals), Math.round(yWithDecimals), topLevelWorldView.getPlane());
    }
}

