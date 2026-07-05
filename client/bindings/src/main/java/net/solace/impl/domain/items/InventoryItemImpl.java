package net.solace.impl.domain.items;

import net.runelite.api.Item;
import net.runelite.api.MenuAction;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;

public class InventoryItemImpl extends ItemImpl implements IInventoryItem {
    public InventoryItemImpl(Item wrapped, int slot, IWidget widget, IClient client) {
        super(wrapped, slot, widget, client, InventoryID.INV);
    }

    @Override
    public void use() {
        use(false);
    }

    @Override
    public void use(boolean queued) {
        client.getWrapped().menuAction(
                getSlot(),
                getWidget().getId(),
                MenuAction.WIDGET_TARGET,
                0,
                getId(),
                "Automated",
                ""
        );
    }

    @Override
    public void useOn(IItem item, boolean queued) {
        item.interact(this);
    }

    @Override
    public void useOn(ITileObject tileObject, boolean queued) {
        tileObject.interact(this);
    }

    @Override
    public void useOn(IActor actor, boolean queued) {
        if (actor instanceof IPlayer) {
            ((IPlayer) actor).interact(this);
        } else if (actor instanceof INPC) {
            ((INPC) actor).interact(this);
        }
    }

    @Override
    public void useOn(IWidget widget, boolean queued) {
        widget.interact(this);
    }

    @Override
    public void useOn(ITileItem tileItem, boolean queued) {
        tileItem.interact(this);
    }

    @Override
    public void drop() {
        interact("Drop");
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, int actionIndex) {
        return MENU_FACTORY.item(getId(), getSlot(), getWidget().getId())
                .interactMethod(interactMethod)
                .actionIndex(actionIndex)
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, MenuAction opcode) {
        return MENU_FACTORY.item(getId(), getSlot(), getWidget().getId())
                .interactMethod(interactMethod)
                .opcode(opcode)
                .build(getClickPoint());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IInventoryItem)) return false;

        IInventoryItem other = (IInventoryItem) o;
        return getId() == other.getId()
               && getQuantity() == other.getQuantity()
               && getSlot() == other.getSlot();
    }
}
