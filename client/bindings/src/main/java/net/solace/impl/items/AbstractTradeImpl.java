package net.solace.impl.items;

import net.runelite.api.Item;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.impl.domain.items.InventoryItemImpl;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.IWidgets;

import java.util.Arrays;

public abstract class AbstractTradeImpl extends ItemsImpl<IItem> {
    private final int interfaceId;
    private final int childId;

    public AbstractTradeImpl(IWidgets widgets, IClient client, int inventoryId,
                             int interfaceId, int childId, int maxCapacity) {
        super(widgets, client, inventoryId, IItem.class, IItem[].class, AbstractTradeImpl::map, maxCapacity);
        this.interfaceId = interfaceId;
        this.childId = childId;
    }

    private static IInventoryItem map(IWidgets widgets, IClient client, Item item, int slot) {
        if (item == null) {
            return null;
        }

        return new InventoryItemImpl(item, slot, null, client);
    }

    protected IWidget getWidget(Item item) {
        var containerWidget = widgets.get(interfaceId, childId);
        if (containerWidget == null) {
            return null;
        }

        var children = containerWidget.getChildren();
        if (children == null) {
            return null;
        }

        return Arrays.stream(children)
                .filter(child -> child.getItemId() == item.getId())
                .findFirst()
                .orElse(null);
    }
}
