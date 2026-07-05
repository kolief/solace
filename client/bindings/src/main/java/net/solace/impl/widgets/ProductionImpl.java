package net.solace.impl.widgets;

import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemComposition;
import net.runelite.client.util.Text;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.input.IKeyboard;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IProduction;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.ProductionQuantity;
import net.solace.api.widgets.WidgetGroup;

import java.util.Arrays;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class ProductionImpl implements IProduction {
    private final IWidgets Widgets;
    private final IKeyboard Keyboard;
    private final IDialog Dialog;
    private final IClient Client;

    @Override
    public boolean isOpen() {
        return Widgets.isVisible(WidgetGroup.MULTISKILL_MENU_GROUP_ID, 0);
    }

    @Override
    public void chooseOption(Predicate<String> option) {
        if (!isOpen()) {
            return;
        }

        IWidget optionsWidget = Widgets.get(WidgetGroup.MULTISKILL_MENU_GROUP_ID, 14);
        if (!Widgets.isVisible(optionsWidget)) {
            return;
        }

        int options = optionsWidget.getChildren() != null ? optionsWidget.getChildren().length : 1;
        for (int i = 0; i < options; i++) {
            IWidget currentOption = Widgets.get(WidgetGroup.MULTISKILL_MENU_GROUP_ID, 15 + i);
            if (currentOption != null && option.test(currentOption.getName())) {
                chooseOption(i + 1);
                return;
            }
        }
    }


    @Override
    public void selectOtherQuantity() {
        IWidget otherQuantity = Widgets.get(WidgetGroup.MULTISKILL_MENU_GROUP_ID, 11);
        if (Widgets.isVisible(otherQuantity)) {
            otherQuantity.interact(0);
        }
    }

    @Override
    public void chooseOption(int index) {
        if (isOpen()) {
            Keyboard.type(index);
        }
    }

    @Override
    public void choosePreviousOption() {
        if (isOpen()) {
            Keyboard.sendSpace();
        }
    }

    @Override
    public boolean isEnterInputOpen() {
        return Dialog.isEnterInputOpen();
    }

    @Override
    public void enterAmount(int amount) {
        Dialog.enterAmount(amount);
    }

    @Override
    public void enterName(String input) {
        Dialog.enterName(input);
    }

    @Override
    public void selectItem(String name) {
        IWidget widget = Widgets.get(WidgetGroup.MULTISKILL_MENU_GROUP_ID, w -> w.getName().contains(name));
        if (Widgets.isVisible(widget)) {
            widget.interact(0);
        }
    }

    @Override
    public void selectItem(int itemId) {
        var widget = Widgets.get(WidgetGroup.MULTISKILL_MENU_GROUP_ID, w ->
        {
            ItemComposition itemComposition = Client.getItemComposition(itemId);
            if (itemComposition != null) {
                return w.getName().contains(itemComposition.getName());
            }

            return false;
        });

        if (Widgets.isVisible(widget)) {
            widget.interact(0);
        }
    }

    @Override
    public int getMakeXQuantity() {
        IWidget widget = ProductionQuantity.X_SET.getWidgetSupplier().get();
        IWidget[] children = widget.getDynamicChildren();

        if (!Widgets.isVisible(widget) || children == null || children.length == 0) {
            return -1;
        }

        final String sanitized = Text.sanitize(children[9].getText());

        return Integer.parseInt(sanitized);
    }

    @Override
    public boolean selectMakeXQuantity(int quantity) {
        final int selectedQuantity = getMakeXQuantity();

        if (selectedQuantity == quantity) {
            return true;
        }

        if (isEnterInputOpen()) {
            Dialog.enterAmount(quantity);
            return true;
        }

        ProductionQuantity.X.select();
        return false;
    }

    @Override
    public ProductionQuantity getSelectedQuantity() {
        return Arrays.stream(ProductionQuantity.values())
                .filter(ProductionQuantity::isSelected)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean selectQuantity(ProductionQuantity quantity) {
        if (quantity != null) {
            return quantity.select();
        }

        return false;
    }
}
