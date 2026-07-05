package net.solace.api.items;

import net.solace.api.items.GrandExchangeState;

public interface IGrandExchange {
    public GrandExchangeState getState();

    public boolean isOpen();

    public boolean isSearchingItem();
}

