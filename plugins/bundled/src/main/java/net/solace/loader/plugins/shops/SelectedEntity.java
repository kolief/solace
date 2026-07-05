package net.solace.loader.plugins.shops;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.SceneEntity;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;

public class SelectedEntity {
    private final int id;
    @Getter
    private final WorldPoint worldPoint;
    @Getter
    @Setter
    private WorldPoint returnTile;
    Class<? extends SceneEntity> type;

    public SelectedEntity(int id, WorldPoint worldPoint, Class<? extends SceneEntity> type) {
        this.id = id;
        this.worldPoint = worldPoint;
        this.type = type;
    }

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
