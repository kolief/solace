package net.solace.api.domain.items;

import net.solace.api.domain.Interactable;
import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.domain.widgets.IWidget;

public interface IInventoryItem
extends IItem {
    default public void use() {
        this.use(false);
    }

    public void use(boolean var1);

    default public void useOn(IItem item) {
        this.useOn(item, false);
    }

    public void useOn(IItem var1, boolean var2);

    default public void useOn(ITileObject tileObject) {
        this.useOn(tileObject, false);
    }

    public void useOn(ITileObject var1, boolean var2);

    default public void useOn(IActor actor) {
        this.useOn(actor, false);
    }

    public void useOn(IActor var1, boolean var2);

    default public void useOn(IWidget widget) {
        this.useOn(widget, false);
    }

    public void useOn(IWidget var1, boolean var2);

    default public void useOn(ITileItem tileItem) {
        this.useOn(tileItem, false);
    }

    public void useOn(ITileItem var1, boolean var2);

    default public void useOn(Interactable interactable) {
        this.useOn(interactable, false);
    }

    default public void useOn(Interactable interactable, boolean queued) {
        if (interactable instanceof IItem) {
            this.useOn((IItem)interactable, queued);
        } else if (interactable instanceof ITileObject) {
            this.useOn((ITileObject)interactable, queued);
        } else if (interactable instanceof IActor) {
            this.useOn((IActor)interactable, queued);
        } else if (interactable instanceof IWidget) {
            this.useOn((IWidget)interactable, queued);
        } else if (interactable instanceof ITileItem) {
            this.useOn((ITileItem)interactable, queued);
        } else {
            throw new IllegalArgumentException("Unsupported interactable type: " + interactable.getClass().getSimpleName());
        }
    }

    public void drop();
}

