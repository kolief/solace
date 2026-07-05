package net.solace.impl.domain.game;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IWorldMap;

@RequiredArgsConstructor
public class WorldMapImpl implements IWorldMap {
    private final IClient client;

    @Override
    public WorldPoint getMouseLocation() {
        var mouseLocation = client.getWrapped().getMouseCanvasPosition();
        return mapGraphicsPointToWorldPoint(mouseLocation);
    }

    public WorldPoint mapGraphicsPointToWorldPoint(Point graphicsPoint) {
        var worldMap = client.getWorldMap();
        var map = client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER);

        if (map == null) {
            return null;
        }

        var worldMapRect = map.getBounds();
        var pixelsPerTile = worldMap.getWorldMapZoom();

        var worldMapCenter = worldMap.getWorldMapPosition();

        var xOffset = (graphicsPoint.getX() - worldMapRect.getCenterX()) / pixelsPerTile;
        var yOffset = (worldMapRect.getCenterY() - graphicsPoint.getY()) / pixelsPerTile;

        var x = worldMapCenter.getX() + (int) Math.round(xOffset);
        var y = worldMapCenter.getY() + (int) Math.round(yOffset);

        var worldPoint = new WorldPoint(x, y, 0);

        if (!worldMap.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY())) {
            return null;
        }

        return worldPoint;
    }
}
