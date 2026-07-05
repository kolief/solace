package net.solace.api.domain.tiles;

import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldArea;
import net.solace.api.domain.tiles.ITileObject;

public interface IGameObject
extends GameObject,
ITileObject {
    public WorldArea getWorldArea();
}

