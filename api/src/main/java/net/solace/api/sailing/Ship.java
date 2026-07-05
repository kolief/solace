package net.solace.api.sailing;

import net.runelite.api.WorldEntity;
import net.runelite.client.eventbus.EventBus;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileItems;
import net.solace.api.entities.ITileObjects;
import net.solace.api.entities.ITiles;

public interface Ship {
    public ITiles getTileManager();

    public INPCs getNpcManager();

    public IPlayers getPlayerManager();

    public ITileObjects getTileObjectManager();

    public ITileItems getTileItemManager();

    public WorldEntity getWorldEntity();

    public void destroy(EventBus var1);
}

