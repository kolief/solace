package net.solace.impl.containers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.game.IClient;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.SynchronizedIntObjectMap;

import java.util.Collection;

@Slf4j
public abstract class ActorContainer<A extends IActor, RL extends Actor> {
    protected final IClient client;
    protected final int worldViewId;
    private final MutableIntObjectMap<A> cache;
    private final Class<RL> rlType;

    @Getter
    protected volatile A hintArrowed;

    protected ActorContainer(IClient client, int worldViewId, Class<RL> rlType) {
        this.client = client;
        this.worldViewId = worldViewId;
        this.rlType = rlType;
        this.cache = new SynchronizedIntObjectMap<>(new IntObjectHashMap<>());
    }

    public A get(int index) {
        return cache.get(index);
    }

    public Collection<A> getAll() {
        return cache.values();
    }

    public A create(RL rlActor) {
        var index = getActorIndex(rlActor);
        if (index == 0 && rlActor instanceof Player) {
            // localplayer is 0 when logging in so it dupes after login
            return null;
        }

        var actor = cache.get(index);
        if (actor == null) {
            actor = createActor(rlActor);
            cache.put(index, actor);
        } else {
            updateActor(actor, rlActor);
        }

        return actor;
    }

    protected A onSpawn(RL rlActor) {
        var index = getActorIndex(rlActor);
        var actor = create(rlActor);
        cache.put(index, actor);
        return actor;
    }

    protected A onDespawn(RL rlActor) {
        var index = getActorIndex(rlActor);
        var actor = get(index);
        cache.remove(index);

        return actor;
    }

    protected A onChanged(RL rlActor) {
        return create(rlActor);
    }

    protected void clearIndicesExcept(IntIterable indices) {
        cache.keySet().retainAll(indices);
    }

    protected void clearCache() {
        cache.clear();
        hintArrowed = null;
    }

    protected boolean checkWorldView(Actor rlActor) {
        return rlActor.getWorldView().getId() == worldViewId;
    }

    @Subscribe(priority = Integer.MAX_VALUE)
    private void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.LOGGED_IN || e.getGameState() == GameState.LOADING) {
            updateCache();
        } else {
            clearCache();
        }
    }

    @Subscribe(priority = Integer.MAX_VALUE)
    private void onGameTick(GameTick e) {
        var startMs = System.currentTimeMillis();
        updateCache();
        log.trace("[ActorContainer] onGameTick took {} ms", System.currentTimeMillis() - startMs);
    }

    @Subscribe
    private void onActorDeath(ActorDeath e) {
        var actor = e.getActor();
        if (rlType.isInstance(actor) && checkWorldView(actor)) {
            var cached = create(rlType.cast(actor));
            client.getWrapped().getCallbacks().post(new net.solace.api.events.ActorDeath(cached));
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged e) {
        var actor = e.getActor();
        if (rlType.isInstance(actor) && checkWorldView(actor)) {
            var cached = create(rlType.cast(actor));
            var animationChanged = new net.solace.api.events.AnimationChanged();
            animationChanged.setActor(cached);
            client.getWrapped().getCallbacks().post(animationChanged);
        }
    }

    @Subscribe
    private void onGraphicChanged(GraphicChanged e) {
        var actor = e.getActor();
        if (rlType.isInstance(actor) && checkWorldView(actor)) {
            var event = new net.solace.api.events.GraphicChanged();
            var cached = create(rlType.cast(actor));
            event.setActor(cached);
            client.getWrapped().getCallbacks().post(event);
        }
    }

    private int getActorIndex(RL rlActor) {
        if (rlActor instanceof Player) {
            return ((Player) rlActor).getId();
        }

        return ((NPC) rlActor).getIndex();
    }

    private void updateActor(A actor, RL rlActor) {
        if (actor instanceof IPlayer && rlActor instanceof Player) {
            ((IPlayer) actor).update(((Player) rlActor));
        } else if (actor instanceof INPC && rlActor instanceof NPC) {
            ((INPC) actor).update(((NPC) rlActor));
        }
    }

    protected abstract void updateCache();

    protected abstract A createActor(RL rlActor);
}
