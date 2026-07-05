package net.solace.impl.containers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.containers.NpcContainer;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.game.IClient;
import net.solace.impl.domain.actors.NPCImpl;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

@Slf4j
public class NpcContainerImpl extends ActorContainer<INPC, NPC> implements NpcContainer {
    private INPC follower;

    public NpcContainerImpl(IClient client, int worldViewId) {
        super(client, worldViewId, NPC.class);
    }

    @Override
    public INPC getFollower() {
        return follower;
    }

    @Override
    protected void updateCache() {
        var indexed = new IntObjectHashMap<NPC>();
        var npcs = client.getWrapped().getWorldView(worldViewId).npcs();
        for (var npc : npcs) {
            indexed.put(npc.getIndex(), npc);
        }

        indexed.values().forEach(this::create);
        clearIndicesExcept(indexed.keySet());

        var hintArrowNpc = client.getWrapped().getHintArrowNpc();
        hintArrowed = hintArrowNpc != null ? get(hintArrowNpc.getIndex()) : null;
        var followerNpc = client.getWrapped().getFollower();
        follower = followerNpc != null ? get(followerNpc.getIndex()) : null;
    }

    @Override
    protected INPC createActor(NPC rlActor) {
        return NPCImpl.of(rlActor, client);
    }

    @Override
    protected void clearCache() {
        super.clearCache();
        follower = null;
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned e) {
        var rlNpc = e.getNpc();
        if (checkWorldView(rlNpc)) {
            var npc = onSpawn(rlNpc);
            client.getWrapped().getCallbacks().post(new net.solace.api.events.NpcSpawned(npc));
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned e) {
        var rlNpc = e.getNpc();
        if (checkWorldView(rlNpc)) {
            var npc = onDespawn(rlNpc);
            if (npc != null) {
                client.getWrapped().getCallbacks().post(new net.solace.api.events.NpcDespawned(npc));
            } else {
                log.debug("Npc despawned but it was not in cache: {}", rlNpc.getName());
            }
        }
    }

    @Subscribe
    private void onNpcChanged(NpcChanged e) {
        var rlNpc = e.getNpc();
        if (checkWorldView(rlNpc)) {
            var npc = onChanged(rlNpc);
            client.getWrapped().getCallbacks().post(new net.solace.api.events.NpcChanged(npc, e.getOld()));
        }
    }
}
