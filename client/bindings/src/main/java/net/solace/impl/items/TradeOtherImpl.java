package net.solace.impl.items;

import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.game.IClient;
import net.solace.api.items.ITradeOther;
import net.solace.api.widgets.IWidgets;

public class TradeOtherImpl extends AbstractTradeImpl implements ITradeOther {
    public TradeOtherImpl(IWidgets widgets, IClient client) {
        super(widgets, client, InventoryID.TRADEOFFER | 0x8000, InterfaceID.TRADEMAIN, 28, 28);
    }
}
