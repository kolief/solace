package net.solace.impl.containers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Player;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.containers.PlayerContainer;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.game.IClient;
import net.solace.impl.domain.actors.PlayerImpl;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

@Slf4j
public class PlayerContainerImpl extends ActorContainer<IPlayer, Player> implements PlayerContainer {
    @Getter
    private volatile IPlayer localPlayer;

    public PlayerContainerImpl(IClient client, int worldViewId) {
        super(client, worldViewId, Player.class);
    }

    @Override
    protected void updateCache() {
        var wrappedClient = client.getWrapped();
        var indexed = new IntObjectHashMap<Player>();
        var players = wrappedClient.getWorldView(worldViewId).players();
        for (var player : players) {
            indexed.put(player.getId(), player);
        }

        indexed.values().forEach(this::create);
        clearIndicesExcept(indexed.keySet());

        var hintArrowPlayer = wrappedClient.getHintArrowPlayer();
        hintArrowed = hintArrowPlayer != null ? get(hintArrowPlayer.getId()) : null;
        var localPlayer1 = wrappedClient.getLocalPlayer();
        localPlayer = localPlayer1 != null ? get(localPlayer1.getId()) : null;
    }

    @Override
    protected IPlayer createActor(Player rlActor) {
        return PlayerImpl.of(rlActor, client);
    }

    @Override
    protected void clearCache() {
        super.clearCache();
        localPlayer = null;
    }

    @Subscribe
    private void onPlayerSpawned(PlayerSpawned e) {
        var rlPlayer = e.getPlayer();
        if (checkWorldView(rlPlayer)) {
            var player = onSpawn(rlPlayer);
            client.getWrapped().getCallbacks().post(new net.solace.api.events.PlayerSpawned(player));
        }
    }

    @Subscribe
    private void onPlayerDespawned(PlayerDespawned e) {
        var rlPlayer = e.getPlayer();
        if (checkWorldView(rlPlayer)) {
            var player = onDespawn(rlPlayer);
            if (player != null) {
                client.getWrapped().getCallbacks().post(new net.solace.api.events.PlayerDespawned(player));
            } else {
                log.debug("Player despawned but it was not in cache: {}", rlPlayer.getName());
            }
        }
    }

    @Subscribe
    private void onPlayerChanged(PlayerChanged e) {
        var rlPlayer = e.getPlayer();
        if (checkWorldView(rlPlayer)) {
            var player = onChanged(rlPlayer);
            client.getWrapped().getCallbacks().post(new net.solace.api.events.PlayerChanged(player));
        }
    }
}
