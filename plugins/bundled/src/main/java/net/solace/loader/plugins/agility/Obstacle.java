package net.solace.loader.plugins.agility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
@Getter
@Setter
public class Obstacle {

    private final WorldArea area;
    private final String name;
    private final String action;
    private final WorldPoint tile;
    private final boolean npc;
    private int id;

    public Obstacle(WorldArea area, String name, String action, boolean npc, WorldPoint tile) {
        this.area = area;
        this.name = name;
        this.action = action;
        this.npc = npc;
        this.tile = tile;
    }

    public Obstacle(WorldArea area, String name, String action, boolean npc, WorldPoint tile, int id) {
        this.area = area;
        this.name = name;
        this.action = action;
        this.npc = npc;
        this.tile = tile;
        this.id = id;
    }

    public Obstacle(WorldArea area, String name, String action, int id) {
        this(area, name, action, false, null, id);
    }

    public Obstacle(WorldArea location, String name, String action, WorldPoint tile) {
        this(location, name, action, false, tile);
    }

    public Obstacle(WorldArea location, String name, String action) {
        this(location, name, action, false, null);
    }
}
