package net.solace.impl.sailing;

import lombok.Getter;
import net.runelite.api.WorldEntity;
import net.runelite.client.eventbus.EventBus;
import net.solace.api.containers.NpcContainer;
import net.solace.api.containers.PlayerContainer;
import net.solace.api.containers.TileContainer;
import net.solace.api.domain.game.IClient;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileItems;
import net.solace.api.entities.ITileObjects;
import net.solace.api.entities.ITiles;
import net.solace.api.sailing.Ship;
import net.solace.impl.entities.NPCsImpl;
import net.solace.impl.entities.PlayersImpl;
import net.solace.impl.entities.TileItemsImpl;
import net.solace.impl.entities.TileObjectsImpl;
import net.solace.impl.entities.TilesImpl;

@Getter
public class ShipImpl implements Ship {
    private final TileContainer tileContainer;
    private final NpcContainer npcContainer;
    private final PlayerContainer playerContainer;

    private final ITiles tileManager;
    private final INPCs npcManager;
    private final IPlayers playerManager;
    private final ITileObjects tileObjectManager;
    private final ITileItems tileItemManager;
    private final WorldEntity worldEntity;

    public ShipImpl(EventBus eventBus, IClient client, TileContainer tileContainer, NpcContainer npcContainer,
                    PlayerContainer playerContainer, WorldEntity worldEntity) {
        this.tileContainer = tileContainer;
        this.npcContainer = npcContainer;
        this.playerContainer = playerContainer;
        this.worldEntity = worldEntity;

        eventBus.register(tileContainer);
        eventBus.register(npcContainer);
        eventBus.register(playerContainer);

        tileManager = new TilesImpl(client, tileContainer);
        npcManager = new NPCsImpl(npcContainer, playerContainer);
        playerManager = new PlayersImpl(playerContainer);
        tileObjectManager = new TileObjectsImpl(tileManager, client);
        tileItemManager = new TileItemsImpl(tileManager, client);
    }

    @Override
    public void destroy(EventBus eventBus) {
        eventBus.unregister(tileContainer);
        eventBus.unregister(npcContainer);
        eventBus.unregister(playerContainer);
    }
}
