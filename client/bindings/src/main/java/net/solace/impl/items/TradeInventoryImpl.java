package net.solace.impl.items;

import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.game.IClient;
import net.solace.api.items.ITradeInventory;
import net.solace.api.widgets.IWidgets;

public class TradeInventoryImpl extends AbstractTradeImpl implements ITradeInventory {
    public TradeInventoryImpl(IWidgets widgets, IClient client) {
        super(widgets, client, InventoryID.INV, InterfaceID.TRADESIDE, 0, 28);
    }
}
