package net.solace.loader.plugins.cannonballer;

import lombok.Value;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.SceneEntity;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;

@Value
public class SelectedEntity {
    int id;
    WorldPoint worldPoint;
    Class<? extends SceneEntity> type;

    public SceneEntity get() {
        if (TileObject.class.isAssignableFrom(type)) {
            return TileObjects.getFirstAt(worldPoint, id);
        }

        if (NPC.class.isAssignableFrom(type)) {
            return NPCs.getNearest(worldPoint, id);
        }

        return null;
    }

    public int distance() {
        return Players.getLocal().distanceTo(worldPoint);
    }
}
