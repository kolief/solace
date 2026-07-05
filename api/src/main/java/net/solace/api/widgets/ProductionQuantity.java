package net.solace.api.widgets;

import java.util.function.Supplier;
import net.solace.api.Static;
import net.solace.api.domain.widgets.IWidget;

public enum ProductionQuantity {
    ONE(() -> Static.getWidgets().get(17694727)),
    FIVE(() -> Static.getWidgets().get(17694728)),
    TEN(() -> Static.getWidgets().get(17694729)),
    X_SET(() -> Static.getWidgets().get(17694730)),
    X(() -> Static.getWidgets().get(17694731)),
    ALL(() -> Static.getWidgets().get(17694732));

    private final Supplier<IWidget> widgetSupplier;

    private ProductionQuantity(Supplier<IWidget> widgetSupplier) {
        this.widgetSupplier = widgetSupplier;
    }

    public boolean isVisible() {
        return Static.getWidgets().isVisible(this.widgetSupplier.get());
    }

    public boolean select() {
        IWidget widget = this.widgetSupplier.get();
        if (Static.getWidgets().isVisible(widget)) {
            widget.interact(0);
            return true;
        }
        return false;
    }

    public boolean isSelected() {
        IWidget widget = this.widgetSupplier.get();
        IWidget[] children = widget.getDynamicChildren();
        if (children == null || children.length == 0) {
            return false;
        }
        return children[0].getSpriteId() == -1;
    }

    public Supplier<IWidget> getWidgetSupplier() {
        return this.widgetSupplier;
    }
}

