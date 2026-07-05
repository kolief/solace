package net.solace.impl.items;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.commons.QuadFunction;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.items.ItemProvider;
import net.solace.api.widgets.IWidgets;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public abstract class ItemsImpl<T extends IItem> implements ItemProvider<T> {
    protected final IWidgets widgets;
    protected final IClient client;
    private final int inventoryId;
    private final QuadFunction<IWidgets, IClient, Item, Integer, T> mapper;
    private final T[] cache;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ItemsImpl(IWidgets widgets, IClient client, int inventoryId, Class<T> itemType, Class<T[]> arrayType,
                     QuadFunction<IWidgets, IClient, Item, Integer, T> mapper, int maxCapacity) {
        this.widgets = widgets;
        this.client = client;
        this.inventoryId = inventoryId;
        this.mapper = mapper;
        this.cache = arrayType.cast(Array.newInstance(itemType, maxCapacity));
    }

    @Override
    public List<T> getAll(Predicate<? super T> filter) {
        lock.readLock().lock();
        try {
            return Arrays.stream(cache)
                    .filter(Objects::nonNull)
                    .filter(filter)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public T get(int i) {
        lock.readLock().lock();
        try {
            return cache[i];
        } finally {
            lock.readLock().unlock();
        }
    }

    @Subscribe(priority = Integer.MAX_VALUE)
    private void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() != GameState.LOADING && e.getGameState() != GameState.LOGGED_IN) {
            Arrays.fill(cache, null);
        }
    }

    @Subscribe(priority = Integer.MAX_VALUE)
    private void onGameTick(GameTick e) {
        var startMs = System.currentTimeMillis();
        updateCache();
        log.trace("[ItemsImpl] onGameTick took {} ms", System.currentTimeMillis() - startMs);
    }

    protected void updateCache() {
        var itemContainer = client.getItemContainer(inventoryId);
        if (itemContainer == null) {
            Arrays.fill(cache, null);
            return;
        }

        var items = itemContainer.getItems();
        for (int i = 0; i < cache.length; i++) {
            if (i >= items.length) {
                continue;
            }

            var rlItem = items[i];
            if (rlItem == null || rlItem.getId() == -1) {
                cache[i] = null;
                continue;
            }

            var mapped = mapper.apply(widgets, client, rlItem, i);
            if (mapped != null) {
                var widget = getWidget(rlItem);
                if (widget != null) {
                    mapped.setWidget(widget);
                }

                cache[i] = mapped;
            }
        }
    }

    protected IWidget getWidget(Item item) {
        return null;
    }
}
