package net.solace.impl.items;

import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.game.IClient;
import net.solace.api.items.ITradeOurs;
import net.solace.api.widgets.IWidgets;

public class TradeOursImpl extends AbstractTradeImpl implements ITradeOurs {
    public TradeOursImpl(IWidgets widgets, IClient client) {
        super(widgets, client, InventoryID.TRADEOFFER, InterfaceID.TRADEMAIN, 28, 28);
    }
}
